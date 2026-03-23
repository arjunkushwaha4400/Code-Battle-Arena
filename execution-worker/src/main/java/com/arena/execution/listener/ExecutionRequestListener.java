package com.arena.execution.listener;

import com.arena.common.constants.AppConstants;
import com.arena.common.event.CodeExecutionRequest;
import com.arena.common.event.CodeExecutionResult;
import com.arena.execution.service.CodeExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExecutionRequestListener {

    private final CodeExecutionService codeExecutionService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = AppConstants.CODE_EXECUTION_QUEUE, concurrency = "2-5")
    public void handleExecutionRequest(CodeExecutionRequest request) {
        log.info("Received execution request for submission: {}", request.getSubmissionId());

        try {
            CodeExecutionResult result = codeExecutionService.executeCode(request);

            rabbitTemplate.convertAndSend(
                    AppConstants.CODE_EXCHANGE,
                    AppConstants.CODE_RESULT_KEY,
                    result
            );

            log.info("Execution completed for submission: {} - Status: {}, Passed: {}/{}",
                    request.getSubmissionId(),
                    result.getStatus(),
                    result.getTestCasesPassed(),
                    result.getTotalTestCases());

        } catch (Exception e) {
            log.error("Execution failed for submission: {}", request.getSubmissionId(), e);

            CodeExecutionResult errorResult = CodeExecutionResult.builder()
                    .submissionId(request.getSubmissionId())
                    .roomId(request.getRoomId())
                    .userId(request.getUserId())
                    .status(com.arena.common.enums.SubmissionStatus.RUNTIME_ERROR)
                    .testCasesPassed(0)
                    .totalTestCases(request.getTestCases().size())
                    .errorMessage("Internal execution error: " + e.getMessage())
                    .build();

            rabbitTemplate.convertAndSend(
                    AppConstants.CODE_EXCHANGE,
                    AppConstants.CODE_RESULT_KEY,
                    errorResult
            );
        }
    }
}