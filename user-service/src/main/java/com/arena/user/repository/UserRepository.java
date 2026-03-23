package com.arena.user.repository;

import com.arena.user.entity.User;
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
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByKeycloakId(String keycloakId);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByKeycloakId(String keycloakId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // Leaderboard queries
    @Query("SELECT u FROM User u ORDER BY u.rating DESC")
    Page<User> findTopByRating(Pageable pageable);

    @Query("SELECT u FROM User u ORDER BY u.wins DESC")
    Page<User> findTopByWins(Pageable pageable);

    // Search users
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<User> searchByUsername(@Param("query") String query, Pageable pageable);

    // Find users by rating range (for matchmaking info)
    @Query("SELECT u FROM User u WHERE u.rating BETWEEN :minRating AND :maxRating AND u.id != :excludeId")
    List<User> findByRatingRange(@Param("minRating") int minRating,
                                 @Param("maxRating") int maxRating,
                                 @Param("excludeId") UUID excludeId);

    // Count users by rank
    @Query("SELECT u.rankTitle, COUNT(u) FROM User u GROUP BY u.rankTitle")
    List<Object[]> countByRankTitle();
}