package com.arena.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntryDTO {

    private Integer rank;
    private UUID userId;
    private String username;
    private String avatarUrl;
    private Integer rating;
    private Integer wins;
    private Integer losses;
    private Double winRate;
    private String rankTitle;
}