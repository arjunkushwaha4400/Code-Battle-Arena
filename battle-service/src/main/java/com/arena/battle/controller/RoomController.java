package com.arena.battle.controller;

import com.arena.battle.client.UserServiceClient;
import com.arena.battle.dto.BattleRoomResponse;
import com.arena.battle.dto.CreateRoomRequest;
import com.arena.battle.dto.JoinRoomRequest;
import com.arena.battle.service.RoomService;
import com.arena.common.dto.ApiResponse;
import com.arena.common.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@Tag(name = "Rooms", description = "Battle room management APIs")
public class RoomController {

    private final RoomService roomService;
    private final UserServiceClient userServiceClient;

    @PostMapping
    @Operation(summary = "Create a new battle room")
    public ResponseEntity<ApiResponse<BattleRoomResponse>> createRoom(
            @Valid @RequestBody CreateRoomRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        UserDTO user = userServiceClient.getUserByKeycloakId(jwt.getSubject());
        BattleRoomResponse room = roomService.createRoom(request, user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Room created successfully", room));
    }

    @PostMapping("/join")
    @Operation(summary = "Join an existing room")
    public ResponseEntity<ApiResponse<BattleRoomResponse>> joinRoom(
            @Valid @RequestBody JoinRoomRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        UserDTO user = userServiceClient.getUserByKeycloakId(jwt.getSubject());
        BattleRoomResponse room = roomService.joinRoom(request.getRoomCode(), user);

        return ResponseEntity.ok(ApiResponse.success("Joined room successfully", room));
    }

    @PostMapping("/{roomId}/leave")
    @Operation(summary = "Leave a room")
    public ResponseEntity<ApiResponse<Void>> leaveRoom(
            @PathVariable UUID roomId,
            @AuthenticationPrincipal Jwt jwt) {

        UserDTO user = userServiceClient.getUserByKeycloakId(jwt.getSubject());
        roomService.leaveRoom(roomId, user.getId());

        return ResponseEntity.ok(ApiResponse.success("Left room successfully", null));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get room by ID")
    public ResponseEntity<ApiResponse<BattleRoomResponse>> getRoomById(@PathVariable UUID id) {
        BattleRoomResponse room = roomService.getRoomById(id);
        return ResponseEntity.ok(ApiResponse.success(room));
    }

    @GetMapping("/code/{roomCode}")
    @Operation(summary = "Get room by code")
    public ResponseEntity<ApiResponse<BattleRoomResponse>> getRoomByCode(
            @PathVariable String roomCode) {

        BattleRoomResponse room = roomService.getRoomByCode(roomCode);
        return ResponseEntity.ok(ApiResponse.success(room));
    }

    @GetMapping("/my-rooms")
    @Operation(summary = "Get current user's active rooms")
    public ResponseEntity<ApiResponse<List<BattleRoomResponse>>> getMyRooms(
            @AuthenticationPrincipal Jwt jwt) {

        UserDTO user = userServiceClient.getUserByKeycloakId(jwt.getSubject());
        List<BattleRoomResponse> rooms = roomService.getActiveRoomsByUser(user.getId());

        return ResponseEntity.ok(ApiResponse.success(rooms));
    }
}