package com.arena.battle.websocket;

import com.arena.battle.client.UserServiceClient;
import com.arena.battle.config.WebSocketAuthChannelInterceptor.WebSocketPrincipal;
import com.arena.battle.entity.BattleRoom;
import com.arena.battle.entity.RoomParticipant;
import com.arena.battle.repository.BattleRoomRepository;
import com.arena.battle.service.WebSocketNotificationService;
import com.arena.common.dto.UserDTO;
import com.arena.common.enums.RoomStatus;
import com.arena.common.enums.WebSocketMessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class WebSocketEventListener {

    private final BattleRoomRepository roomRepository;
    private final WebSocketNotificationService notificationService;
    private final UserServiceClient userServiceClient;

    // sessionId -> keycloakId
    private final Map<String, String> sessionToKeycloakId = new ConcurrentHashMap<>();
    // keycloakId -> dbUserId
    private final Map<String, UUID> keycloakIdToUserId = new ConcurrentHashMap<>();

    // ✅ Track active keycloakId -> set of sessionIds (user can have multiple tabs)
    private final Map<String, Set<String>> keycloakIdToSessions = new ConcurrentHashMap<>();

    // ✅ Pending forfeit timers: userId -> scheduled future (so we can cancel on reconnect)
    private final Map<UUID, ScheduledFuture<?>> pendingForfeits = new ConcurrentHashMap<>();

    // ✅ Grace period before forfeiting — long enough for page refresh (~10s)
    private static final int DISCONNECT_GRACE_SECONDS = 12;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public WebSocketEventListener(
            BattleRoomRepository roomRepository,
            WebSocketNotificationService notificationService,
            UserServiceClient userServiceClient) {
        this.roomRepository = roomRepository;
        this.notificationService = notificationService;
        this.userServiceClient = userServiceClient;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        if (headerAccessor.getUser() instanceof UsernamePasswordAuthenticationToken auth) {
            if (auth.getPrincipal() instanceof WebSocketPrincipal principal) {
                String keycloakId = principal.keycloakId();

                // Track session -> keycloakId
                sessionToKeycloakId.put(sessionId, keycloakId);

                // ✅ Track keycloakId -> set of sessions (multi-tab support)
                keycloakIdToSessions
                        .computeIfAbsent(keycloakId, k -> ConcurrentHashMap.newKeySet())
                        .add(sessionId);

                log.info("User connected: {} (session: {})", principal.username(), sessionId);

                // Eagerly cache the DB userId
                try {
                    UserDTO user = userServiceClient.getUserByKeycloakId(keycloakId);
                    if (user != null) {
                        UUID userId = user.getId();
                        keycloakIdToUserId.put(keycloakId, userId);

                        // ✅ If this user had a pending forfeit timer, cancel it — they reconnected!
                        ScheduledFuture<?> pendingForfeit = pendingForfeits.remove(userId);
                        if (pendingForfeit != null && !pendingForfeit.isDone()) {
                            pendingForfeit.cancel(false);
                            log.info("User {} reconnected — cancelled pending forfeit timer", userId);
                        }
                    }
                } catch (Exception e) {
                    log.warn("Could not pre-cache userId for keycloakId {}: {}", keycloakId, e.getMessage());
                }
            }
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        String keycloakId = sessionToKeycloakId.remove(sessionId);
        if (keycloakId == null) {
            return; // session was never tracked (e.g. auth failed)
        }

        // ✅ Remove this session from the keycloakId -> sessions map
        Set<String> sessions = keycloakIdToSessions.get(keycloakId);
        if (sessions != null) {
            sessions.remove(sessionId);

            // ✅ If user still has other active sessions (other tabs), do NOT forfeit
            if (!sessions.isEmpty()) {
                log.info("User keycloakId={} disconnected session={} but has {} other active sessions — skipping forfeit",
                        keycloakId, sessionId, sessions.size());
                return;
            }

            // No more sessions — user is truly disconnected
            keycloakIdToSessions.remove(keycloakId);
        }

        UUID userId = keycloakIdToUserId.get(keycloakId); // ✅ don't remove yet — needed for reconnect check
        if (userId == null) {
            log.warn("Disconnected user keycloakId={} has no cached DB userId — skipping room cleanup", keycloakId);
            return;
        }

        log.info("User fully disconnected: keycloakId={} userId={} session={} — starting {}s grace period",
                keycloakId, userId, sessionId, DISCONNECT_GRACE_SECONDS);

        // ✅ Schedule forfeit after grace period instead of immediately
        scheduleGracefulDisconnect(keycloakId, userId);
    }

    /**
     * Schedule disconnect handling after a grace period.
     * If the user reconnects within the grace period, the timer is cancelled.
     */
    private void scheduleGracefulDisconnect(String keycloakId, UUID userId) {
        // Cancel any existing pending forfeit for this user (shouldn't happen, but safety)
        ScheduledFuture<?> existing = pendingForfeits.get(userId);
        if (existing != null && !existing.isDone()) {
            existing.cancel(false);
        }

        ScheduledFuture<?> future = scheduler.schedule(() -> {
            try {
                pendingForfeits.remove(userId);

                // ✅ Final check: is the user still disconnected after grace period?
                Set<String> remainingSessions = keycloakIdToSessions.get(keycloakId);
                if (remainingSessions != null && !remainingSessions.isEmpty()) {
                    log.info("User {} reconnected during grace period — no forfeit", userId);
                    return;
                }

                // User is still gone — clean up their rooms
                log.info("User {} still disconnected after {}s grace period — processing forfeit",
                        userId, DISCONNECT_GRACE_SECONDS);

                // ✅ Now remove from userId cache since we're sure they're gone
                keycloakIdToUserId.remove(keycloakId);

                handleDisconnectedUser(userId);

            } catch (Exception e) {
                log.error("Error in grace period disconnect handler for user {}: {}", userId, e.getMessage(), e);
            }
        }, DISCONNECT_GRACE_SECONDS, TimeUnit.SECONDS);

        pendingForfeits.put(userId, future);
    }

    /**
     * Clean up all active rooms the user was in when they disconnected.
     * - WAITING rooms: remove participant; cancel if empty.
     * - IN_PROGRESS rooms: schedule forfeit after grace period.
     */
    private void handleDisconnectedUser(UUID userId) {
        List<BattleRoom> activeRooms = roomRepository.findByUserIdAndStatusIn(
                userId,
                List.of(RoomStatus.WAITING, RoomStatus.IN_PROGRESS)
        );

        for (BattleRoom room : activeRooms) {
            try {
                handleRoomOnDisconnect(room, userId);
            } catch (Exception e) {
                log.error("Error handling disconnect for room {} user {}: {}",
                        room.getId(), userId, e.getMessage(), e);
            }
        }
    }

    private void handleRoomOnDisconnect(BattleRoom room, UUID disconnectedUserId) {
        // Reload fresh from DB with participants to avoid lazy-load issues
        BattleRoom fullRoom = roomRepository.findByIdWithParticipants(room.getId())
                .orElse(null);
        if (fullRoom == null) return;

        // ✅ Re-check status from DB — room may have already ended normally
        if (fullRoom.getStatus() == RoomStatus.WAITING) {
            handleWaitingRoomDisconnect(fullRoom, disconnectedUserId);
        } else if (fullRoom.getStatus() == RoomStatus.IN_PROGRESS) {
            handleInProgressRoomDisconnect(fullRoom, disconnectedUserId);
        } else {
            log.info("Room {} is already in status {} — skipping disconnect handling",
                    fullRoom.getRoomCode(), fullRoom.getStatus());
        }
    }

    @Transactional
    protected void handleWaitingRoomDisconnect(BattleRoom room, UUID disconnectedUserId) {
        RoomParticipant participant = room.getParticipant(disconnectedUserId);
        if (participant == null) return;

        room.removeParticipant(participant);

        if (room.getParticipants().isEmpty()) {
            room.setStatus(RoomStatus.CANCELLED);
            log.info("Room {} cancelled — last player disconnected", room.getRoomCode());
        } else {
            notificationService.notifyRoom(
                    room.getId(),
                    WebSocketMessageType.PLAYER_LEFT.name(),
                    new PlayerLeftPayload(disconnectedUserId, room.getRoomCode())
            );
            log.info("Player {} left waiting room {} on disconnect", disconnectedUserId, room.getRoomCode());
        }

        roomRepository.save(room);
    }

    @Transactional
    protected void handleInProgressRoomDisconnect(BattleRoom room, UUID disconnectedUserId) {
        // ✅ Re-fetch fresh from DB — room may have ended during grace period
        BattleRoom freshRoom = roomRepository.findByIdWithParticipants(room.getId())
                .orElse(null);
        if (freshRoom == null) return;

        // ✅ If room ended normally during grace period, do nothing
        if (freshRoom.getStatus() != RoomStatus.IN_PROGRESS) {
            log.info("Room {} status is now {} — skipping forfeit (resolved during grace period)",
                    freshRoom.getRoomCode(), freshRoom.getStatus());
            return;
        }

        freshRoom.setStatus(RoomStatus.ABANDONED);
        freshRoom.setEndedAt(LocalDateTime.now());

        freshRoom.getParticipants().stream()
                .filter(p -> !p.getUserId().equals(disconnectedUserId))
                .findFirst()
                .ifPresent(opponent -> {
                    freshRoom.setWinnerId(opponent.getUserId());

                    notificationService.notifyUser(
                            opponent.getUserId(),
                            WebSocketMessageType.BATTLE_END.name(),
                            new OpponentDisconnectedPayload(
                                    disconnectedUserId,
                                    opponent.getUserId(),
                                    freshRoom.getRoomCode(),
                                    "Opponent disconnected — you win!"
                            )
                    );

                    log.info("Room {} abandoned after grace period — user {} disconnected, opponent {} wins",
                            freshRoom.getRoomCode(), disconnectedUserId, opponent.getUserId());
                });

        roomRepository.save(freshRoom);
    }

    public boolean isUserConnected(String keycloakId) {
        Set<String> sessions = keycloakIdToSessions.get(keycloakId);
        return sessions != null && !sessions.isEmpty();
    }

    public int getConnectedUsersCount() {
        return sessionToKeycloakId.size();
    }

    // Payload records
    record PlayerLeftPayload(UUID userId, String roomCode) {}
    record OpponentDisconnectedPayload(UUID disconnectedUserId, UUID winnerId, String roomCode, String message) {}
}