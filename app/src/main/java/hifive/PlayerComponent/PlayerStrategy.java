package hifive.PlayerComponent;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import hifive.CardComponent.CardManager;

public interface PlayerStrategy {
    Card playCard(Hand hand, CardManager cardManager);
}