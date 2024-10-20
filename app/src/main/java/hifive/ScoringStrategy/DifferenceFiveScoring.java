package hifive.ScoringStrategy;

import ch.aplu.jcardgame.Card;
import hifive.Enum.Rank;
import hifive.Enum.Suit;

import java.util.List;

public class DifferenceFiveScoring implements ScoringStrategy {
    private final int fiveGoal;
    private final int differenceFivePoints;

    public DifferenceFiveScoring(int fiveGoal, int differenceFivePoints) {
        this.fiveGoal = fiveGoal;
        this.differenceFivePoints = differenceFivePoints;
    }

    @Override
    public int calculateScore(List<Card> cards) {
        if(cards.size() != 2)
            return 0;
        Card card1 = cards.get(0);
        Card card2 = cards.get(1);
        Rank rank1 = (Rank)card1.getRank();
        Rank rank2 = (Rank)card2.getRank();

        if(checkDifference(rank1, rank2) || checkDifference(rank2, rank1)) {
            Suit suit1 = (Suit)card1.getSuit();
            Suit suit2 = (Suit)card2.getSuit();
            return differenceFivePoints + suit1.getBonusFactor() + suit2.getBonusFactor();
        }
        return 0;
    }

    private boolean checkDifference(Rank rank1, Rank rank2) {
        if(Math.abs(rank1.getRankCardValue() - rank2.getRankCardValue()) == fiveGoal) {
            return true;
        }
        if(rank1.isWildCard()) {
            for(int wildValue : rank1.getWildValues()) {
                if(Math.abs(wildValue - rank2.getRankCardValue()) == fiveGoal) {
                    return true;
                }
            }
        }
        return false;
    }
}
