package hifive.PlayerComponent;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import hifive.CardComponent.CardManager;
import hifive.GameConfigurations;

import static ch.aplu.jgamegrid.GameGrid.delay;

public class HumanPlayerStrategy implements PlayerStrategy {
    private final GameConfigurations config;

    public HumanPlayerStrategy(GameConfigurations config) {
        this.config = config;
    }

    // Waits for the human player to select a card and returns the selected card
    @Override
    public Card playCard(Hand hand, CardManager cardManager) {
        hand.setTouchEnabled(true);
        Card selected = null;
        while(selected == null) {
            delay(config.delayTime);
        }
        hand.setTouchEnabled(false);
        return selected;
    }
}
