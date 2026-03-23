package com.arena.user.repository;

import com.arena.user.entity.UserStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserStatisticsRepository extends JpaRepository<UserStatistics, UUID> {

    Optional<UserStatistics> findByUserId(UUID userId);
}