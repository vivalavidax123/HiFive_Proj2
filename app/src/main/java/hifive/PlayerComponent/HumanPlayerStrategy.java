package hifive.PlayerComponent;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import hifive.CardComponent.CardManager;

import static ch.aplu.jgamegrid.GameGrid.delay;

public class HumanPlayerStrategy implements PlayerStrategy {
    private final int delayTime;

    public HumanPlayerStrategy(int delayTime) {
        this.delayTime = delayTime;
    }

    // Waits for the human player to select a card and returns the selected card
    @Override
    public Card playCard(Hand hand, CardManager cardManager) {
        hand.setTouchEnabled(true);
        Card selected = null;
        while (selected == null) {
            delay(delayTime);
        }
        hand.setTouchEnabled(false);
        return selected;
    }
}
