package com.arena.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // User Service
                .route("user-service", r -> r
                        .path("/api/v1/users/**", "/api/v1/leaderboard/**", "/api/v1/auth/**")
                        .filters(f -> f.stripPrefix(0))
                        .uri("http://localhost:8081")
                )

                // Battle Service
                .route("battle-service", r -> r
                        .path("/api/v1/rooms/**", "/api/v1/problems/**",
                                "/api/v1/submissions/**", "/api/v1/matchmaking/**")
                        .filters(f -> f.stripPrefix(0))
                        .uri("http://localhost:8082")
                )

                // WebSocket
                .route("battle-websocket", r -> r
                        .path("/ws/**")
                        .uri("ws://localhost:8082")
                )

                // AI Service
                .route("ai-service", r -> r
                        .path("/api/v1/ai/**")
                        .filters(f -> f.stripPrefix(0))
                        .uri("http://localhost:8083")
                )

                .build();
    }
}