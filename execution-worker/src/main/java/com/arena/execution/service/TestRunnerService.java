package com.arena.execution.service;

import com.arena.execution.dto.ExecutionContext;
import com.arena.execution.dto.TestCaseExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestRunnerService {

    private final DockerSandboxService dockerSandboxService;


    public TestCaseExecution runTestCase(
            ExecutionContext context,
            String input,
            String expectedOutput) {

        log.debug("Running test case for submission: {}", context.getSubmissionId());

        DockerSandboxService.ExecutionOutput result = dockerSandboxService.executeCode(
                context,
                input,
                context.getTimeLimitSeconds()
        );

        // TLE
        if (result.timedOut()) {
            return TestCaseExecution.builder()
                    .input(input)
                    .expectedOutput(expectedOutput)
                    .actualOutput("")
                    .passed(false)
                    .executionTimeMs(result.executionTimeMs())
                    .error("Time Limit Exceeded")
                    .build();
        }

        // Runtime error
        if (result.error() != null && !result.error().isEmpty()) {
            return TestCaseExecution.builder()
                    .input(input)
                    .expectedOutput(expectedOutput)
                    .actualOutput(result.output())
                    .passed(false)
                    .executionTimeMs(result.executionTimeMs())
                    .error(result.error())
                    .build();
        }

        // Compare output
        boolean passed = compareOutput(result.output(), expectedOutput);

        return TestCaseExecution.builder()
                .input(input)
                .expectedOutput(expectedOutput)
                .actualOutput(result.output())
                .passed(passed)
                .executionTimeMs(result.executionTimeMs())
                .error(passed ? null : "Wrong Answer")
                .build();
    }


    private boolean compareOutput(String actual, String expected) {
        if (actual == null) {
            return expected == null || expected.isEmpty();
        }
        return normalizeOutput(actual).equals(normalizeOutput(expected));
    }

    private String normalizeOutput(String output) {
        if (output == null) {
            return "";
        }
        return output
                .trim()
                .replaceAll("\\r\\n", "\n")     // CRLF → LF
                .replaceAll("\\r", "\n")         // CR   → LF
                .replaceAll("[ \\t]+$", "")      // trailing spaces/tabs per line
                .replaceAll("(?m)^[ \\t]+", ""); // leading  spaces/tabs per line
    }
}