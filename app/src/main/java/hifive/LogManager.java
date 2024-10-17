package hifive;

import ch.aplu.jcardgame.Card;

import java.util.List;
import java.util.stream.Collectors;

public class LogManager {
    // Returns the singleton instance of LogManager
    public static LogManager getInstance() {
        return instance;
    }

    // Adds information about played cards to the log
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

    // Adds round information to the log
    public void addRoundInfoToLog(int roundNumber) {
        logResult.append("Round").append(roundNumber).append(":");
    }

    // Adds end of round information and scores to the log
    public void addEndOfRoundToLog(int[] scores) {
        logResult.append("Score:");
        for(int score : scores) {
            logResult.append(score).append(",");
        }
        logResult.append("\n");
    }

    // Adds end of game information, final scores, and winners to the log
    public void addEndOfGameToLog(int[] scores, List<Integer> winners) {
        logResult.append("EndGame:");
        for(int score : scores) {
            logResult.append(score).append(",");
        }
        logResult.append("\n");
        logResult.append("Winners:").append(winners.stream().map(String::valueOf).collect(Collectors.joining(", ")));
    }

    // Returns the complete log as a string
    public String getLogResult() {
        return logResult.toString();
    }

    // Clears the log
    public void resetLog() {
        logResult.setLength(0);
    }

    private static final LogManager instance = new LogManager();
    private final StringBuilder logResult = new StringBuilder();
}
