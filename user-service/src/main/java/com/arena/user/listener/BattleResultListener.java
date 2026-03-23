package com.arena.user.listener;

import com.arena.common.constants.AppConstants;
import com.arena.common.event.BattleCompletedEvent;
import com.arena.user.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BattleResultListener {

    private final RatingService ratingService;

    @RabbitListener(queues = AppConstants.BATTLE_RESULT_QUEUE)
    public void handleBattleCompleted(BattleCompletedEvent event) {
        log.info("Received battle completed event: roomId={}, winnerId={}, loserId={}",
                event.getRoomId(), event.getWinnerId(), event.getLoserId());

        try {
            RatingService.RatingUpdate update = ratingService.calculateAndUpdateRatings(
                    event.getWinnerId(),
                    event.getLoserId()
            );

            log.info("Rating update completed: winner {} -> {}, loser {} -> {}",
                    update.winnerOldRating(), update.winnerNewRating(),
                    update.loserOldRating(), update.loserNewRating());

        } catch (Exception e) {
            log.error("Failed to update ratings for battle: roomId={}", event.getRoomId(), e);
            throw e; // Re-throw to trigger retry
        }
    }
}