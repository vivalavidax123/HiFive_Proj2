package hifive;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public interface PlayerStrategy {
    Card playCard(Hand hand, CardManager cardManager);
}