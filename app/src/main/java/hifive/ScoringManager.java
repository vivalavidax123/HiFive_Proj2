package hifive;

import ch.aplu.jcardgame.Card;
import java.util.List;

public class ScoringManager {
    private final List<ScoringStrategy> scoringStrategies;

    public ScoringManager(List<ScoringStrategy> scoringStrategies) {
        this.scoringStrategies = scoringStrategies;
    }

    // Calculates the score for a player based on their hand
    public int calculateScoreForPlayer(List<Card> playerCards) {
        return scoringStrategies.stream()
                .mapToInt(strategy -> strategy.calculateScore(playerCards))
                .max()
                .orElse(0);
    }
}
