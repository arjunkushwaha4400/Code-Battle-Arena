package com.arena.battle.listener;

import com.arena.battle.service.SubmissionService;
import com.arena.common.constants.AppConstants;
import com.arena.common.event.CodeExecutionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExecutionResultListener {

    private final SubmissionService submissionService;

    @RabbitListener(queues = AppConstants.CODE_RESULT_QUEUE)
    public void handleExecutionResult(CodeExecutionResult result) {
        log.info("Received execution result for submission: {}", result.getSubmissionId());

        try {
            submissionService.processExecutionResult(result);
            log.info("Processed execution result: status={}, passed={}/{}",
                    result.getStatus(),
                    result.getTestCasesPassed(),
                    result.getTotalTestCases());

        } catch (Exception e) {
            log.error("Failed to process execution result", e);
            throw e;
        }
    }
}