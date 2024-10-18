package hifive.GameEngine.ScoringComponent;

import ch.aplu.jcardgame.Card;

import java.util.List;

public interface ScoringStrategy {
    int calculateScore(List<Card> cards);
}