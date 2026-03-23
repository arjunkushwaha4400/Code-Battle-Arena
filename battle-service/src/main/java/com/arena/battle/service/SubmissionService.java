package com.arena.battle.service;

import com.arena.battle.dto.SubmissionResponse;
import com.arena.battle.dto.SubmitCodeRequest;
import com.arena.battle.entity.BattleRoom;
import com.arena.battle.entity.Problem;
import com.arena.battle.entity.Submission;
import com.arena.battle.entity.TestCase;
import com.arena.battle.mapper.SubmissionMapper;
import com.arena.battle.repository.SubmissionRepository;
import com.arena.common.constants.AppConstants;
import com.arena.common.enums.RoomStatus;
import com.arena.common.enums.SubmissionStatus;
import com.arena.common.event.BattleCompletedEvent;
import com.arena.common.event.CodeExecutionRequest;
import com.arena.common.event.CodeExecutionResult;
import com.arena.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final RoomService roomService;
    private final SubmissionMapper submissionMapper;
    private final RabbitTemplate rabbitTemplate;
    private final WebSocketNotificationService notificationService;

    /**
     * Submit code for execution
     */
    @Transactional
    public SubmissionResponse submitCode(SubmitCodeRequest request, UUID userId) {
        log.info("User {} submitting code for room {}", userId, request.getRoomId());

        // Get room and validate
        BattleRoom room = roomService.getRoomEntityById(request.getRoomId());

        if (room.getStatus() != RoomStatus.IN_PROGRESS) {
            throw new BadRequestException("Battle is not in progress");
        }

        if (!room.hasParticipant(userId)) {
            throw new BadRequestException("You are not a participant in this battle");
        }

        // Check if already has accepted submission
        if (submissionRepository.existsByRoomIdAndUserIdAndStatus(
                request.getRoomId(), userId, SubmissionStatus.ACCEPTED)) {
            throw new BadRequestException("You already have an accepted submission");
        }

        // Create submission
        Submission submission = Submission.builder()
                .room(room)
                .userId(userId)
                .code(request.getCode())
                .language(request.getLanguage())
                .status(SubmissionStatus.PENDING)
                .totalTestCases(room.getProblem().getTestCases().size())
                .build();

        Submission savedSubmission = submissionRepository.save(submission);

        // Send to execution queue
        sendToExecutionQueue(savedSubmission, room.getProblem());

        // Notify opponent
        UUID opponentId = room.getOpponentId(userId);
        if (opponentId != null) {
            notificationService.notifyRoomParticipants(
                    room.getId(),
                    "OPPONENT_SUBMITTED",
                    new OpponentSubmittedPayload(userId, savedSubmission.getId())
            );
        }

        log.info("Submission created: {}", savedSubmission.getId());

        return submissionMapper.toResponse(savedSubmission);
    }

    /**
     * Process execution result from worker
     */
    @Transactional
    public void processExecutionResult(CodeExecutionResult result) {
        log.info("Processing execution result for submission: {}", result.getSubmissionId());

        Submission submission = submissionRepository.findById(result.getSubmissionId())
                .orElseThrow(() -> new BadRequestException("Submission not found"));

        // Update submission
        submission.setStatus(result.getStatus());
        submission.setTestCasesPassed(result.getTestCasesPassed());
        submission.setExecutionTimeMs(result.getExecutionTimeMs());
        submission.setMemoryUsedKb(result.getMemoryUsedKb());
        submission.setErrorMessage(result.getErrorMessage());

        submissionRepository.save(submission);

        // Update participant
        BattleRoom room = submission.getRoom();
        long submissionTimeMs = Duration.between(
                room.getStartedAt(),
                submission.getSubmittedAt()
        ).toMillis();

        roomService.updateParticipantSubmission(
                room.getId(),
                submission.getUserId(),
                result.getTestCasesPassed(),
                submissionTimeMs
        );

        // Notify user of result
        notificationService.notifyUser(
                submission.getUserId(),
                "SUBMISSION_RESULT",
                submissionMapper.toResponse(submission)
        );

        // Notify opponent
        UUID opponentId = room.getOpponentId(submission.getUserId());
        if (opponentId != null) {
            notificationService.notifyUser(
                    opponentId,
                    "OPPONENT_RESULT",
                    new OpponentResultPayload(
                            submission.getUserId(),
                            result.getStatus(),
                            result.getTestCasesPassed(),
                            submission.getTotalTestCases()
                    )
            );
        }

        // Check if battle should end
        checkBattleEnd(room);
    }

    /**
     * Check if battle should end
     */
    private void checkBattleEnd(BattleRoom room) {
        // Reload room to get fresh data
        room = roomService.getRoomEntityById(room.getId());

        // Get accepted submissions
        List<Submission> acceptedSubmissions = submissionRepository
                .findAcceptedSubmissionsByRoomId(room.getId());

        if (!acceptedSubmissions.isEmpty()) {
            // Winner is the first accepted submission
            Submission winnerSubmission = acceptedSubmissions.get(0);
            UUID winnerId = winnerSubmission.getUserId();
            UUID loserId = room.getOpponentId(winnerId);

            // End battle
            roomService.endBattle(room.getId(), winnerId);

            // Notify participants
            BattleEndPayload payload = new BattleEndPayload(
                    room.getId(),
                    winnerId,
                    loserId,
                    winnerSubmission.getTestCasesPassed(),
                    winnerSubmission.getTotalTestCases()
            );

            notificationService.notifyRoom(room.getId(), "BATTLE_END", payload);

            // Send event for rating update
            if (room.getIsRanked() && loserId != null) {
                BattleCompletedEvent event = BattleCompletedEvent.builder()
                        .roomId(room.getId())
                        .winnerId(winnerId)
                        .loserId(loserId)
                        .completedAt(LocalDateTime.now())
                        .build();

                rabbitTemplate.convertAndSend(
                        AppConstants.BATTLE_EXCHANGE,
                        AppConstants.BATTLE_COMPLETED_KEY,
                        event
                );

                log.info("Sent battle completed event for room: {}", room.getId());
            }
        }
    }

    /**
     * Send submission to execution queue
     */
    private void sendToExecutionQueue(Submission submission, Problem problem) {
        List<CodeExecutionRequest.TestCaseInfo> testCaseInfos = problem.getTestCases().stream()
                .map(tc -> CodeExecutionRequest.TestCaseInfo.builder()
                        .testCaseId(tc.getId())
                        .input(tc.getInput())
                        .expectedOutput(tc.getExpectedOutput())
                        .orderIndex(tc.getOrderIndex())
                        .build())
                .toList();

        CodeExecutionRequest request = CodeExecutionRequest.builder()
                .submissionId(submission.getId())
                .roomId(submission.getRoom().getId())
                .userId(submission.getUserId())
                .code(submission.getCode())
                .language(submission.getLanguage())
                .testCases(testCaseInfos)
                .timeLimitSeconds(problem.getTimeLimitSeconds())
                .memoryLimitMb(problem.getMemoryLimitMb())
                .build();

        rabbitTemplate.convertAndSend(
                AppConstants.CODE_EXCHANGE,
                AppConstants.CODE_EXECUTE_KEY,
                request
        );

        log.info("Sent submission {} to execution queue", submission.getId());
    }

    /**
     * Get submissions by room
     */
    @Transactional(readOnly = true)
    public List<SubmissionResponse> getSubmissionsByRoom(UUID roomId) {
        List<Submission> submissions = submissionRepository.findByRoomIdOrderBySubmittedAtAsc(roomId);
        return submissionMapper.toResponseList(submissions);
    }

    /**
     * Get submission by ID
     */
    @Transactional(readOnly = true)
    public SubmissionResponse getSubmissionById(UUID id) {
        Submission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Submission not found"));
        return submissionMapper.toResponse(submission);
    }

    // Payload records
    record OpponentSubmittedPayload(UUID opponentId, UUID submissionId) {}

    record OpponentResultPayload(
            UUID opponentId,
            SubmissionStatus status,
            int testCasesPassed,
            int totalTestCases
    ) {}

    record BattleEndPayload(
            UUID roomId,
            UUID winnerId,
            UUID loserId,
            int winnerTestCasesPassed,
            int totalTestCases
    ) {}
}