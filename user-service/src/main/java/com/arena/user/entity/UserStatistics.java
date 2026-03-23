package com.arena.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_statistics")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "total_battles")
    @Builder.Default
    private Integer totalBattles = 0;

    @Column(name = "problems_solved")
    @Builder.Default
    private Integer problemsSolved = 0;

    @Column(name = "easy_solved")
    @Builder.Default
    private Integer easySolved = 0;

    @Column(name = "medium_solved")
    @Builder.Default
    private Integer mediumSolved = 0;

    @Column(name = "hard_solved")
    @Builder.Default
    private Integer hardSolved = 0;

    @Column(name = "average_solve_time_ms")
    @Builder.Default
    private Long averageSolveTimeMs = 0L;

    @Column(name = "preferred_language", length = 20)
    private String preferredLanguage;

    @Column(name = "current_streak")
    @Builder.Default
    private Integer currentStreak = 0;

    @Column(name = "max_streak")
    @Builder.Default
    private Integer maxStreak = 0;

    @Column(name = "hints_used")
    @Builder.Default
    private Integer hintsUsed = 0;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}