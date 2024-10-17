package hifive;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import static ch.aplu.jgamegrid.GameGrid.delay;

public class HumanPlayerStrategy implements PlayerStrategy {
    private final GameConfig config;

    public HumanPlayerStrategy(GameConfig config) {
        this.config = config;
    }

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