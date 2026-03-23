package com.arena.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeAnalysisDTO {

    private String timeComplexity;
    private String spaceComplexity;
    private Integer qualityScore;
    private List<String> suggestions;
    private String explanation;
}