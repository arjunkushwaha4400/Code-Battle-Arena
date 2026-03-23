package com.arena.battle.controller;

import com.arena.battle.dto.CreateProblemRequest;
import com.arena.battle.service.ProblemService;
import com.arena.common.dto.ApiResponse;
import com.arena.common.dto.PagedResponse;
import com.arena.common.dto.ProblemDTO;
import com.arena.common.enums.Difficulty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/problems")
@RequiredArgsConstructor
@Tag(name = "Problems", description = "Problem management APIs")
public class ProblemController {

    private final ProblemService problemService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new problem")
    public ResponseEntity<ApiResponse<ProblemDTO>> createProblem(
            @Valid @RequestBody CreateProblemRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        UUID createdBy = UUID.fromString(jwt.getSubject());
        ProblemDTO problem = problemService.createProblem(request, createdBy);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Problem created successfully", problem));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get problem by ID")
    public ResponseEntity<ApiResponse<ProblemDTO>> getProblemById(@PathVariable UUID id) {
        ProblemDTO problem = problemService.getProblemById(id);
        return ResponseEntity.ok(ApiResponse.success(problem));
    }

    @GetMapping
    @Operation(summary = "Get all problems")
    public ResponseEntity<ApiResponse<PagedResponse<ProblemDTO>>> getAllProblems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<ProblemDTO> problems = problemService.getAllProblems(page, size);
        return ResponseEntity.ok(ApiResponse.success(problems));
    }

    @GetMapping("/difficulty/{difficulty}")
    @Operation(summary = "Get problems by difficulty")
    public ResponseEntity<ApiResponse<PagedResponse<ProblemDTO>>> getProblemsByDifficulty(
            @PathVariable Difficulty difficulty,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        PagedResponse<ProblemDTO> problems = problemService.getProblemsByDifficulty(difficulty, page, size);
        return ResponseEntity.ok(ApiResponse.success(problems));
    }

    @GetMapping("/random")
    @Operation(summary = "Get random problem")
    public ResponseEntity<ApiResponse<ProblemDTO>> getRandomProblem(
            @RequestParam(required = false) Difficulty difficulty) {

        ProblemDTO problem = difficulty != null ?
                problemService.getRandomProblemByDifficulty(difficulty) :
                problemService.getRandomProblem();

        return ResponseEntity.ok(ApiResponse.success(problem));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate a problem")
    public ResponseEntity<ApiResponse<Void>> deactivateProblem(@PathVariable UUID id) {
        problemService.deactivateProblem(id);
        return ResponseEntity.ok(ApiResponse.success("Problem deactivated", null));
    }
}