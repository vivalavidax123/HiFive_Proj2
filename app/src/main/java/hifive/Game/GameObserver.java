package hifive.Game;

import ch.aplu.jcardgame.Card;

import java.util.List;

public interface GameObserver {
    // Notifies when a new round starts
    void onRoundStart(int roundNumber);

    // Notifies when a player plays a card
    void onCardPlayed(int player, Card card);

    // Notifies when a player's score is updated
    void onScoreUpdate(int player, int newScore);

    // Notifies when the game is over, providing final scores and winners
    void onGameOver(int[] finalScores, List<Integer> winners);
}
