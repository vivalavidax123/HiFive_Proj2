package hifive.Game;

import ch.aplu.jcardgame.*;
import hifive.Managers.*;
import hifive.Player.PlayerStrategy;
import hifive.ScoringStrategy.ScoringManager;
import hifive.ScoringStrategy.ScoringStrategy;

import java.util.*;

public class GameFacade {
    private final GameEngine gameEngine;
    private final ILogManager logManager;

    public GameFacade(GameConfigurations config, Deck deck, CardManager cardManager, UIManager gameUI) {
        // Initialize the log manager
        this.logManager = new LogManager();

        // Initialize observer manager for observing game events
        IObserverManager observerManager = new ObserverManager();

        // Create game components using the factory
        GameComponentFactory factory = new StandardGameComponentFactory();

        // Set up the strategies for scoring and players
        List<ScoringStrategy> scoringStrategies = factory.createScoringStrategies(config);
        ScoringManager scoringManager = new ScoringManager(scoringStrategies);
        PlayerStrategy[] playerStrategies = factory.createPlayerStrategies(config);

        // Initialize scores array based on the number of players
        int[] scores = new int[GameConfigurations.NB_PLAYERS];

        // Initialize the game engine, passing all necessary components
        this.gameEngine = new GameEngine(config, deck, cardManager, gameUI, logManager, observerManager,
                scoringManager, playerStrategies, scores);

        // Set up card listeners for the human player (player 0)
        gameEngine.setupCardListeners();
    }

    // Starts the game by calling the game engine's playGame method
    public void startGame() {
        gameEngine.playGame();
    }

    // Get the final scores after the game ends
    public int[] getScores() {
        return gameEngine.getScores();
    }

    // Get the winners of the game based on the final scores
    public List<Integer> getWinners() {
        return gameEngine.getWinners();
    }

    // Get the log of game events for analysis or debugging
    public String getLogResult() {
        return logManager.getLogResult();
    }
}
