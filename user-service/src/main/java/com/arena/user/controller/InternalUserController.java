package com.arena.user.controller;

import com.arena.common.dto.ApiResponse;
import com.arena.common.dto.UserDTO;
import com.arena.user.dto.RatingUpdateRequest;
import com.arena.user.service.RatingService;
import com.arena.user.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/internal/users")
@RequiredArgsConstructor
@Hidden // Hide from Swagger documentation
public class InternalUserController {

    private final UserService userService;
    private final RatingService ratingService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable UUID id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping("/keycloak/{keycloakId}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByKeycloakId(
            @PathVariable String keycloakId) {

        UserDTO user = userService.getUserByKeycloakId(keycloakId);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @PostMapping("/rating/update")
    public ResponseEntity<ApiResponse<RatingService.RatingUpdate>> updateRatings(
            @Valid @RequestBody RatingUpdateRequest request) {

        log.info("Updating ratings - Winner: {}, Loser: {}",
                request.getWinnerId(), request.getLoserId());

        RatingService.RatingUpdate update = ratingService.calculateAndUpdateRatings(
                request.getWinnerId(),
                request.getLoserId()
        );

        return ResponseEntity.ok(ApiResponse.success("Ratings updated successfully", update));
    }

    @GetMapping("/exists/{keycloakId}")
    public ResponseEntity<ApiResponse<Boolean>> checkUserExists(
            @PathVariable String keycloakId) {

        boolean exists = userService.existsByKeycloakId(keycloakId);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }
}