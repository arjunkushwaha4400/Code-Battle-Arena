package com.arena.common.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HintRequestDTO {

    @NotNull(message = "Problem ID is required")
    private UUID problemId;

    private String currentCode;

    @Min(value = 1, message = "Hint level must be at least 1")
    @Max(value = 3, message = "Hint level must be at most 3")
    private Integer hintLevel;
}