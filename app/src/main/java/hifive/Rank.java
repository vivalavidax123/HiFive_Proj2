package hifive;

import java.util.Arrays;
import java.util.List;

public enum Rank {
    ACE(1, 1, Arrays.asList(11, 12, 13)), KING(13, 13, Arrays.asList(1, 3, 7, 9, 11)), QUEEN(12, 12, Arrays.asList(6, 7, 8, 9)), JACK(11, 11, Arrays.asList(1, 2, 3, 4)), TEN(10, 10), NINE(9, 9), EIGHT(8, 8), SEVEN(7, 7), SIX(6, 6), FIVE(5, 5), FOUR(4, 4), THREE(3, 3), TWO(2, 2);

    private final int rankCardValue;
    private final int scoreValue;
    private final List<Integer> wildValues;

    Rank(int rankCardValue, int scoreValue) {
        this(rankCardValue, scoreValue, null);
    }

    Rank(int rankCardValue, int scoreValue, List<Integer> wildValues) {
        this.rankCardValue = rankCardValue;
        this.scoreValue = scoreValue;
        this.wildValues = wildValues;
    }

    public static Rank getRankFromString(String cardName) {
        String rankString = cardName.substring(0, cardName.length() - 1);
        switch(rankString) {
            case "A":
                return ACE;
            case "K":
                return KING;
            case "Q":
                return QUEEN;
            case "J":
                return JACK;
            default:
                Integer rankValue = Integer.parseInt(rankString);
                for(Rank rank : Rank.values()) {
                    if(rank.getRankCardValue() == rankValue) {
                        return rank;
                    }
                }
                throw new IllegalArgumentException("Invalid rank: " + rankString);
        }
    }

    public int getRankCardValue() {
        return rankCardValue;
    }

    public int getScoreCardValue() {
        return scoreValue;
    }

    public String getRankCardLog() {
        return String.format("%d", rankCardValue);
    }

    public List<Integer> getWildValues() {
        return wildValues;
    }

    public boolean isWildCard() {
        return wildValues != null && !wildValues.isEmpty();
    }
}
