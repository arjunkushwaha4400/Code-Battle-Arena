package com.arena.battle.dto;

import com.arena.common.dto.ProblemDTO;
import com.arena.common.enums.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattleRoomResponse {

    private UUID id;
    private String roomCode;
    private ProblemDTO problem;
    private List<ParticipantResponse> participants;
    private RoomStatus status;
    private UUID winnerId;
    private Integer maxPlayers;
    private Boolean isRanked;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}