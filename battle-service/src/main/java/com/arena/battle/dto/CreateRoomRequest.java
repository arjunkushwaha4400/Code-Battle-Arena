package com.arena.battle.dto;

import com.arena.common.enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomRequest {

    private UUID problemId;

    private Difficulty difficulty;

    @Builder.Default
    private Integer maxPlayers = 2;

    @Builder.Default
    private Boolean isRanked = true;
}