package com.arena.ai.service;

import com.arena.ai.dto.GeneratedProblem;
import com.arena.ai.dto.ProblemGenerationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProblemGenerationService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    @CircuitBreaker(name = "aiService", fallbackMethod = "generateProblemFallback")
    @Retry(name = "aiService")
    public GeneratedProblem generateProblem(ProblemGenerationRequest request) {
        log.info("Generating {} problem on topic: {}", request.getDifficulty(), request.getTopic());

        String prompt = buildGenerationPrompt(request);

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return parseProblemResponse(response, request);
    }

    private String buildGenerationPrompt(ProblemGenerationRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a coding problem with the following specifications:\n\n");
        prompt.append("Difficulty: ").append(request.getDifficulty()).append("\n");

        if (request.getTopic() != null && !request.getTopic().isBlank()) {
            prompt.append("Topic: ").append(request.getTopic()).append("\n");
        }

        prompt.append("Number of test cases: ").append(request.getNumberOfTestCases()).append("\n\n");

        prompt.append("""
            Please provide the problem in the following format:
            
            TITLE: [Problem Title]
            
            DESCRIPTION:
            [Full problem description with examples]
            
            INPUT_FORMAT:
            [Description of input format]
            
            OUTPUT_FORMAT:
            [Description of output format]
            
            CONSTRAINTS:
            [List of constraints]
            
            TEST_CASES:
            [For each test case, provide:]
            INPUT: [input]
            OUTPUT: [expected output]
            HIDDEN: [true/false]
            ---
            
            Make the problem engaging and educational.
            Ensure test cases cover edge cases.
            First 2-3 test cases should be visible (HIDDEN: false), rest hidden (HIDDEN: true).
            """);

        return prompt.toString();
    }

    private GeneratedProblem parseProblemResponse(String response, ProblemGenerationRequest request) {
        String title = extractSection(response, "TITLE:", "DESCRIPTION:");
        String description = extractSection(response, "DESCRIPTION:", "INPUT_FORMAT:");
        String inputFormat = extractSection(response, "INPUT_FORMAT:", "OUTPUT_FORMAT:");
        String outputFormat = extractSection(response, "OUTPUT_FORMAT:", "CONSTRAINTS:");
        String constraints = extractSection(response, "CONSTRAINTS:", "TEST_CASES:");

        List<GeneratedProblem.GeneratedTestCase> testCases = parseTestCases(response);

        return GeneratedProblem.builder()
                .title(title)
                .description(description)
                .difficulty(request.getDifficulty())
                .inputFormat(inputFormat)
                .outputFormat(outputFormat)
                .constraints(constraints)
                .testCases(testCases)
                .build();
    }

    private String extractSection(String response, String startMarker, String endMarker) {
        int startIndex = response.indexOf(startMarker);
        if (startIndex == -1) return "";

        startIndex += startMarker.length();
        int endIndex = response.indexOf(endMarker, startIndex);
        if (endIndex == -1) endIndex = response.length();

        return response.substring(startIndex, endIndex).trim();
    }

    private List<GeneratedProblem.GeneratedTestCase> parseTestCases(String response) {
        List<GeneratedProblem.GeneratedTestCase> testCases = new ArrayList<>();

        int testCasesStart = response.indexOf("TEST_CASES:");
        if (testCasesStart == -1) return testCases;

        String testCasesSection = response.substring(testCasesStart + "TEST_CASES:".length());
        String[] cases = testCasesSection.split("---");

        for (String caseStr : cases) {
            if (caseStr.trim().isEmpty()) continue;

            String input = extractSection(caseStr + "\nEND", "INPUT:", "OUTPUT:");
            String output = extractSection(caseStr + "\nEND", "OUTPUT:", "HIDDEN:");
            String hiddenStr = extractSection(caseStr + "\nEND", "HIDDEN:", "END");

            if (!input.isEmpty() && !output.isEmpty()) {
                testCases.add(GeneratedProblem.GeneratedTestCase.builder()
                        .input(input.trim())
                        .expectedOutput(output.trim())
                        .isHidden(hiddenStr.toLowerCase().contains("true"))
                        .build());
            }
        }

        return testCases;
    }

    // Fallback method
    public GeneratedProblem generateProblemFallback(ProblemGenerationRequest request, Throwable t) {
        log.warn("AI service unavailable for problem generation: {}", t.getMessage());

        return GeneratedProblem.builder()
                .title("Sample Problem")
                .description("AI problem generation is currently unavailable. Please try again later.")
                .difficulty(request.getDifficulty())
                .inputFormat("N/A")
                .outputFormat("N/A")
                .constraints("N/A")
                .testCases(List.of())
                .build();
    }
}