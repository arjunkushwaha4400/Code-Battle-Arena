package com.arena.battle.dto;

import com.arena.common.enums.ProgrammingLanguage;
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
public class SubmitCodeRequest {

    @NotNull(message = "Room ID is required")
    private UUID roomId;

    @NotBlank(message = "Code is required")
    private String code;

    @NotNull(message = "Language is required")
    private ProgrammingLanguage language;
}