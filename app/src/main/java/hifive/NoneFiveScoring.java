package hifive;

import ch.aplu.jcardgame.Card;

import java.util.List;

public class NoneFiveScoring implements ScoringStrategy {
    @Override
    public int calculateScore(List<Card> cards) {
        return cards.stream().mapToInt(card -> ((Rank)card.getRank()).getRankCardValue()).sum();
    }
}