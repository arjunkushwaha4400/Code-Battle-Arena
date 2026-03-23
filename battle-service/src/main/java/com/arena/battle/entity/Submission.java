package com.arena.battle.entity;

import com.arena.common.enums.ProgrammingLanguage;
import com.arena.common.enums.SubmissionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "submissions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private BattleRoom room;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProgrammingLanguage language;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SubmissionStatus status = SubmissionStatus.PENDING;

    @Column(name = "execution_time_ms")
    private Integer executionTimeMs;

    @Column(name = "memory_used_kb")
    private Integer memoryUsedKb;

    @Column(name = "test_cases_passed")
    @Builder.Default
    private Integer testCasesPassed = 0;

    @Column(name = "total_test_cases")
    @Builder.Default
    private Integer totalTestCases = 0;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "submitted_at", updatable = false)
    private LocalDateTime submittedAt;
}