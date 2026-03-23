package com.arena.battle.client;

import com.arena.common.dto.ApiResponse;
import com.arena.common.dto.UserDTO;
import com.arena.common.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Slf4j
@Component
public class UserServiceClient {

    private final RestClient restClient;

    public UserServiceClient(
            RestClient.Builder restClientBuilder,
            @Value("${services.user-service.url:http://localhost:8081}") String userServiceUrl) {
        this.restClient = restClientBuilder
                .baseUrl(userServiceUrl)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getUserByIdFallback")
    @Retry(name = "userService")
    public UserDTO getUserById(UUID userId) {
        log.debug("Fetching user by ID: {}", userId);

        ApiResponse<UserDTO> response = restClient.get()
                .uri("/api/v1/internal/users/{id}", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (response != null && response.isSuccess()) {
            return response.getData();
        }

        throw new ServiceUnavailableException("User Service");
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getUserByKeycloakIdFallback")
    @Retry(name = "userService")
    public UserDTO getUserByKeycloakId(String keycloakId) {
        log.debug("Fetching user by Keycloak ID: {}", keycloakId);

        ApiResponse<UserDTO> response = restClient.get()
                .uri("/api/v1/internal/users/keycloak/{keycloakId}", keycloakId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (response != null && response.isSuccess()) {
            return response.getData();
        }

        throw new ServiceUnavailableException("User Service");
    }

    // Fallback methods
    private UserDTO getUserByIdFallback(UUID userId, Throwable t) {
        log.warn("Fallback for getUserById: {}", t.getMessage());
        // Return basic user info from cache or default
        return UserDTO.builder()
                .id(userId)
                .username("Unknown")
                .rating(1000)
                .build();
    }

    private UserDTO getUserByKeycloakIdFallback(String keycloakId, Throwable t) {
        log.warn("Fallback for getUserByKeycloakId: {}", t.getMessage());
        return UserDTO.builder()
                .keycloakId(keycloakId)
                .username("Unknown")
                .rating(1000)
                .build();
    }
}