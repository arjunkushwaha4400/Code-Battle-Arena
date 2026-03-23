package com.arena.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsDTO {

    private Integer totalBattles;
    private Integer problemsSolved;
    private Integer easySolved;
    private Integer mediumSolved;
    private Integer hardSolved;
    private Long averageSolveTimeMs;
    private String preferredLanguage;
    private Integer currentStreak;
    private Integer maxStreak;
    private Integer hintsUsed;
}