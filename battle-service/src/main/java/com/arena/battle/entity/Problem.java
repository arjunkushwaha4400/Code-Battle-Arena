package com.arena.battle.entity;

import com.arena.common.enums.Difficulty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "problems")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @Column(name = "time_limit_seconds", nullable = false)
    @Builder.Default
    private Integer timeLimitSeconds = 5;

    @Column(name = "memory_limit_mb", nullable = false)
    @Builder.Default
    private Integer memoryLimitMb = 256;

    @Column(name = "input_format", columnDefinition = "TEXT")
    private String inputFormat;

    @Column(name = "output_format", columnDefinition = "TEXT")
    private String outputFormat;

    @Column(columnDefinition = "TEXT")
    private String constraints;

    @Column(name = "starter_code_java", columnDefinition = "TEXT")
    private String starterCodeJava;

    @Column(name = "starter_code_python", columnDefinition = "TEXT")
    private String starterCodePython;

    @Column(name = "starter_code_javascript", columnDefinition = "TEXT")
    private String starterCodeJavascript;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    private List<TestCase> testCases = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper methods
    public void addTestCase(TestCase testCase) {
        testCases.add(testCase);
        testCase.setProblem(this);
    }

    public void removeTestCase(TestCase testCase) {
        testCases.remove(testCase);
        testCase.setProblem(null);
    }

    public List<TestCase> getSampleTestCases() {
        return testCases.stream()
                .filter(tc -> !tc.getIsHidden())
                .toList();
    }

    public List<TestCase> getHiddenTestCases() {
        return testCases.stream()
                .filter(TestCase::getIsHidden)
                .toList();
    }
}