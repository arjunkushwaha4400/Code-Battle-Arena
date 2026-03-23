package com.arena.battle.repository;

import com.arena.battle.entity.Submission;
import com.arena.common.enums.SubmissionStatus;
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
public interface SubmissionRepository extends JpaRepository<Submission, UUID> {

    List<Submission> findByRoomIdOrderBySubmittedAtAsc(UUID roomId);

    List<Submission> findByRoomIdAndUserId(UUID roomId, UUID userId);

    Optional<Submission> findFirstByRoomIdAndUserIdAndStatus(UUID roomId, UUID userId, SubmissionStatus status);

    Page<Submission> findByUserIdOrderBySubmittedAtDesc(UUID userId, Pageable pageable);

    @Query("SELECT s FROM Submission s WHERE s.room.id = :roomId AND s.status = 'ACCEPTED' ORDER BY s.submittedAt ASC")
    List<Submission> findAcceptedSubmissionsByRoomId(@Param("roomId") UUID roomId);

    @Query("SELECT COUNT(s) FROM Submission s WHERE s.userId = :userId AND s.status = 'ACCEPTED'")
    long countAcceptedByUserId(@Param("userId") UUID userId);

    boolean existsByRoomIdAndUserIdAndStatus(UUID roomId, UUID userId, SubmissionStatus status);
}