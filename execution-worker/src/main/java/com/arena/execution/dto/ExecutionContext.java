package com.arena.execution.dto;

import com.arena.common.enums.ProgrammingLanguage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionContext {

    private UUID submissionId;
    private String code;
    private ProgrammingLanguage language;
    private int timeLimitSeconds;
    private int memoryLimitMb;
    private String containerId;
    private String workDir;
}