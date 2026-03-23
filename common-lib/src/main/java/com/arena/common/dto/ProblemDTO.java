package com.arena.common.dto;

import com.arena.common.enums.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemDTO {

    private UUID id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty;

    @Positive(message = "Time limit must be positive")
    private Integer timeLimitSeconds;

    @Positive(message = "Memory limit must be positive")
    private Integer memoryLimitMb;

    private String inputFormat;

    private String outputFormat;

    private String constraints;

    private List<TestCaseDTO> sampleTestCases;

    private LocalDateTime createdAt;
}