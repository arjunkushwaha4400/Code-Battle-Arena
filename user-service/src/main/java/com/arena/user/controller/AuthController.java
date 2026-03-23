package com.arena.user.controller;

import com.arena.common.dto.ApiResponse;
import com.arena.common.dto.UserDTO;
import com.arena.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication related APIs")
public class AuthController {

    private final UserService userService;

    @PostMapping("/sync")
    @Operation(summary = "Sync user from Keycloak after login")
    public ResponseEntity<ApiResponse<UserDTO>> syncUser(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");

        log.info("Syncing user after login: keycloakId={}, username={}", keycloakId, username);

        UserDTO user = userService.syncUser(keycloakId, username, email);

        return ResponseEntity.ok(ApiResponse.success("User synced successfully", user));
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate JWT token")
    public ResponseEntity<ApiResponse<TokenInfo>> validateToken(@AuthenticationPrincipal Jwt jwt) {
        TokenInfo tokenInfo = new TokenInfo(
                jwt.getSubject(),
                jwt.getClaimAsString("preferred_username"),
                jwt.getClaimAsString("email"),
                jwt.getExpiresAt() != null ? jwt.getExpiresAt().toString() : null
        );

        return ResponseEntity.ok(ApiResponse.success(tokenInfo));
    }

    record TokenInfo(String userId, String username, String email, String expiresAt) {}
}