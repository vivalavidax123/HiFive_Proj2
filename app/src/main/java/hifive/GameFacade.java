package hifive;

import ch.aplu.jcardgame.*;
import java.util.*;

public class GameFacade {
    private final GameEngine gameEngine;
    private final ILogManager logManager;

    public GameFacade(GameConfigurations config, Deck deck, CardManager cardManager, UIManager gameUI) {
        this.logManager = new LogManager(); // Create a new instance
        IObserverManager observerManager = new ObserverManager();

        // Initialize the game
        GameInitializer gameInitializer = new GameInitializer(config, deck, cardManager, gameUI);
        gameInitializer.initGame();

        // Get initialized components
        Hand[] hands = gameInitializer.getHands();
        Hand playingArea = gameInitializer.getPlayingArea();
        Hand pack = gameInitializer.getPack();
        List<List<String>> playerAutoMovements = gameInitializer.getPlayerAutoMovements();

        // Create game components using the factory
        GameComponentFactory factory = new StandardGameComponentFactory();
        List<ScoringStrategy> scoringStrategies = factory.createScoringStrategies(config);
        ScoringManager scoringManager = new ScoringManager(scoringStrategies);
        PlayerStrategy[] playerStrategies = factory.createPlayerStrategies(config);

        // Initialize scores array
        int[] scores = new int[config.NB_PLAYERS];

        // Initialize the game engine
        this.gameEngine = new GameEngine(config, cardManager, gameUI, logManager, observerManager,
                scoringManager, playerStrategies, scores, playerAutoMovements, hands, playingArea, pack);

        // Set up card listeners for the human player
        gameEngine.setupCardListeners();
    }

    // Start the game
    // Start the game
    public void startGame() {
        gameEngine.playGame();
    }

    // Get the final scores
    public int[] getScores() {
        return gameEngine.getScores();
    }

    // Get the log result
    public String getLogResult() {
        return logManager.getLogResult();
    }

    // Get the list of winners
    public List<Integer> getWinners() {
        return gameEngine.getWinners();
    }
}
