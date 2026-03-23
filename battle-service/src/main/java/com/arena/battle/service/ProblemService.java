package com.arena.battle.service;

import com.arena.battle.dto.CreateProblemRequest;
import com.arena.battle.dto.CreateTestCaseRequest;
import com.arena.battle.entity.Problem;
import com.arena.battle.entity.TestCase;
import com.arena.battle.mapper.ProblemMapper;
import com.arena.battle.repository.ProblemRepository;
import com.arena.common.dto.PagedResponse;
import com.arena.common.dto.ProblemDTO;
import com.arena.common.enums.Difficulty;
import com.arena.common.exception.BadRequestException;
import com.arena.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final ProblemMapper problemMapper;

    /**
     * Create a new problem with test cases
     */
    @Transactional
    public ProblemDTO createProblem(CreateProblemRequest request, UUID createdBy) {
        log.info("Creating problem: {}", request.getTitle());

        if (problemRepository.existsByTitle(request.getTitle())) {
            throw new BadRequestException("Problem with this title already exists");
        }

        Problem problem = problemMapper.toEntity(request);
        problem.setCreatedBy(createdBy);

        // Set default values if not provided
        if (problem.getTimeLimitSeconds() == null) {
            problem.setTimeLimitSeconds(5);
        }
        if (problem.getMemoryLimitMb() == null) {
            problem.setMemoryLimitMb(256);
        }

        // Add test cases
        AtomicInteger orderIndex = new AtomicInteger(0);
        for (CreateTestCaseRequest tcRequest : request.getTestCases()) {
            TestCase testCase = TestCase.builder()
                    .input(tcRequest.getInput())
                    .expectedOutput(tcRequest.getExpectedOutput())
                    .isHidden(tcRequest.getIsHidden())
                    .orderIndex(tcRequest.getOrderIndex() != null ?
                            tcRequest.getOrderIndex() : orderIndex.getAndIncrement())
                    .explanation(tcRequest.getExplanation())
                    .build();

            problem.addTestCase(testCase);
        }

        Problem savedProblem = problemRepository.save(problem);
        log.info("Created problem with ID: {}", savedProblem.getId());

        return problemMapper.toDTO(savedProblem);
    }

    /**
     * Get problem by ID
     */
    @Transactional(readOnly = true)
    public ProblemDTO getProblemById(UUID id) {
        Problem problem = problemRepository.findByIdWithTestCases(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem", id.toString()));
        return problemMapper.toDTO(problem);
    }

    /**
     * Get all active problems with pagination
     */
    @Transactional(readOnly = true)
    public PagedResponse<ProblemDTO> getAllProblems(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Problem> problemsPage = problemRepository.findByIsActiveTrue(pageable);

        List<ProblemDTO> problems = problemMapper.toDTOList(problemsPage.getContent());

        return PagedResponse.of(
                problems,
                problemsPage.getNumber(),
                problemsPage.getSize(),
                problemsPage.getTotalElements(),
                problemsPage.getTotalPages()
        );
    }

    /**
     * Get problems by difficulty
     */
    @Transactional(readOnly = true)
    public PagedResponse<ProblemDTO> getProblemsByDifficulty(Difficulty difficulty, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Problem> problemsPage = problemRepository.findByDifficultyAndIsActiveTrue(difficulty, pageable);

        List<ProblemDTO> problems = problemMapper.toDTOList(problemsPage.getContent());

        return PagedResponse.of(
                problems,
                problemsPage.getNumber(),
                problemsPage.getSize(),
                problemsPage.getTotalElements(),
                problemsPage.getTotalPages()
        );
    }

    /**
     * Get random problem
     */
    @Transactional(readOnly = true)
    public ProblemDTO getRandomProblem() {
        Problem problem = problemRepository.findRandomProblem()
                .orElseThrow(() -> new ResourceNotFoundException("No problems available"));
        return problemMapper.toDTO(problem);
    }

    /**
     * Get random problem by difficulty
     */
    @Transactional(readOnly = true)
    public ProblemDTO getRandomProblemByDifficulty(Difficulty difficulty) {
        Problem problem = problemRepository.findRandomProblemByDifficulty(difficulty)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No problems available for difficulty: " + difficulty));
        return problemMapper.toDTO(problem);
    }

    /**
     * Get problem entity for internal use
     */
    @Transactional(readOnly = true)
    public Problem getProblemEntityById(UUID id) {
        return problemRepository.findByIdWithTestCases(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem", id.toString()));
    }

    /**
     * Deactivate a problem
     */
    @Transactional
    public void deactivateProblem(UUID id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Problem", id.toString()));
        problem.setIsActive(false);
        problemRepository.save(problem);
        log.info("Deactivated problem: {}", id);
    }
}