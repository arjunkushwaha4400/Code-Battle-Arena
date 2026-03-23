package com.arena.battle.dto;

import com.arena.common.enums.Difficulty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProblemRequest {

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

    private String starterCodeJava;

    private String starterCodePython;

    private String starterCodeJavascript;

    @NotEmpty(message = "At least one test case is required")
    @Valid
    private List<CreateTestCaseRequest> testCases;
}