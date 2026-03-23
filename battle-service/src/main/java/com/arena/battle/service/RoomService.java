package com.arena.battle.service;

import com.arena.battle.dto.BattleRoomResponse;
import com.arena.battle.dto.CreateRoomRequest;
import com.arena.battle.entity.BattleRoom;
import com.arena.battle.entity.Problem;
import com.arena.battle.entity.RoomParticipant;
import com.arena.battle.mapper.RoomMapper;
import com.arena.battle.repository.BattleRoomRepository;
import com.arena.battle.repository.RoomParticipantRepository;
import com.arena.common.dto.UserDTO;
import com.arena.common.enums.RoomStatus;
import com.arena.common.exception.BadRequestException;
import com.arena.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final BattleRoomRepository roomRepository;
    private final RoomParticipantRepository participantRepository;
    private final ProblemService problemService;
    private final RoomMapper roomMapper;

    private static final String ROOM_CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ROOM_CODE_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    /**
     * Create a new battle room
     */
    @Transactional
    public BattleRoomResponse createRoom(CreateRoomRequest request, UserDTO creator) {
        log.info("Creating room for user: {}", creator.getUsername());

        // Get problem
        Problem problem;
        if (request.getProblemId() != null) {
            problem = problemService.getProblemEntityById(request.getProblemId());
        } else if (request.getDifficulty() != null) {
            problem = problemService.getProblemEntityById(
                    problemService.getRandomProblemByDifficulty(request.getDifficulty()).getId());
        } else {
            problem = problemService.getProblemEntityById(
                    problemService.getRandomProblem().getId());
        }

        // Generate unique room code
        String roomCode = generateUniqueRoomCode();

        // Create room
        BattleRoom room = BattleRoom.builder()
                .roomCode(roomCode)
                .problem(problem)
                .maxPlayers(request.getMaxPlayers() != null ? request.getMaxPlayers() : 2)
                .isRanked(request.getIsRanked() != null ? request.getIsRanked() : true)
                .status(RoomStatus.WAITING)
                .build();

        // Add creator as participant
        RoomParticipant participant = RoomParticipant.builder()
                .userId(creator.getId())
                .username(creator.getUsername())
                .rating(creator.getRating() != null ? creator.getRating() : 1000)
                .isReady(false)
                .build();

        room.addParticipant(participant);

        BattleRoom savedRoom = roomRepository.save(room);
        log.info("Created room with code: {}", roomCode);

        return roomMapper.toResponse(savedRoom);
    }

    /**
     * Join an existing room
     */
    @Transactional
    public BattleRoomResponse joinRoom(String roomCode, UserDTO user) {
        log.info("User {} joining room: {}", user.getUsername(), roomCode);

        BattleRoom room = roomRepository.findByRoomCodeWithDetails(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException("Room", roomCode));

        // Check if room is available
        if (room.getStatus() != RoomStatus.WAITING) {
            throw new BadRequestException("Room is not available for joining");
        }

        if (room.isFull()) {
            throw new BadRequestException("Room is full");
        }

        // Check if user already in room
        if (room.hasParticipant(user.getId())) {
            throw new BadRequestException("You are already in this room");
        }

        // Add participant
        RoomParticipant participant = RoomParticipant.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .rating(user.getRating() != null ? user.getRating() : 1000)
                .isReady(false)
                .build();

        room.addParticipant(participant);

        BattleRoom savedRoom = roomRepository.save(room);
        log.info("User {} joined room: {}", user.getUsername(), roomCode);

        return roomMapper.toResponse(savedRoom);
    }

    /**
     * Leave a room
     */
    @Transactional
    public void leaveRoom(UUID roomId, UUID userId) {
        log.info("User {} leaving room: {}", userId, roomId);

        BattleRoom room = roomRepository.findByIdWithParticipants(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", roomId.toString()));

        if (room.getStatus() == RoomStatus.IN_PROGRESS) {
            throw new BadRequestException("Cannot leave a room in progress");
        }

        RoomParticipant participant = room.getParticipant(userId);
        if (participant == null) {
            throw new BadRequestException("You are not in this room");
        }

        room.removeParticipant(participant);

        // If room is empty, cancel it
        if (room.getParticipants().isEmpty()) {
            room.setStatus(RoomStatus.CANCELLED);
        }

        roomRepository.save(room);
        log.info("User {} left room: {}", userId, roomId);
    }

    /**
     * Set player ready status
     */
    @Transactional
    public BattleRoomResponse setPlayerReady(UUID roomId, UUID userId, boolean isReady) {
        log.info("Setting ready status for user {} in room {}: {}", userId, roomId, isReady);

        BattleRoom room = roomRepository.findByIdWithParticipants(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", roomId.toString()));

        if (room.getStatus() != RoomStatus.WAITING) {
            throw new BadRequestException("Cannot change ready status - room is not in waiting state");
        }

        RoomParticipant participant = room.getParticipant(userId);
        if (participant == null) {
            throw new BadRequestException("You are not in this room");
        }

        participant.setIsReady(isReady);
        roomRepository.save(room);

        return roomMapper.toResponse(room);
    }

    /**
     * Start battle when all players are ready
     */
    @Transactional
    public BattleRoomResponse startBattle(UUID roomId) {
        log.info("Starting battle in room: {}", roomId);

        BattleRoom room = roomRepository.findByIdWithParticipants(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", roomId.toString()));

        if (room.getStatus() != RoomStatus.WAITING) {
            throw new BadRequestException("Room is not in waiting state");
        }

        if (!room.allParticipantsReady()) {
            throw new BadRequestException("Not all participants are ready");
        }

        room.setStatus(RoomStatus.IN_PROGRESS);
        room.setStartedAt(LocalDateTime.now());

        BattleRoom savedRoom = roomRepository.save(room);
        log.info("Battle started in room: {}", roomId);

        return roomMapper.toResponse(savedRoom);
    }

    /**
     * End battle with winner
     */
    @Transactional
    public BattleRoomResponse endBattle(UUID roomId, UUID winnerId) {
        log.info("Ending battle in room {} with winner: {}", roomId, winnerId);

        BattleRoom room = roomRepository.findByIdWithParticipants(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", roomId.toString()));

        room.setStatus(RoomStatus.COMPLETED);
        room.setWinnerId(winnerId);
        room.setEndedAt(LocalDateTime.now());

        BattleRoom savedRoom = roomRepository.save(room);
        log.info("Battle ended in room: {}", roomId);

        return roomMapper.toResponse(savedRoom);
    }

    /**
     * Get room by ID
     */
    @Transactional(readOnly = true)
    public BattleRoomResponse getRoomById(UUID id) {
        BattleRoom room = roomRepository.findByIdWithParticipants(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", id.toString()));
        return roomMapper.toResponse(room);
    }

    /**
     * Get room by code
     */
    @Transactional(readOnly = true)
    public BattleRoomResponse getRoomByCode(String roomCode) {
        BattleRoom room = roomRepository.findByRoomCodeWithDetails(roomCode)
                .orElseThrow(() -> new ResourceNotFoundException("Room", roomCode));
        return roomMapper.toResponse(room);
    }

    /**
     * Get rooms by user
     */
    @Transactional(readOnly = true)
    public List<BattleRoomResponse> getActiveRoomsByUser(UUID userId) {
        List<BattleRoom> rooms = roomRepository.findByUserIdAndStatusIn(
                userId,
                List.of(RoomStatus.WAITING, RoomStatus.IN_PROGRESS)
        );
        return roomMapper.toResponseList(rooms);
    }

    /**
     * Get room entity for internal use
     */
    @Transactional(readOnly = true)
    public BattleRoom getRoomEntityById(UUID id) {
        return roomRepository.findByIdWithParticipants(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", id.toString()));
    }

    /**
     * Update participant submission status
     */
    @Transactional
    public void updateParticipantSubmission(UUID roomId, UUID userId, int testCasesPassed, long submissionTimeMs) {
        RoomParticipant participant = participantRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));

        participant.setHasSubmitted(true);
        participant.setTestCasesPassed(testCasesPassed);
        participant.setSubmissionTimeMs(submissionTimeMs);

        participantRepository.save(participant);
    }

    /**
     * Increment hints used for participant
     */
    @Transactional
    public void incrementHintsUsed(UUID roomId, UUID userId) {
        RoomParticipant participant = participantRepository.findByRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));

        participant.setHintsUsed(participant.getHintsUsed() + 1);
        participantRepository.save(participant);
    }

    // Helper method to generate unique room code
    private String generateUniqueRoomCode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder(ROOM_CODE_LENGTH);
            for (int i = 0; i < ROOM_CODE_LENGTH; i++) {
                sb.append(ROOM_CODE_CHARS.charAt(random.nextInt(ROOM_CODE_CHARS.length())));
            }
            code = sb.toString();
        } while (roomRepository.existsByRoomCode(code));

        return code;
    }
}