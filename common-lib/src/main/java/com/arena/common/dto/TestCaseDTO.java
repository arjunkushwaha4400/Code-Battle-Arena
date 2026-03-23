package com.arena.common.dto;

import jakarta.validation.constraints.NotBlank;
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
public class TestCaseDTO {

    private UUID id;

    @NotBlank(message = "Input is required")
    private String input;

    @NotBlank(message = "Expected output is required")
    private String expectedOutput;

    @NotNull(message = "Hidden flag is required")
    private Boolean isHidden;

    private Integer orderIndex;
}