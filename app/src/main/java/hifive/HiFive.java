package hifive;

import ch.aplu.jcardgame.*;
import hifive.Enum.Rank;
import hifive.Enum.Suit;
import hifive.Game.GameConfigurations;
import hifive.Game.GameFacade;
import hifive.Managers.CardManager;
import hifive.Managers.UIManager;

import java.util.*;

public class HiFive extends CardGame {
    private final GameFacade gameFacade;

    public HiFive(Properties properties) {
        super(700, 700, 30);
        // Configuration and game components
        GameConfigurations config = new GameConfigurations(properties);

        // Initialize the game facade
        this.gameFacade = new GameFacade(config, new Deck(Suit.values(), Rank.values(), "cover"), new CardManager(config), new UIManager(this));
    }

    // Main method to run the HiFive game application
    public String runApp() {
        setTitle("HiFive (V" + GameConfigurations.VERSION + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");

        // Start the game
        gameFacade.startGame();  // No need to set status here

        return gameFacade.getLogResult();
    }
}
