package hifive;

import ch.aplu.jcardgame.Card;

import java.util.ArrayList;
import java.util.List;

public class ObserverManager {
    private final List<GameObserver> observers = new ArrayList<>();

    // Add an observer
    public void addObserver(GameObserver observer) {
        observers.add(observer);
    }

    // Remove an observer
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    // Notify observers of round start
    public void notifyRoundStart(int roundNumber) {
        for (GameObserver observer : observers) {
            observer.onRoundStart(roundNumber);
        }
    }

    // Notify observers that a card has been played
    public void notifyCardPlayed(int player, Card card) {
        for (GameObserver observer : observers) {
            observer.onCardPlayed(player, card);
        }
    }

    // Notify observers of score update
    public void notifyScoreUpdate(int player, int newScore) {
        for (GameObserver observer : observers) {
            observer.onScoreUpdate(player, newScore);
        }
    }

    // Notify observers that the game is over
    public void notifyGameOver(int[] finalScores, List<Integer> winners) {
        for (GameObserver observer : observers) {
            observer.onGameOver(finalScores, winners);
        }
    }
}
