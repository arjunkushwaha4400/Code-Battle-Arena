package com.arena.ai.service;

import com.arena.ai.dto.CodeAnalysisRequest;
import com.arena.common.dto.CodeAnalysisDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeAnalysisService {

    private final ChatClient chatClient;

    @CircuitBreaker(name = "aiService", fallbackMethod = "analyzeCodeFallback")
    @Retry(name = "aiService")
    public CodeAnalysisDTO analyzeCode(CodeAnalysisRequest request) {
        log.info("Analyzing code in language: {}", request.getLanguage());

        String prompt = buildAnalysisPrompt(request);

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return parseAnalysisResponse(response);
    }

    private String buildAnalysisPrompt(CodeAnalysisRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Analyze this ").append(request.getLanguage()).append(" code:\n\n");
        prompt.append("```").append(request.getLanguage()).append("\n");
        prompt.append(request.getCode());
        prompt.append("\n```\n\n");

        if (request.getProblemDescription() != null) {
            prompt.append("Problem context: ").append(request.getProblemDescription()).append("\n\n");
        }

        prompt.append("""
            Please provide analysis in the following format:
            
            TIME_COMPLEXITY: [Big O notation]
            SPACE_COMPLEXITY: [Big O notation]
            QUALITY_SCORE: [1-10]
            
            SUGGESTIONS:
            - [suggestion 1]
            - [suggestion 2]
            - [suggestion 3]
            
            EXPLANATION:
            [Brief explanation of the code's approach and efficiency]
            """);

        return prompt.toString();
    }

    private CodeAnalysisDTO parseAnalysisResponse(String response) {
        String timeComplexity = extractValue(response, "TIME_COMPLEXITY:");
        String spaceComplexity = extractValue(response, "SPACE_COMPLEXITY:");
        int qualityScore = parseQualityScore(extractValue(response, "QUALITY_SCORE:"));
        List<String> suggestions = extractSuggestions(response);
        String explanation = extractExplanation(response);

        return CodeAnalysisDTO.builder()
                .timeComplexity(timeComplexity)
                .spaceComplexity(spaceComplexity)
                .qualityScore(qualityScore)
                .suggestions(suggestions)
                .explanation(explanation)
                .build();
    }

    private String extractValue(String response, String prefix) {
        int startIndex = response.indexOf(prefix);
        if (startIndex == -1) return "Unknown";

        startIndex += prefix.length();
        int endIndex = response.indexOf("\n", startIndex);
        if (endIndex == -1) endIndex = response.length();

        return response.substring(startIndex, endIndex).trim();
    }

    private int parseQualityScore(String score) {
        try {
            int parsed = Integer.parseInt(score.replaceAll("[^0-9]", ""));
            return Math.min(10, Math.max(1, parsed));
        } catch (NumberFormatException e) {
            return 5;
        }
    }

    private List<String> extractSuggestions(String response) {
        int startIndex = response.indexOf("SUGGESTIONS:");
        if (startIndex == -1) return List.of("No specific suggestions");

        int endIndex = response.indexOf("EXPLANATION:", startIndex);
        if (endIndex == -1) endIndex = response.length();

        String suggestionsSection = response.substring(startIndex + "SUGGESTIONS:".length(), endIndex);

        return Arrays.stream(suggestionsSection.split("\n"))
                .map(String::trim)
                .filter(s -> s.startsWith("-") || s.startsWith("•"))
                .map(s -> s.substring(1).trim())
                .filter(s -> !s.isEmpty())
                .limit(5)
                .toList();
    }

    private String extractExplanation(String response) {
        int startIndex = response.indexOf("EXPLANATION:");
        if (startIndex == -1) return "No explanation available";

        return response.substring(startIndex + "EXPLANATION:".length()).trim();
    }

    // Fallback method
    public CodeAnalysisDTO analyzeCodeFallback(CodeAnalysisRequest request, Throwable t) {
        log.warn("AI service unavailable for code analysis: {}", t.getMessage());

        return CodeAnalysisDTO.builder()
                .timeComplexity("Unable to analyze")
                .spaceComplexity("Unable to analyze")
                .qualityScore(5)
                .suggestions(List.of(
                        "Consider using meaningful variable names",
                        "Add comments to explain complex logic",
                        "Handle edge cases"
                ))
                .explanation("AI analysis is currently unavailable. Please try again later.")
                .build();
    }
}