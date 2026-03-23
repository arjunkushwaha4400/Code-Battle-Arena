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
public class CodeAnalysisRequest {

    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Programming language is required")
    private String language;

    private String problemDescription;
}