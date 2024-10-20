package hifive;

import ch.aplu.jcardgame.Card;
import java.util.ArrayList;
import java.util.List;

public class ObserverManager implements IObserverManager {
    private final List<GameObserver> observers = new ArrayList<>();

    @Override
    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyRoundStart(int roundNumber) {
        for (GameObserver observer : observers) {
            observer.onRoundStart(roundNumber);
        }
    }

    @Override
    public void notifyCardPlayed(int player, Card card) {
        for (GameObserver observer : observers) {
            observer.onCardPlayed(player, card);
        }
    }

    @Override
    public void notifyScoreUpdate(int player, int newScore) {
        for (GameObserver observer : observers) {
            observer.onScoreUpdate(player, newScore);
        }
    }

    @Override
    public void notifyGameOver(int[] finalScores, List<Integer> winners) {
        for (GameObserver observer : observers) {
            observer.onGameOver(finalScores, winners);
        }
    }
}
