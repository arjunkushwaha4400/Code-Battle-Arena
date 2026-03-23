package com.arena.ai.controller;

import com.arena.ai.dto.CodeAnalysisRequest;
import com.arena.ai.dto.GeneratedProblem;
import com.arena.ai.dto.HintRequest;
import com.arena.ai.dto.ProblemGenerationRequest;
import com.arena.ai.service.CodeAnalysisService;
import com.arena.ai.service.HintService;
import com.arena.ai.service.ProblemGenerationService;
import com.arena.common.dto.ApiResponse;
import com.arena.common.dto.CodeAnalysisDTO;
import com.arena.common.dto.HintResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Tag(name = "AI", description = "AI-powered features")
public class AIController {

    private final HintService hintService;
    private final CodeAnalysisService codeAnalysisService;
    private final ProblemGenerationService problemGenerationService;

    @PostMapping("/hints")
    @Operation(summary = "Generate a coding hint")
    public ResponseEntity<ApiResponse<HintResponseDTO>> generateHint(
            @Valid @RequestBody HintRequest request) {

        HintResponseDTO hint = hintService.generateHint(request);
        return ResponseEntity.ok(ApiResponse.success(hint));
    }

    @PostMapping("/analyze")
    @Operation(summary = "Analyze code quality")
    public ResponseEntity<ApiResponse<CodeAnalysisDTO>> analyzeCode(
            @Valid @RequestBody CodeAnalysisRequest request) {

        CodeAnalysisDTO analysis = codeAnalysisService.analyzeCode(request);
        return ResponseEntity.ok(ApiResponse.success(analysis));
    }

    @PostMapping("/generate-problem")
    @Operation(summary = "Generate a new coding problem")
    public ResponseEntity<ApiResponse<GeneratedProblem>> generateProblem(
            @Valid @RequestBody ProblemGenerationRequest request) {

        GeneratedProblem problem = problemGenerationService.generateProblem(request);
        return ResponseEntity.ok(ApiResponse.success(problem));
    }

    @GetMapping("/health")
    @Operation(summary = "Check AI service health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("AI Service is running"));
    }
}