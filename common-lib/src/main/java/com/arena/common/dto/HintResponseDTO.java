package com.arena.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HintResponseDTO {

    private String hint;
    private Integer hintLevel;
    private Integer hintsRemaining;
}