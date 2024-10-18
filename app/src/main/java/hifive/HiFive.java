package hifive;

import ch.aplu.jcardgame.*;
import hifive.CardComponent.CardManager;
import hifive.CardComponent.ICardManager;
import hifive.Enumeration.Rank;
import hifive.Enumeration.Suit;
import hifive.GameEngine.GameEngine;
import hifive.GameEngine.IGameUtilities;
import hifive.LogComponent.ILogManager;
import hifive.LogComponent.LogManager;
import hifive.UIComponent.IUIManager;
import hifive.UIComponent.UIManager;

import java.util.*;

public class HiFive extends CardGame implements IGameUtilities {
    // Configuration and game components
    private final GameConfigurations config;
    private final Random random;
    private final Deck deck;
    private final ICardManager cardManager;
    private final IUIManager gameUI;
    private final ILogManager logManager = LogManager.getInstance();

    // Game setup
    private final GameSetup gameSetup;

    // Player-related fields
    private final int[] scores;

    // Game state
    private Hand[] hands;
    private Hand playingArea;
    private Card selected;

    // Scoring and observers
    private final List<GameObserver> observers = new ArrayList<>();

    // Game engine
    private GameEngine gameEngine;

    // Constructor
    public HiFive(Properties properties) {
        super(700, 700, 30);
        // Initialize game components
        this.config = new GameConfigurations(properties);
        this.gameSetup = new GameSetup(config);
        this.random = new Random(config.SEED);
        this.deck = new Deck(Suit.values(), Rank.values(), "cover");
        this.cardManager = new CardManager(random, config);
        this.gameUI = new UIManager(config, this);

        // Initialize player-related fields
        this.scores = new int[config.NB_PLAYERS];
    }

    // Initialize hands, playing area, and game engine
    private void initGame() {
        hands = new Hand[config.NB_PLAYERS];
        for (int i = 0; i < config.NB_PLAYERS; i++) {
            hands[i] = new Hand(deck);
        }
        playingArea = new Hand(deck);

        // Initialize game engine
        gameEngine = new GameEngine(
                config,
                gameSetup.getScoringStrategies(),
                cardManager,
                gameUI,
                logManager,
                hands,
                observers,
                this
        );

        // Deal initial cards
        gameEngine.dealingOut();

        for (int i = 0; i < config.NB_PLAYERS; i++) {
            hands[i].sort(Hand.SortType.SUITPRIORITY, false);
        }

        // Add card listener for human player
        CardListener cardListener = new CardAdapter() {
            public void leftDoubleClicked(Card card) {
                selected = card;
                hands[0].setTouchEnabled(false);
            }
        };
        hands[0].addCardListener(cardListener);

        gameUI.setupCardLayout(hands, playingArea);

        // Initialize scores after gameEngine is initialized
        gameEngine.initScores();
    }

    // Initialize the score display in the game UI
    private void initScore() {
        gameUI.initScore();
    }

    // Implement IGameUtilities methods
    @Override
    public Card getSelectedCard() {
        return selected;
    }

    @Override
    public void setSelectedCard(Card card) {
        this.selected = card;
    }

    @Override
    public void delay(int time) {
        super.delay(time);
    }

    // Main method to run the HiFive game application
    public String runApp() {
        logManager.resetLog(); // Reset the log at the start of the game
        setTitle("HiFive (V" + config.VERSION + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        gameUI.setStatus("Initializing...");

        initGame(); // Initialize the game and gameEngine first
        initScore();
        gameEngine.setupPlayerAutoMovements();
        gameEngine.playGame();

        int[] finalScores = gameEngine.getFinalScores();

        for (int i = 0; i < config.NB_PLAYERS; i++)
            gameUI.updateScore(i, finalScores[i]);
        int maxScore = Arrays.stream(finalScores).max().orElse(0);
        List<Integer> winners = new ArrayList<>();
        for (int i = 0; i < config.NB_PLAYERS; i++)
            if (finalScores[i] == maxScore)
                winners.add(i);

        gameUI.showGameOver(winners);
        logManager.addEndOfGameToLog(finalScores, winners);
        notifyGameOver(finalScores, winners);

        return logManager.getLogResult();
    }

    // Notify all observers that the game has ended
    private void notifyGameOver(int[] finalScores, List<Integer> winners) {
        for (GameObserver observer : observers) {
            observer.onGameOver(finalScores, winners);
        }
    }
}
