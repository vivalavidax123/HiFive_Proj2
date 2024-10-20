package hifive.Player;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import hifive.Managers.CardManager;

public interface PlayerStrategy {
    Card playCard(Hand hand, CardManager cardManager);
}