package hifive.GameEngine.ScoringComponent;

import ch.aplu.jcardgame.Card;
import hifive.Enumeration.Rank;
import hifive.Enumeration.Suit;

import java.util.List;

public class SumFiveScoring implements ScoringStrategy {
    private final int fiveGoal;
    private final int sumFivePoints;

    public SumFiveScoring(int fiveGoal, int sumFivePoints) {
        this.fiveGoal = fiveGoal;
        this.sumFivePoints = sumFivePoints;
    }

    @Override
    public int calculateScore(List<Card> cards) {
        if(cards.size() != 2)
            return 0;
        Card card1 = cards.get(0);
        Card card2 = cards.get(1);
        Rank rank1 = (Rank)card1.getRank();
        Rank rank2 = (Rank)card2.getRank();

        if(checkSum(rank1, rank2) || checkSum(rank2, rank1)) {
            Suit suit1 = (Suit)card1.getSuit();
            Suit suit2 = (Suit)card2.getSuit();
            return sumFivePoints + suit1.getBonusFactor() + suit2.getBonusFactor();
        }
        return 0;
    }

    private boolean checkSum(Rank rank1, Rank rank2) {
        if(rank1.getRankCardValue() + rank2.getRankCardValue() == fiveGoal) {
            return true;
        }
        if(rank1.isWildCard()) {
            for(int wildValue : rank1.getWildValues()) {
                if(wildValue + rank2.getRankCardValue() == fiveGoal) {
                    return true;
                }
            }
        }
        return false;
    }
}
