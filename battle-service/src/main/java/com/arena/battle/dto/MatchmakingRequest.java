package com.arena.battle.dto;

import com.arena.common.enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchmakingRequest {

    private Difficulty preferredDifficulty;

    @Builder.Default
    private Boolean isRanked = true;
}