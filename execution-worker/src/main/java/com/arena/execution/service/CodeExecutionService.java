package com.arena.execution.service;

import com.arena.common.enums.ProgrammingLanguage;
import com.arena.common.enums.SubmissionStatus;
import com.arena.common.event.CodeExecutionRequest;
import com.arena.common.event.CodeExecutionResult;
import com.arena.execution.dto.ExecutionContext;
import com.arena.execution.dto.TestCaseExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeExecutionService {

    private final DockerSandboxService dockerSandboxService;
    private final TestRunnerService testRunnerService;

    /**
     * Execute a code submission against all provided test cases.
     * A Docker sandbox container is created, used, and then always cleaned up
     * in the finally block regardless of outcome.
     */
    public CodeExecutionResult executeCode(CodeExecutionRequest request) {
        log.info("Executing code for submission: {}", request.getSubmissionId());

        ExecutionContext context = null;
        List<CodeExecutionResult.TestCaseResult> testResults = new ArrayList<>();

        try {
            // Build execution context
            context = ExecutionContext.builder()
                    .submissionId(request.getSubmissionId())
                    .code(request.getCode())
                    .language(request.getLanguage())
                    .timeLimitSeconds(request.getTimeLimitSeconds() != null
                            ? request.getTimeLimitSeconds() : 5)
                    .memoryLimitMb(request.getMemoryLimitMb() != null
                            ? request.getMemoryLimitMb() : 256)
                    .build();

            // Spin up sandbox container (image is pulled automatically if missing)
            String containerId = dockerSandboxService.createContainer(context);
            context.setContainerId(containerId);

            // Write source file into the container bind-mount
            dockerSandboxService.copyCodeToContainer(context);

            // Compile if the language requires it (currently only Java)
            if (requiresCompilation(context.getLanguage())) {
                CompilationResult compilationResult = dockerSandboxService.compileCode(context);
                if (!compilationResult.success()) {
                    return CodeExecutionResult.builder()
                            .submissionId(request.getSubmissionId())
                            .roomId(request.getRoomId())
                            .userId(request.getUserId())
                            .status(SubmissionStatus.COMPILATION_ERROR)
                            .testCasesPassed(0)
                            .totalTestCases(request.getTestCases().size())
                            .errorMessage(compilationResult.errorMessage())
                            .build();
                }
            }

            // Run each test case in sequence
            int passedCount         = 0;
            int totalExecutionTime  = 0;
            int maxMemoryUsed       = 0;
            SubmissionStatus finalStatus = SubmissionStatus.ACCEPTED;
            String errorMessage     = null;

            for (CodeExecutionRequest.TestCaseInfo testCase : request.getTestCases()) {
                TestCaseExecution execution = testRunnerService.runTestCase(
                        context,
                        testCase.getInput(),
                        testCase.getExpectedOutput()
                );

                CodeExecutionResult.TestCaseResult testResult = CodeExecutionResult.TestCaseResult.builder()
                        .testCaseId(testCase.getTestCaseId())
                        .passed(execution.isPassed())
                        .actualOutput(execution.getActualOutput())
                        .expectedOutput(testCase.getExpectedOutput())
                        .executionTimeMs(execution.getExecutionTimeMs())
                        .error(execution.getError())
                        .build();

                testResults.add(testResult);

                if (execution.isPassed()) {
                    passedCount++;
                } else if (finalStatus == SubmissionStatus.ACCEPTED) {
                    // Record the first failure reason
                    if (execution.getError() != null) {
                        if (execution.getError().contains("Time Limit")) {
                            finalStatus = SubmissionStatus.TIME_LIMIT_EXCEEDED;
                        } else if (execution.getError().contains("Memory")) {
                            finalStatus = SubmissionStatus.MEMORY_LIMIT_EXCEEDED;
                        } else {
                            finalStatus = SubmissionStatus.RUNTIME_ERROR;
                        }
                        errorMessage = execution.getError();
                    } else {
                        finalStatus = SubmissionStatus.WRONG_ANSWER;
                    }
                }

                totalExecutionTime += execution.getExecutionTimeMs();
            }

            // Promote back to ACCEPTED if every test case passed
            if (passedCount == request.getTestCases().size()) {
                finalStatus = SubmissionStatus.ACCEPTED;
            }

            return CodeExecutionResult.builder()
                    .submissionId(request.getSubmissionId())
                    .roomId(request.getRoomId())
                    .userId(request.getUserId())
                    .status(finalStatus)
                    .testCasesPassed(passedCount)
                    .totalTestCases(request.getTestCases().size())
                    .executionTimeMs(totalExecutionTime)
                    .memoryUsedKb(maxMemoryUsed)
                    .errorMessage(errorMessage)
                    .testCaseResults(testResults)
                    .build();

        } catch (Exception e) {
            log.error("Execution failed for submission: {}", request.getSubmissionId(), e);

            return CodeExecutionResult.builder()
                    .submissionId(request.getSubmissionId())
                    .roomId(request.getRoomId())
                    .userId(request.getUserId())
                    .status(SubmissionStatus.RUNTIME_ERROR)
                    .testCasesPassed(0)
                    .totalTestCases(request.getTestCases().size())
                    .errorMessage("Execution failed: " + e.getMessage())
                    .testCaseResults(testResults)
                    .build();

        } finally {
            // Always clean up the sandbox container
            if (context != null && context.getContainerId() != null) {
                try {
                    dockerSandboxService.removeContainer(context.getContainerId());
                } catch (Exception e) {
                    log.warn("Failed to clean up container for submission {}: {}",
                            context.getSubmissionId(), e.getMessage());
                }
            }
        }
    }

    private boolean requiresCompilation(ProgrammingLanguage language) {
        return language == ProgrammingLanguage.JAVA;
    }

    public record CompilationResult(boolean success, String errorMessage) {}
}