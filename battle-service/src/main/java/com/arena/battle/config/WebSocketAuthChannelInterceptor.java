package com.arena.battle.config;

import com.arena.battle.client.UserServiceClient;
import com.arena.common.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtDecoder jwtDecoder;
    private final UserServiceClient userServiceClient;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                try {
                    Jwt jwt = jwtDecoder.decode(token);
                    String keycloakId = jwt.getSubject();
                    String username = jwt.getClaimAsString("preferred_username");

                    // Create authentication
                    WebSocketPrincipal principal = new WebSocketPrincipal(keycloakId, username);

                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );

                    accessor.setUser(auth);

                    log.info("WebSocket connected: user={}", username);

                } catch (Exception e) {
                    log.error("WebSocket authentication failed", e);
                    throw new IllegalArgumentException("Invalid token");
                }
            } else {
                log.warn("WebSocket connection without auth token");
                // Allow anonymous for now, handle in message handlers
            }
        }

        return message;
    }

    /**
     * Custom principal for WebSocket sessions
     */
    public record WebSocketPrincipal(String keycloakId, String username)
            implements java.security.Principal {

        @Override
        public String getName() {
            return keycloakId;
        }
    }
}