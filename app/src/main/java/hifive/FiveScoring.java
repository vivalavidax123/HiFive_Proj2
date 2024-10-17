package hifive;

import ch.aplu.jcardgame.Card;

import java.util.List;

public class FiveScoring implements ScoringStrategy {
    private final int fiveGoal;
    private final int fivePoints;

    public FiveScoring(int fiveGoal, int fivePoints) {
        this.fiveGoal = fiveGoal;
        this.fivePoints = fivePoints;
    }

    @Override
    public int calculateScore(List<Card> cards) {
        int maxScore = 0;
        for(Card card : cards) {
            Rank rank = (Rank)card.getRank();
            Suit suit = (Suit)card.getSuit();
            if(rank.getRankCardValue() == fiveGoal || (rank.isWildCard() && rank.getWildValues().contains(fiveGoal))) {
                int score = fivePoints + suit.getBonusFactor();
                if(score > maxScore) {
                    maxScore = score;
                }
            }
        }
        return maxScore;
    }
}
