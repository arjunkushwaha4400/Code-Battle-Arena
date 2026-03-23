package com.arena.battle.websocket;

import com.arena.battle.client.AIServiceClient;
import com.arena.battle.client.UserServiceClient;
import com.arena.battle.config.WebSocketAuthChannelInterceptor.WebSocketPrincipal;
import com.arena.battle.dto.BattleRoomResponse;
import com.arena.battle.dto.SubmissionResponse;
import com.arena.battle.dto.SubmitCodeRequest;
import com.arena.battle.dto.websocket.*;
import com.arena.battle.service.RoomService;
import com.arena.battle.service.SubmissionService;
import com.arena.battle.service.WebSocketNotificationService;
import com.arena.common.constants.AppConstants;
import com.arena.common.dto.HintRequestDTO;
import com.arena.common.dto.HintResponseDTO;
import com.arena.common.dto.UserDTO;
import com.arena.common.enums.WebSocketMessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BattleWebSocketController {

    private final RoomService roomService;
    private final SubmissionService submissionService;
    private final UserServiceClient userServiceClient;
    private final AIServiceClient aiServiceClient;
    private final WebSocketNotificationService notificationService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    /**
     * Handle join room request
     */
    @MessageMapping("/room/join")
    @SendToUser("/queue/events")
    public WsBattleEvent<?> handleJoinRoom(
            @Payload WsJoinRoomMessage message,
            Principal principal) {

        log.info("Join room request: {}", message.getRoomCode());

        try {
            UserDTO user = getUserFromPrincipal(principal);
            BattleRoomResponse room = roomService.joinRoom(message.getRoomCode(), user);

            // Notify other participants
            notificationService.notifyRoom(
                    room.getId(),
                    "PLAYER_JOINED",
                    room
            );

            return WsBattleEvent.of(WebSocketMessageType.JOIN_ROOM, room);

        } catch (Exception e) {
            log.error("Failed to join room", e);
            return WsBattleEvent.of(WebSocketMessageType.ERROR, e.getMessage());
        }
    }

    /**
     * Handle player ready status
     */
    @MessageMapping("/room/ready")
    public void handleReady(
            @Payload WsReadyMessage message,
            Principal principal) {

        log.info("Ready status: roomId={}, isReady={}", message.getRoomId(), message.getIsReady());

        try {
            UserDTO user = getUserFromPrincipal(principal);
            BattleRoomResponse room = roomService.setPlayerReady(
                    message.getRoomId(),
                    user.getId(),
                    message.getIsReady()
            );

            // Notify all participants
            notificationService.notifyRoom(
                    room.getId(),
                    "PLAYER_READY",
                    room
            );

            // Check if all players are ready
            if (room.getParticipants().stream().allMatch(p -> p.getIsReady()) &&
                    room.getParticipants().size() >= 2) {

                // Start countdown
                startBattleCountdown(room.getId());
            }

        } catch (Exception e) {
            log.error("Failed to set ready status", e);
            UserDTO user = getUserFromPrincipal(principal);
            notificationService.sendError(user.getId(), e.getMessage());
        }
    }

    /**
     * Handle code submission
     */
    @MessageMapping("/room/submit")
    @SendToUser("/queue/events")
    public WsBattleEvent<?> handleSubmit(
            @Payload WsSubmitMessage message,
            Principal principal) {

        log.info("Code submission: roomId={}", message.getRoomId());

        try {
            UserDTO user = getUserFromPrincipal(principal);

            SubmitCodeRequest request = SubmitCodeRequest.builder()
                    .roomId(message.getRoomId())
                    .code(message.getCode())
                    .language(message.getLanguage())
                    .build();

            SubmissionResponse submission = submissionService.submitCode(request, user.getId());

            return WsBattleEvent.of(WebSocketMessageType.CODE_SUBMITTED, submission);

        } catch (Exception e) {
            log.error("Failed to submit code", e);
            return WsBattleEvent.of(WebSocketMessageType.ERROR, e.getMessage());
        }
    }

    /**
     * Handle hint request
     */
    @MessageMapping("/room/hint")
    @SendToUser("/queue/events")
    public WsBattleEvent<?> handleHintRequest(
            @Payload WsHintRequestMessage message,
            Principal principal) {

        log.info("Hint request: roomId={}, level={}", message.getRoomId(), message.getHintLevel());

        try {
            UserDTO user = getUserFromPrincipal(principal);

            // Check hints remaining
            BattleRoomResponse room = roomService.getRoomById(message.getRoomId());
            var participant = room.getParticipants().stream()
                    .filter(p -> p.getUserId().equals(user.getId()))
                    .findFirst()
                    .orElseThrow();

            if (participant.getHintsUsed() >= AppConstants.MAX_HINT_COUNT) {
                return WsBattleEvent.of(
                        WebSocketMessageType.ERROR,
                        "No hints remaining"
                );
            }

            // Get problem description
            String problemDescription = room.getProblem().getDescription();

            // Request hint from AI service
            HintRequestDTO hintRequest = HintRequestDTO.builder()
                    .problemId(room.getProblem().getId())
                    .currentCode(message.getCurrentCode())
                    .hintLevel(message.getHintLevel())
                    .build();

            HintResponseDTO hintResponse = aiServiceClient.getHint(hintRequest);
            hintResponse.setHintsRemaining(AppConstants.MAX_HINT_COUNT - participant.getHintsUsed() - 1);

            // Update hints used
            roomService.incrementHintsUsed(message.getRoomId(), user.getId());

            return WsBattleEvent.of(WebSocketMessageType.HINT_RESPONSE, hintResponse);

        } catch (Exception e) {
            log.error("Failed to get hint", e);
            return WsBattleEvent.of(WebSocketMessageType.ERROR, e.getMessage());
        }
    }

    /**
     * Handle chat message
     */
    @MessageMapping("/room/chat")
    public void handleChat(
            @Payload WsChatMessage message,
            Principal principal) {

        try {
            UserDTO user = getUserFromPrincipal(principal);

            ChatPayload payload = new ChatPayload(
                    user.getId(),
                    user.getUsername(),
                    message.getMessage()
            );

            notificationService.notifyRoom(
                    message.getRoomId(),
                    "CHAT_MESSAGE",
                    payload
            );

        } catch (Exception e) {
            log.error("Failed to send chat message", e);
        }
    }

    /**
     * Handle leave room
     */
    @MessageMapping("/room/leave")
    public void handleLeaveRoom(
            @Payload UUID roomId,
            Principal principal) {

        try {
            UserDTO user = getUserFromPrincipal(principal);
            roomService.leaveRoom(roomId, user.getId());

            notificationService.notifyRoom(
                    roomId,
                    "PLAYER_LEFT",
                    new PlayerLeftPayload(user.getId(), user.getUsername())
            );

        } catch (Exception e) {
            log.error("Failed to leave room", e);
        }
    }

    /**
     * Start battle countdown
     */
    private void startBattleCountdown(UUID roomId) {
        log.info("Starting countdown for room: {}", roomId);

        // Send countdown: 3, 2, 1
        for (int i = AppConstants.BATTLE_COUNTDOWN_SECONDS; i > 0; i--) {
            final int seconds = i;
            scheduler.schedule(
                    () -> notificationService.sendCountdown(roomId, seconds),
                    (AppConstants.BATTLE_COUNTDOWN_SECONDS - i) * 1000L,
                    TimeUnit.MILLISECONDS
            );
        }

        // Start battle after countdown
        scheduler.schedule(() -> {
            try {
                BattleRoomResponse room = roomService.startBattle(roomId);

                // Send battle start with problem
                notificationService.sendBattleStart(roomId, room.getProblem());

                log.info("Battle started in room: {}", roomId);

            } catch (Exception e) {
                log.error("Failed to start battle", e);
            }
        }, AppConstants.BATTLE_COUNTDOWN_SECONDS * 1000L, TimeUnit.MILLISECONDS);
    }

    /**
     * Get user from principal
     */
    private UserDTO getUserFromPrincipal(Principal principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken auth) {
            if (auth.getPrincipal() instanceof WebSocketPrincipal wsPrincipal) {
                return userServiceClient.getUserByKeycloakId(wsPrincipal.keycloakId());
            }
        }
        throw new IllegalStateException("Invalid principal");
    }

    // Payload records
    record ChatPayload(UUID userId, String username, String message) {}
    record PlayerLeftPayload(UUID userId, String username) {}
}