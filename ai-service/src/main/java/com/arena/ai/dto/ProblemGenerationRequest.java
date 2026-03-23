package com.arena.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemGenerationRequest {

    @NotBlank(message = "Difficulty is required")
    private String difficulty;

    private String topic;

    @Builder.Default
    private Integer numberOfTestCases = 5;
}