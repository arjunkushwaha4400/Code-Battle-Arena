package com.arena.battle.repository;

import com.arena.battle.entity.RoomParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomParticipantRepository extends JpaRepository<RoomParticipant, UUID> {

    List<RoomParticipant> findByRoomId(UUID roomId);

    Optional<RoomParticipant> findByRoomIdAndUserId(UUID roomId, UUID userId);

    boolean existsByRoomIdAndUserId(UUID roomId, UUID userId);

    int countByRoomId(UUID roomId);
}