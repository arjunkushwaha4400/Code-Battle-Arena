package com.arena.battle.client;

import com.arena.common.dto.ApiResponse;
import com.arena.common.dto.HintRequestDTO;
import com.arena.common.dto.HintResponseDTO;
import com.arena.common.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class AIServiceClient {

    private final RestClient restClient;

    public AIServiceClient(
            RestClient.Builder restClientBuilder,
            @Value("${services.ai-service.url:http://localhost:8083}") String aiServiceUrl) {
        this.restClient = restClientBuilder
                .baseUrl(aiServiceUrl)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @CircuitBreaker(name = "aiService", fallbackMethod = "getHintFallback")
    @Retry(name = "aiService")
    public HintResponseDTO getHint(HintRequestDTO request) {
        log.debug("Requesting hint for problem: {}", request.getProblemId());

        ApiResponse<HintResponseDTO> response = restClient.post()
                .uri("/api/v1/ai/hints")
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (response != null && response.isSuccess()) {
            return response.getData();
        }

        throw new ServiceUnavailableException("AI Service");
    }

    // Fallback method
    private HintResponseDTO getHintFallback(HintRequestDTO request, Throwable t) {
        log.warn("Fallback for getHint: {}", t.getMessage());
        return HintResponseDTO.builder()
                .hint("AI service is currently unavailable. Try breaking down the problem into smaller steps.")
                .hintLevel(request.getHintLevel())
                .hintsRemaining(0)
                .build();
    }
}