package com.arena.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "keycloak_id", unique = true, nullable = false)
    private String keycloakId;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(nullable = false)
    @Builder.Default
    private Integer rating = 1000;

    @Column(nullable = false)
    @Builder.Default
    private Integer wins = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer losses = 0;

    @Column(name = "rank_title", length = 50)
    @Builder.Default
    private String rankTitle = "Novice";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserStatistics statistics;

    // Helper method to calculate win rate
    public double getWinRate() {
        int totalGames = wins + losses;
        return totalGames > 0 ? (double) wins / totalGames * 100 : 0.0;
    }

    // Helper method to update rank title based on rating
    public void updateRankTitle() {
        this.rankTitle = switch (rating) {
            case Integer r when r >= 2400 -> "Grandmaster";
            case Integer r when r >= 2000 -> "Master";
            case Integer r when r >= 1600 -> "Expert";
            case Integer r when r >= 1200 -> "Intermediate";
            case Integer r when r >= 800 -> "Beginner";
            default -> "Novice";
        };
    }
}