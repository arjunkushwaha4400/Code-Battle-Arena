package com.arena.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingUpdateRequest {

    @NotNull(message = "Winner ID is required")
    private UUID winnerId;

    @NotNull(message = "Loser ID is required")
    private UUID loserId;
}