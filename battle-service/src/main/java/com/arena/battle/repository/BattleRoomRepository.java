package com.arena.battle.repository;

import com.arena.battle.entity.BattleRoom;
import com.arena.common.enums.RoomStatus;
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
public interface BattleRoomRepository extends JpaRepository<BattleRoom, UUID> {

    Optional<BattleRoom> findByRoomCode(String roomCode);

    boolean existsByRoomCode(String roomCode);

    List<BattleRoom> findByStatus(RoomStatus status);

    @Query("SELECT r FROM BattleRoom r LEFT JOIN FETCH r.participants WHERE r.id = :id")
    Optional<BattleRoom> findByIdWithParticipants(@Param("id") UUID id);

    @Query("SELECT r FROM BattleRoom r LEFT JOIN FETCH r.participants LEFT JOIN FETCH r.problem WHERE r.roomCode = :roomCode")
    Optional<BattleRoom> findByRoomCodeWithDetails(@Param("roomCode") String roomCode);

    @Query("SELECT r FROM BattleRoom r JOIN r.participants p WHERE p.userId = :userId AND r.status IN :statuses")
    List<BattleRoom> findByUserIdAndStatusIn(@Param("userId") UUID userId,
                                             @Param("statuses") List<RoomStatus> statuses);

    @Query("SELECT r FROM BattleRoom r JOIN r.participants p WHERE p.userId = :userId ORDER BY r.createdAt DESC")
    Page<BattleRoom> findByUserId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT COUNT(r) FROM BattleRoom r WHERE r.status = :status")
    long countByStatus(@Param("status") RoomStatus status);
}