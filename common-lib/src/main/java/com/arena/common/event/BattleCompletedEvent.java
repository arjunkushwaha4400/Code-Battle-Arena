package com.arena.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattleCompletedEvent implements Serializable {

    private UUID roomId;
    private UUID winnerId;
    private UUID loserId;
    private Integer winnerRating;
    private Integer loserRating;
    private LocalDateTime completedAt;
}