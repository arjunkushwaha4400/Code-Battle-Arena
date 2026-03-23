package com.arena.battle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantResponse {

    private UUID id;
    private UUID userId;
    private String username;
    private Integer rating;
    private Boolean isReady;
    private Boolean hasSubmitted;
    private Integer testCasesPassed;
    private Integer hintsUsed;
    private LocalDateTime joinedAt;
}