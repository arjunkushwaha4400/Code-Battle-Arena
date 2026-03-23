package com.arena.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedProblem {

    private String title;
    private String description;
    private String difficulty;
    private String inputFormat;
    private String outputFormat;
    private String constraints;
    private List<GeneratedTestCase> testCases;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeneratedTestCase {
        private String input;
        private String expectedOutput;
        private boolean isHidden;
        private String explanation;
    }
}