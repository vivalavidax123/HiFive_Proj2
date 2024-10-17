package hifive;

import ch.aplu.jcardgame.Card;

import java.util.List;
import java.util.stream.Collectors;

public class LogManager {
    private static final LogManager instance = new LogManager();
    private final StringBuilder logResult = new StringBuilder();

    public static LogManager getInstance() {
        return instance;
    }

    public void addCardPlayedToLog(int player, List<Card> cards) {
        if(cards.size() < 2) {
            return;
        }
        logResult.append("P").append(player).append("-");

        for(int i = 0; i < cards.size(); i++) {
            Rank cardRank = (Rank)cards.get(i).getRank();
            Suit cardSuit = (Suit)cards.get(i).getSuit();
            logResult.append(cardRank.getRankCardLog()).append(cardSuit.getSuitShortHand());
            if(i < cards.size() - 1) {
                logResult.append("-");
            }
        }
        logResult.append(",");
    }

    public void addRoundInfoToLog(int roundNumber) {
        logResult.append("Round").append(roundNumber).append(":");
    }

    public void addEndOfRoundToLog(int[] scores) {
        logResult.append("Score:");
        for(int score : scores) {
            logResult.append(score).append(",");
        }
        logResult.append("\n");
    }

    public void addEndOfGameToLog(int[] scores, List<Integer> winners) {
        logResult.append("EndGame:");
        for(int score : scores) {
            logResult.append(score).append(",");
        }
        logResult.append("\n");
        logResult.append("Winners:").append(winners.stream().map(String::valueOf).collect(Collectors.joining(", ")));
    }

    public String getLogResult() {
        return logResult.toString();
    }

    public void resetLog() {
        logResult.setLength(0);
    }
}
