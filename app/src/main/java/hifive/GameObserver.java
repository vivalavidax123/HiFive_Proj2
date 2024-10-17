package hifive;

import ch.aplu.jcardgame.Card;

import java.util.List;

public interface GameObserver {
    void onRoundStart(int roundNumber);

    void onCardPlayed(int player, Card card);

    void onScoreUpdate(int player, int newScore);

    void onGameOver(int[] finalScores, List<Integer> winners);
}