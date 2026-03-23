package com.arena.common.event;

import com.arena.common.enums.SubmissionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeExecutionResult implements Serializable {

    private UUID submissionId;
    private UUID roomId;
    private UUID userId;
    private SubmissionStatus status;
    private Integer testCasesPassed;
    private Integer totalTestCases;
    private Integer executionTimeMs;
    private Integer memoryUsedKb;
    private String errorMessage;
    private List<TestCaseResult> testCaseResults;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestCaseResult implements Serializable {
        private UUID testCaseId;
        private Boolean passed;
        private String actualOutput;
        private String expectedOutput;
        private Integer executionTimeMs;
        private String error;
    }
}