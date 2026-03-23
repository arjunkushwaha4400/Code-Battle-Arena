package com.arena.common.dto;

import com.arena.common.enums.ProgrammingLanguage;
import com.arena.common.enums.SubmissionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionDTO {

    private UUID id;

    @NotNull(message = "Room ID is required")
    private UUID roomId;

    private UUID userId;

    @NotBlank(message = "Code is required")
    private String code;

    @NotNull(message = "Language is required")
    private ProgrammingLanguage language;

    private SubmissionStatus status;

    private Integer executionTimeMs;

    private Integer memoryUsedKb;

    private Integer testCasesPassed;

    private Integer totalTestCases;

    private String errorMessage;

    private LocalDateTime submittedAt;
}