package hifive;

import ch.aplu.jcardgame.*;
import java.util.*;

public class HiFive extends CardGame {
    // Configuration and game components
    private final GameConfigurations config;
    private final Deck deck;
    private final CardManager cardManager;
    private final UIManager gameUI;
    private final GameFacade gameFacade;

    public HiFive(Properties properties) {
        super(700, 700, 30);
        this.config = new GameConfigurations(properties);
        this.deck = new Deck(Suit.values(), Rank.values(), "cover");
        this.cardManager = new CardManager(config);
        this.gameUI = new UIManager(config, this);

        // Initialize the game facade
        this.gameFacade = new GameFacade(config, deck, cardManager, gameUI);
    }

    // Main method to run the HiFive game application
    public String runApp() {
        setTitle("HiFive (V" + config.VERSION + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        gameUI.setStatus("Initializing...");

        // Start the game
        gameFacade.startGame();

        return gameFacade.getLogResult();
    }
}
