package com.arena.ai.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HintRequest {

    private UUID problemId;

    @NotBlank(message = "Problem description is required")
    private String problemDescription;

    private String currentCode;

    @Min(value = 1, message = "Hint level must be at least 1")
    @Max(value = 3, message = "Hint level must be at most 3")
    @Builder.Default
    private Integer hintLevel = 1;

    private String programmingLanguage;
}