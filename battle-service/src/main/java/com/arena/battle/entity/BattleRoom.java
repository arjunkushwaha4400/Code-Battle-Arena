package com.arena.battle.entity;

import com.arena.common.enums.RoomStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "battle_rooms")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattleRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "room_code", unique = true, nullable = false, length = 10)
    private String roomCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RoomStatus status = RoomStatus.WAITING;

    @Column(name = "max_players", nullable = false)
    @Builder.Default
    private Integer maxPlayers = 2;

    @Column(name = "winner_id")
    private UUID winnerId;

    @Column(name = "is_ranked")
    @Builder.Default
    private Boolean isRanked = true;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RoomParticipant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Submission> submissions = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    // Helper methods
    public void addParticipant(RoomParticipant participant) {
        participants.add(participant);
        participant.setRoom(this);
    }

    public void removeParticipant(RoomParticipant participant) {
        participants.remove(participant);
        participant.setRoom(null);
    }

    public boolean isFull() {
        return participants.size() >= maxPlayers;
    }

    public boolean hasParticipant(UUID userId) {
        return participants.stream()
                .anyMatch(p -> p.getUserId().equals(userId));
    }

    public RoomParticipant getParticipant(UUID userId) {
        return participants.stream()
                .filter(p -> p.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    public boolean allParticipantsReady() {
        return participants.size() >= 2 &&
                participants.stream().allMatch(RoomParticipant::getIsReady);
    }

    public UUID getOpponentId(UUID userId) {
        return participants.stream()
                .filter(p -> !p.getUserId().equals(userId))
                .map(RoomParticipant::getUserId)
                .findFirst()
                .orElse(null);
    }
}