package com.arena.battle.service;

import com.arena.battle.dto.BattleRoomResponse;
import com.arena.battle.dto.CreateRoomRequest;
import com.arena.battle.dto.MatchmakingRequest;
import com.arena.battle.dto.MatchmakingResponse;
import com.arena.common.constants.AppConstants;
import com.arena.common.dto.UserDTO;
import com.arena.common.enums.Difficulty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchmakingService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RoomService roomService;
    private final WebSocketNotificationService notificationService;

    private static final String QUEUE_KEY = "matchmaking:queue";
    private static final String USER_QUEUE_KEY = "matchmaking:user:";

    // In-memory queue for simplicity (in production, use Redis sorted set)
    private final ConcurrentHashMap<UUID, QueueEntry> matchmakingQueue = new ConcurrentHashMap<>();

    /**
     * Join matchmaking queue
     */
    public MatchmakingResponse joinQueue(UserDTO user, MatchmakingRequest request) {
        log.info("User {} joining matchmaking queue", user.getUsername());

        // Check if already in queue
        if (matchmakingQueue.containsKey(user.getId())) {
            log.info("User {} already in queue", user.getUsername());
            QueueEntry existing = matchmakingQueue.get(user.getId());
            return MatchmakingResponse.builder()
                    .status("QUEUED")
                    .queuePosition(getQueuePosition(user.getId()))
                    .estimatedWaitSeconds(30)
                    .build();
        }

        // Add to queue
        QueueEntry entry = new QueueEntry(
                user.getId(),
                user.getUsername(),
                user.getRating() != null ? user.getRating() : 1000,
                request.getPreferredDifficulty(),
                request.getIsRanked(),
                Instant.now()
        );

        matchmakingQueue.put(user.getId(), entry);

        log.info("User {} added to queue. Queue size: {}", user.getUsername(), matchmakingQueue.size());

        return MatchmakingResponse.builder()
                .status("QUEUED")
                .queuePosition(getQueuePosition(user.getId()))
                .estimatedWaitSeconds(30)
                .build();
    }

    /**
     * Leave matchmaking queue
     */
    public MatchmakingResponse leaveQueue(UUID userId) {
        log.info("User {} leaving matchmaking queue", userId);

        matchmakingQueue.remove(userId);

        return MatchmakingResponse.builder()
                .status("CANCELLED")
                .build();
    }

    /**
     * Process matchmaking queue periodically
     */
    @Scheduled(fixedDelay = 2000) // Every 2 seconds
    public void processQueue() {
        if (matchmakingQueue.size() < 2) {
            return;
        }

        log.debug("Processing matchmaking queue. Size: {}", matchmakingQueue.size());

        List<QueueEntry> entries = new ArrayList<>(matchmakingQueue.values());
        entries.sort(Comparator.comparing(QueueEntry::joinedAt));

        Set<UUID> matchedUsers = new HashSet<>();

        for (int i = 0; i < entries.size(); i++) {
            QueueEntry player1 = entries.get(i);

            if (matchedUsers.contains(player1.userId())) {
                continue;
            }

            // Find suitable opponent
            QueueEntry opponent = findOpponent(player1, entries, matchedUsers);

            if (opponent != null) {
                // Create match
                createMatch(player1, opponent);
                matchedUsers.add(player1.userId());
                matchedUsers.add(opponent.userId());
            }
        }

        // Remove matched users from queue
        matchedUsers.forEach(matchmakingQueue::remove);
    }

    /**
     * Find suitable opponent for a player
     */
    private QueueEntry findOpponent(QueueEntry player, List<QueueEntry> entries, Set<UUID> matchedUsers) {
        long waitTime = Duration.between(player.joinedAt(), Instant.now()).toSeconds();

        // Expand rating range based on wait time
        int ratingRange = waitTime > 30 ?
                AppConstants.RATING_RANGE_EXPANDED :
                AppConstants.RATING_RANGE_INITIAL;

        for (QueueEntry candidate : entries) {
            if (candidate.userId().equals(player.userId())) {
                continue;
            }

            if (matchedUsers.contains(candidate.userId())) {
                continue;
            }

            // Check rating difference
            int ratingDiff = Math.abs(player.rating() - candidate.rating());
            if (ratingDiff <= ratingRange) {
                // Check difficulty preference match (if both specified)
                if (player.preferredDifficulty() != null &&
                        candidate.preferredDifficulty() != null &&
                        player.preferredDifficulty() != candidate.preferredDifficulty()) {
                    continue;
                }

                return candidate;
            }
        }

        return null;
    }

    /**
     * Create match between two players
     */
    private void createMatch(QueueEntry player1, QueueEntry player2) {
        log.info("Creating match between {} and {}", player1.username(), player2.username());

        try {
            // Determine difficulty
            Difficulty difficulty = player1.preferredDifficulty() != null ?
                    player1.preferredDifficulty() :
                    (player2.preferredDifficulty() != null ? player2.preferredDifficulty() : Difficulty.MEDIUM);

            // Create room request
            CreateRoomRequest request = CreateRoomRequest.builder()
                    .difficulty(difficulty)
                    .isRanked(player1.isRanked() && player2.isRanked())
                    .maxPlayers(2)
                    .build();

            // Create user DTO for player1
            UserDTO user1 = UserDTO.builder()
                    .id(player1.userId())
                    .username(player1.username())
                    .rating(player1.rating())
                    .build();

            // Create room with player1
            BattleRoomResponse room = roomService.createRoom(request, user1);

            // Add player2 to room
            UserDTO user2 = UserDTO.builder()
                    .id(player2.userId())
                    .username(player2.username())
                    .rating(player2.rating())
                    .build();

            room = roomService.joinRoom(room.getRoomCode(), user2);

            // Notify both players
            notifyMatchFound(player1.userId(), room);
            notifyMatchFound(player2.userId(), room);

            log.info("Match created! Room: {}", room.getRoomCode());

        } catch (Exception e) {
            log.error("Failed to create match", e);
            // Put players back in queue
            matchmakingQueue.put(player1.userId(), player1);
            matchmakingQueue.put(player2.userId(), player2);
        }
    }

    /**
     * Notify player that match was found
     */
    private void notifyMatchFound(UUID userId, BattleRoomResponse room) {
        MatchmakingResponse response = MatchmakingResponse.builder()
                .status("MATCHED")
                .roomId(room.getId())
                .roomCode(room.getRoomCode())
                .build();

        notificationService.notifyUser(userId, "MATCH_FOUND", response);
    }

    /**
     * Get queue position for a user
     */
    private int getQueuePosition(UUID userId) {
        List<QueueEntry> entries = new ArrayList<>(matchmakingQueue.values());
        entries.sort(Comparator.comparing(QueueEntry::joinedAt));

        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).userId().equals(userId)) {
                return i + 1;
            }
        }
        return 0;
    }

    /**
     * Check if user is in queue
     */
    public boolean isInQueue(UUID userId) {
        return matchmakingQueue.containsKey(userId);
    }

    /**
     * Get queue size
     */
    public int getQueueSize() {
        return matchmakingQueue.size();
    }

    // Queue entry record
    private record QueueEntry(
            UUID userId,
            String username,
            int rating,
            Difficulty preferredDifficulty,
            boolean isRanked,
            Instant joinedAt
    ) {}
}