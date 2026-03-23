package com.arena.user.controller;

import com.arena.common.dto.ApiResponse;
import com.arena.common.dto.LeaderboardEntryDTO;
import com.arena.common.dto.PagedResponse;
import com.arena.user.service.LeaderboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leaderboard")
@RequiredArgsConstructor
@Tag(name = "Leaderboard", description = "Leaderboard APIs")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    @Operation(summary = "Get leaderboard by rating")
    public ResponseEntity<ApiResponse<PagedResponse<LeaderboardEntryDTO>>> getLeaderboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<LeaderboardEntryDTO> leaderboard =
                leaderboardService.getLeaderboardByRating(page, size);

        return ResponseEntity.ok(ApiResponse.success(leaderboard));
    }

    @GetMapping("/wins")
    @Operation(summary = "Get leaderboard by wins")
    public ResponseEntity<ApiResponse<PagedResponse<LeaderboardEntryDTO>>> getLeaderboardByWins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<LeaderboardEntryDTO> leaderboard =
                leaderboardService.getLeaderboardByWins(page, size);

        return ResponseEntity.ok(ApiResponse.success(leaderboard));
    }

    @GetMapping("/top")
    @Operation(summary = "Get top players")
    public ResponseEntity<ApiResponse<List<LeaderboardEntryDTO>>> getTopPlayers(
            @RequestParam(defaultValue = "10") int count) {

        List<LeaderboardEntryDTO> topPlayers = leaderboardService.getTopPlayers(count);

        return ResponseEntity.ok(ApiResponse.success(topPlayers));
    }
}