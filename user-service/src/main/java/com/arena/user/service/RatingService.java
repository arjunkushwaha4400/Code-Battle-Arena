package com.arena.user.service;

import com.arena.common.constants.AppConstants;
import com.arena.common.exception.ResourceNotFoundException;
import com.arena.user.entity.User;
import com.arena.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingService {

    private final UserRepository userRepository;

    @Transactional
    public RatingUpdate calculateAndUpdateRatings(UUID winnerId, UUID loserId) {
        User winner = userRepository.findById(winnerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", winnerId.toString()));
        User loser = userRepository.findById(loserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", loserId.toString()));

        int winnerOldRating = winner.getRating();
        int loserOldRating = loser.getRating();

        // Calculate expected scores
        double expectedWinner = expectedScore(winnerOldRating, loserOldRating);
        double expectedLoser = expectedScore(loserOldRating, winnerOldRating);

        // Calculate new ratings
        int winnerNewRating = (int) Math.round(winnerOldRating + AppConstants.K_FACTOR * (1 - expectedWinner));
        int loserNewRating = (int) Math.round(loserOldRating + AppConstants.K_FACTOR * (0 - expectedLoser));

        // Ensure rating doesn't go below 0
        loserNewRating = Math.max(0, loserNewRating);

        // Update winner
        winner.setRating(winnerNewRating);
        winner.setWins(winner.getWins() + 1);
        winner.updateRankTitle();

        // Update loser
        loser.setRating(loserNewRating);
        loser.setLosses(loser.getLosses() + 1);
        loser.updateRankTitle();

        userRepository.save(winner);
        userRepository.save(loser);

        log.info("Updated ratings - Winner: {} ({} -> {}), Loser: {} ({} -> {})",
                winner.getUsername(), winnerOldRating, winnerNewRating,
                loser.getUsername(), loserOldRating, loserNewRating);

        return new RatingUpdate(
                winnerId, winnerOldRating, winnerNewRating,
                loserId, loserOldRating, loserNewRating
        );
    }

    private double expectedScore(int ratingA, int ratingB) {
        return 1.0 / (1 + Math.pow(10, (double) (ratingB - ratingA) / 400));
    }
    public record RatingUpdate(
            UUID winnerId,
            int winnerOldRating,
            int winnerNewRating,
            UUID loserId,
            int loserOldRating,
            int loserNewRating
    ) {
        public int winnerRatingChange() {
            return winnerNewRating - winnerOldRating;
        }

        public int loserRatingChange() {
            return loserNewRating - loserOldRating;
        }
    }
}