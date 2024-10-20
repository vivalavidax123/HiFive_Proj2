package hifive;

import ch.aplu.jcardgame.Card;
import java.util.List;

public interface ILogManager {
    void addCardPlayedToLog(int player, List<Card> cards);
    void addRoundInfoToLog(int roundNumber);
    void addEndOfRoundToLog(int[] scores);
    void addEndOfGameToLog(int[] scores, List<Integer> winners);
    String getLogResult();
    void resetLog();
}
