package hifive.Managers;

import ch.aplu.jcardgame.Card;
import hifive.Enum.Rank;
import hifive.Enum.Suit;

import java.util.List;
import java.util.stream.Collectors;

public class LogManager implements ILogManager {
    private final StringBuilder logResult = new StringBuilder();

    // Constructor
    public LogManager() {}

    @Override
    public void addCardPlayedToLog(int player, List<Card> cards) {
        if (cards.size() < 2) {
            return;
        }
        logResult.append("P").append(player).append("-");

        for (int i = 0; i < cards.size(); i++) {
            Rank cardRank = (Rank) cards.get(i).getRank();
            Suit cardSuit = (Suit) cards.get(i).getSuit();
            logResult.append(cardRank.getRankCardLog()).append(cardSuit.getSuitShortHand());
            if (i < cards.size() - 1) {
                logResult.append("-");
            }
        }
        logResult.append(",");
    }

    @Override
    public void addRoundInfoToLog(int roundNumber) {
        logResult.append("Round").append(roundNumber).append(":");
    }

    @Override
    public void addEndOfRoundToLog(int[] scores) {
        logResult.append("Score:");
        for (int score : scores) {
            logResult.append(score).append(",");
        }
        logResult.append("\n");
    }

    @Override
    public void addEndOfGameToLog(int[] scores, List<Integer> winners) {
        logResult.append("EndGame:");
        for (int score : scores) {
            logResult.append(score).append(",");
        }
        logResult.append("\n");
        logResult.append("Winners:").append(winners.stream().map(String::valueOf).collect(Collectors.joining(", ")));
    }

    @Override
    public String getLogResult() {
        return logResult.toString();
    }

    @Override
    public void resetLog() {
        logResult.setLength(0);
    }
}
