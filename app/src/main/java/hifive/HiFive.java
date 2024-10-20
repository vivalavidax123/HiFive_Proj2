package hifive;

import ch.aplu.jcardgame.*;
import java.util.*;

public class HiFive extends CardGame {
    // Configuration and game components
    private final GameConfigurations config;
    private final GameFacade gameFacade;

    public HiFive(Properties properties) {
        super(700, 700, 30);
        this.config = new GameConfigurations(properties);

        // Initialize the game facade
        this.gameFacade = new GameFacade(config, new Deck(Suit.values(), Rank.values(), "cover"), new CardManager(config), new UIManager(config, this));
    }

    // Main method to run the HiFive game application
    public String runApp() {
        setTitle("HiFive (V" + config.VERSION + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");

        // Start the game
        gameFacade.startGame();  // No need to set status here

        return gameFacade.getLogResult();
    }
}
