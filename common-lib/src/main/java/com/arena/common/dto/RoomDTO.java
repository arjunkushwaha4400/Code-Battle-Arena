package com.arena.common.dto;

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
public class RoomDTO {

    private UUID id;
    private String roomCode;
    private ProblemDTO problem;
    private List<ParticipantDTO> participants;
    private RoomStatus status;
    private UUID winnerId;
    private Integer maxPlayers;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}