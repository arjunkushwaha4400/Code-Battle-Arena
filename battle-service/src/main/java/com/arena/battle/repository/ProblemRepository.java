package com.arena.battle.repository;

import com.arena.battle.entity.Problem;
import com.arena.common.enums.Difficulty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, UUID> {

    Page<Problem> findByIsActiveTrue(Pageable pageable);

    Page<Problem> findByDifficultyAndIsActiveTrue(Difficulty difficulty, Pageable pageable);

    @Query("SELECT p FROM Problem p WHERE p.isActive = true ORDER BY RANDOM() LIMIT 1")
    Optional<Problem> findRandomProblem();

    @Query("SELECT p FROM Problem p WHERE p.difficulty = :difficulty AND p.isActive = true ORDER BY RANDOM() LIMIT 1")
    Optional<Problem> findRandomProblemByDifficulty(@Param("difficulty") Difficulty difficulty);

    @Query("SELECT p FROM Problem p LEFT JOIN FETCH p.testCases WHERE p.id = :id")
    Optional<Problem> findByIdWithTestCases(@Param("id") UUID id);

    @Query("SELECT DISTINCT p FROM Problem p LEFT JOIN FETCH p.testCases WHERE p.isActive = true")
    List<Problem> findAllActiveWithTestCases();

    boolean existsByTitle(String title);
}