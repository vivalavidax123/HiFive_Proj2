package hifive.Managers;

import ch.aplu.jcardgame.Card;
import hifive.GameObserver;

import java.util.List;

public interface IObserverManager {
    void addObserver(GameObserver observer);
    void removeObserver(GameObserver observer);
    void notifyRoundStart(int roundNumber);
    void notifyCardPlayed(int player, Card card);
    void notifyScoreUpdate(int player, int newScore);
    void notifyGameOver(int[] finalScores, List<Integer> winners);
}
