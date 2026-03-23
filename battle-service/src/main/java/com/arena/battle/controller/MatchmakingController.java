package com.arena.battle.controller;

import com.arena.battle.client.UserServiceClient;
import com.arena.battle.dto.MatchmakingRequest;
import com.arena.battle.dto.MatchmakingResponse;
import com.arena.battle.service.MatchmakingService;
import com.arena.common.dto.ApiResponse;
import com.arena.common.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/matchmaking")
@RequiredArgsConstructor
@Tag(name = "Matchmaking", description = "Matchmaking APIs")
public class MatchmakingController {

    private final MatchmakingService matchmakingService;
    private final UserServiceClient userServiceClient;

    @PostMapping("/join")
    @Operation(summary = "Join matchmaking queue")
    public ResponseEntity<ApiResponse<MatchmakingResponse>> joinQueue(
            @RequestBody MatchmakingRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        UserDTO user = userServiceClient.getUserByKeycloakId(jwt.getSubject());
        MatchmakingResponse response = matchmakingService.joinQueue(user, request);

        return ResponseEntity.ok(ApiResponse.success("Joined matchmaking queue", response));
    }

    @PostMapping("/leave")
    @Operation(summary = "Leave matchmaking queue")
    public ResponseEntity<ApiResponse<MatchmakingResponse>> leaveQueue(
            @AuthenticationPrincipal Jwt jwt) {

        UserDTO user = userServiceClient.getUserByKeycloakId(jwt.getSubject());
        MatchmakingResponse response = matchmakingService.leaveQueue(user.getId());

        return ResponseEntity.ok(ApiResponse.success("Left matchmaking queue", response));
    }

    @GetMapping("/status")
    @Operation(summary = "Get matchmaking status")
    public ResponseEntity<ApiResponse<MatchmakingStatusResponse>> getStatus(
            @AuthenticationPrincipal Jwt jwt) {

        UserDTO user = userServiceClient.getUserByKeycloakId(jwt.getSubject());

        MatchmakingStatusResponse status = new MatchmakingStatusResponse(
                matchmakingService.isInQueue(user.getId()),
                matchmakingService.getQueueSize()
        );

        return ResponseEntity.ok(ApiResponse.success(status));
    }

    record MatchmakingStatusResponse(boolean inQueue, int queueSize) {}
}