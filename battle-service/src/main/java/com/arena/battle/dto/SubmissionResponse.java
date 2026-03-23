package com.arena.battle.dto;

import com.arena.common.enums.ProgrammingLanguage;
import com.arena.common.enums.SubmissionStatus;
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
public class SubmissionResponse {

    private UUID id;
    private UUID roomId;
    private UUID userId;
    private ProgrammingLanguage language;
    private SubmissionStatus status;
    private Integer executionTimeMs;
    private Integer memoryUsedKb;
    private Integer testCasesPassed;
    private Integer totalTestCases;
    private String errorMessage;
    private LocalDateTime submittedAt;
}