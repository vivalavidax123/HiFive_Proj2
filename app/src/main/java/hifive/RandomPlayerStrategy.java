package hifive;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

public class RandomPlayerStrategy implements PlayerStrategy {

    // Selects a random card to play from the hand
    @Override
    public Card playCard(Hand hand, CardManager cardManager) {
        return cardManager.getRandomCard(hand);
    }
}
