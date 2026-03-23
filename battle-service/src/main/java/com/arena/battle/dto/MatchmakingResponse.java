package com.arena.battle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchmakingResponse {

    private String status; // QUEUED, MATCHED, CANCELLED
    private UUID roomId;
    private String roomCode;
    private Integer queuePosition;
    private Integer estimatedWaitSeconds;
}