package com.arena.battle.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthChannelInterceptor authChannelInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple broker for topics and queues
        config.enableSimpleBroker("/topic", "/queue");

        // Prefix for messages from clients
        config.setApplicationDestinationPrefixes("/app");

        // Prefix for user-specific messages
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/battle")
                .setAllowedOriginPatterns("http://localhost:*")
                .withSockJS();

        // Also add endpoint without SockJS for native WebSocket clients
        registry.addEndpoint("/ws/battle")
                .setAllowedOriginPatterns("http://localhost:*");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authChannelInterceptor);
    }
}