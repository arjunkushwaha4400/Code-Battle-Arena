package com.arena.common.dto;

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
public class ParticipantDTO {

    private UUID id;
    private UUID roomId;
    private UUID userId;
    private String username;
    private Integer rating;
    private Boolean isReady;
    private Boolean hasSubmitted;
    private Integer testCasesPassed;
    private LocalDateTime joinedAt;
}