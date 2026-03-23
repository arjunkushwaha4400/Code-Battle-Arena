package com.arena.common.event;

import com.arena.common.enums.ProgrammingLanguage;
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
public class CodeExecutionRequest implements Serializable {

    private UUID submissionId;
    private UUID roomId;
    private UUID userId;
    private String code;
    private ProgrammingLanguage language;
    private List<TestCaseInfo> testCases;
    private Integer timeLimitSeconds;
    private Integer memoryLimitMb;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestCaseInfo implements Serializable {
        private UUID testCaseId;
        private String input;
        private String expectedOutput;
        private Integer orderIndex;
    }
}