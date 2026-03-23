package com.arena.user.controller;

import com.arena.common.dto.ApiResponse;
import com.arena.common.dto.PagedResponse;
import com.arena.common.dto.UserDTO;
import com.arena.user.dto.UpdateProfileRequest;
import com.arena.user.dto.UserProfileDTO;
import com.arena.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User management APIs")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getCurrentUser(
            @AuthenticationPrincipal Jwt jwt) {

        String keycloakId = jwt.getSubject();
        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");

        // Sync user if not exists
        UserDTO userDTO = userService.syncUser(keycloakId, username, email);

        // Get full profile
        UserProfileDTO profile = userService.getUserProfile(userDTO.getId());

        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable UUID id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping("/profile/{username}")
    @Operation(summary = "Get user profile by username")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getUserProfile(
            @PathVariable String username) {

        UserProfileDTO profile = userService.getUserProfileByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<ApiResponse<UserProfileDTO>> updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateProfileRequest request) {

        String keycloakId = jwt.getSubject();
        UserDTO userDTO = userService.getUserByKeycloakId(keycloakId);

        UserProfileDTO updatedProfile = userService.updateProfile(userDTO.getId(), request);

        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedProfile));
    }

    @GetMapping("/search")
    @Operation(summary = "Search users by username")
    public ResponseEntity<ApiResponse<PagedResponse<UserDTO>>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<UserDTO> users = userService.searchUsers(query, page, size);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
}