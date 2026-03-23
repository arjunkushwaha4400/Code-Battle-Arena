package com.arena.battle.service;

import com.arena.battle.client.UserServiceClient;
import com.arena.battle.dto.websocket.WsBattleEvent;
import com.arena.battle.entity.BattleRoom;
import com.arena.battle.repository.BattleRoomRepository;
import com.arena.common.dto.UserDTO;
import com.arena.common.enums.WebSocketMessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final BattleRoomRepository roomRepository;
    private final UserServiceClient userServiceClient;

    // Cache: database userId -> keycloakId
    private final Map<UUID, String> userKeycloakIdCache = new ConcurrentHashMap<>();

    /**
     * Notify specific user - resolves database userId to keycloakId for Spring's user destination
     */
    public void notifyUser(UUID userId, String eventType, Object payload) {
        log.debug("Notifying user {}: {}", userId, eventType);

        WebSocketMessageType type = WebSocketMessageType.valueOf(eventType);
        WsBattleEvent<?> event = WsBattleEvent.of(type, payload);

        String keycloakId = resolveKeycloakId(userId);
        if (keycloakId == null) {
            log.error("Cannot notify user {} - unable to resolve keycloakId", userId);
            return;
        }

        log.debug("Resolved userId {} to keycloakId {} for WebSocket delivery", userId, keycloakId);

        messagingTemplate.convertAndSendToUser(
                keycloakId,
                "/queue/events",
                event
        );
    }

    /**
     * Resolve database userId to keycloakId
     */
    private String resolveKeycloakId(UUID userId) {
        // Check cache first
        String cached = userKeycloakIdCache.get(userId);
        if (cached != null) {
            return cached;
        }

        try {
            UserDTO user = userServiceClient.getUserById(userId);
            if (user != null && user.getKeycloakId() != null) {
                userKeycloakIdCache.put(userId, user.getKeycloakId());
                return user.getKeycloakId();
            }
        } catch (Exception e) {
            log.error("Failed to resolve keycloakId for userId: {}", userId, e);
        }

        return null;
    }

    /**
     * Notify all participants in a room
     */
    public void notifyRoom(UUID roomId, String eventType, Object payload) {
        log.debug("Notifying room {}: {}", roomId, eventType);

        WebSocketMessageType type = WebSocketMessageType.valueOf(eventType);
        WsBattleEvent<?> event = WsBattleEvent.of(type, payload);

        messagingTemplate.convertAndSend(
                "/topic/room/" + roomId,
                event
        );
    }

    /**
     * Notify room participants (excluding sender)
     */
    public void notifyRoomParticipants(UUID roomId, String eventType, Object payload) {
        notifyRoom(roomId, eventType, payload);
    }

    /**
     * Send room update to all participants
     */
    public void sendRoomUpdate(UUID roomId) {
        roomRepository.findByIdWithParticipants(roomId).ifPresent(room -> {
            WsBattleEvent<?> event = WsBattleEvent.of(
                    WebSocketMessageType.PLAYER_JOINED,
                    room
            );

            messagingTemplate.convertAndSend(
                    "/topic/room/" + roomId,
                    event
            );
        });
    }

    /**
     * Send battle start countdown
     */
    public void sendCountdown(UUID roomId, int seconds) {
        WsBattleEvent<?> event = WsBattleEvent.of(
                WebSocketMessageType.COUNTDOWN,
                new CountdownPayload(seconds)
        );

        messagingTemplate.convertAndSend(
                "/topic/room/" + roomId,
                event
        );
    }

    /**
     * Send battle start event
     */
    public void sendBattleStart(UUID roomId, Object problemData) {
        WsBattleEvent<?> event = WsBattleEvent.of(
                WebSocketMessageType.BATTLE_START,
                problemData
        );

        messagingTemplate.convertAndSend(
                "/topic/room/" + roomId,
                event
        );
    }

    /**
     * Send error to user
     */
    public void sendError(UUID userId, String errorMessage) {
        WsBattleEvent<?> event = WsBattleEvent.of(
                WebSocketMessageType.ERROR,
                new ErrorPayload(errorMessage)
        );

        String keycloakId = resolveKeycloakId(userId);
        if (keycloakId == null) {
            log.error("Cannot send error to user {} - unable to resolve keycloakId", userId);
            return;
        }

        messagingTemplate.convertAndSendToUser(
                keycloakId,
                "/queue/events",
                event
        );
    }

    // Payload records
    record CountdownPayload(int seconds) {}
    record ErrorPayload(String message) {}
}