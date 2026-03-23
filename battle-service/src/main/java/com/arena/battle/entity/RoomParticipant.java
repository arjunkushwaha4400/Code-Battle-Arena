package com.arena.battle.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "room_participants",
        uniqueConstraints = @UniqueConstraint(columnNames = {"room_id", "user_id"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private BattleRoom room;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    @Builder.Default
    private Integer rating = 1000;

    @Column(name = "is_ready", nullable = false)
    @Builder.Default
    private Boolean isReady = false;

    @Column(name = "has_submitted", nullable = false)
    @Builder.Default
    private Boolean hasSubmitted = false;

    @Column(name = "test_cases_passed")
    @Builder.Default
    private Integer testCasesPassed = 0;

    @Column(name = "submission_time_ms")
    private Long submissionTimeMs;

    @Column(name = "hints_used")
    @Builder.Default
    private Integer hintsUsed = 0;

    @CreationTimestamp
    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;
}