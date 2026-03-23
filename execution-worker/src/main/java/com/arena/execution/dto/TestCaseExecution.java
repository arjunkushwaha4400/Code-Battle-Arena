package com.arena.execution.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseExecution {

    private UUID testCaseId;
    private String input;
    private String expectedOutput;
    private String actualOutput;
    private boolean passed;
    private int executionTimeMs;
    private String error;
}