package com.arena.battle.controller;

import com.arena.battle.client.UserServiceClient;
import com.arena.battle.dto.SubmissionResponse;
import com.arena.battle.dto.SubmitCodeRequest;
import com.arena.battle.service.SubmissionService;
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
@RequestMapping("/api/v1/submissions")
@RequiredArgsConstructor
@Tag(name = "Submissions", description = "Code submission APIs")
public class SubmissionController {

    private final SubmissionService submissionService;
    private final UserServiceClient userServiceClient;

    @PostMapping
    @Operation(summary = "Submit code for execution")
    public ResponseEntity<ApiResponse<SubmissionResponse>> submitCode(
            @Valid @RequestBody SubmitCodeRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        UserDTO user = userServiceClient.getUserByKeycloakId(jwt.getSubject());
        SubmissionResponse submission = submissionService.submitCode(request, user.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Code submitted successfully", submission));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get submission by ID")
    public ResponseEntity<ApiResponse<SubmissionResponse>> getSubmissionById(
            @PathVariable UUID id) {

        SubmissionResponse submission = submissionService.getSubmissionById(id);
        return ResponseEntity.ok(ApiResponse.success(submission));
    }

    @GetMapping("/room/{roomId}")
    @Operation(summary = "Get submissions by room")
    public ResponseEntity<ApiResponse<List<SubmissionResponse>>> getSubmissionsByRoom(
            @PathVariable UUID roomId) {

        List<SubmissionResponse> submissions = submissionService.getSubmissionsByRoom(roomId);
        return ResponseEntity.ok(ApiResponse.success(submissions));
    }
}