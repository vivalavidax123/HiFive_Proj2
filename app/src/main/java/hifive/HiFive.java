package hifive;

import ch.aplu.jcardgame.*;

import java.util.*;

@SuppressWarnings("serial")
public class HiFive extends CardGame {
    // Configuration and game components
    private final GameConfigurations config;
    private final Deck deck;
    private final CardManager cardManager;
    private final UIManager gameUI;
    private final LogManager logManager = new LogManager();

    // Player-related fields
    private final int[] scores;
    private final int[] autoIndexHands;
    private final PlayerStrategy[] playerStrategies;
    private final List<List<String>> playerAutoMovements = new ArrayList<>();

    // Game state
    private Hand[] hands;
    private Hand playingArea;
    private Hand pack;
    private Card selected;

    // Scoring and observers
    private final List<ScoringStrategy> scoringStrategies;
    private final ObserverManager observerManager = new ObserverManager();

    // Constructor
    public HiFive(Properties properties) {
        super(700, 700, 30);
        // Initialize game components
        this.config = new GameConfigurations(properties);
        this.deck = new Deck(Suit.values(), Rank.values(), "cover");
        this.cardManager = new CardManager(config);
        this.gameUI = new UIManager(config, this);

        // Initialize player-related fields
        this.scores = new int[config.NB_PLAYERS];
        this.autoIndexHands = new int[config.NB_PLAYERS];

        // Create game components using factory
        GameComponentFactory factory = new StandardGameComponentFactory();
        this.scoringStrategies = factory.createScoringStrategies(config);
        this.playerStrategies = factory.createPlayerStrategies(config);
    }

    // Initialize hands, playing area, and deal cards
    // Set up card layout and add card listeners for player interactions
    private void initGame() {
        hands = new Hand[config.NB_PLAYERS];
        for(int i = 0; i < config.NB_PLAYERS; i++) {
            hands[i] = new Hand(deck);
        }
        playingArea = new Hand(deck);
        dealingOut(hands);

        for(int i = 0; i < config.NB_PLAYERS; i++) {
            hands[i].sort(Hand.SortType.SUITPRIORITY, false);
        }

        CardListener cardListener = new CardAdapter() {
            public void leftDoubleClicked(Card card) {
                selected = card;
                hands[0].setTouchEnabled(false);
            }
        };
        hands[0].addCardListener(cardListener);

        gameUI.setupCardLayout(hands, playingArea);
    }

    // Initialize the score display in the game UI
    private void initScore() {
        gameUI.initScore();
    }

    // Reset all player scores to zero
    private void initScores() {
        Arrays.fill(scores, 0);
    }

    // Deal initial cards based on configuration
    // Randomly deal remaining cards to reach the starting hand size
    private void dealingOut(Hand[] hands) {
        pack = deck.toHand(false);

        for(int i = 0; i < config.NB_PLAYERS; i++) {
            String initialCardsKey = "players." + i + ".initialcards";
            String initialCardsValue = config.properties.getProperty(initialCardsKey);
            if(initialCardsValue == null) {
                continue;
            }
            String[] initialCards = initialCardsValue.split(",");
            for(String initialCard : initialCards) {
                if(initialCard.length() <= 1) {
                    continue;
                }
                Card card = cardManager.getCardFromList(cardManager.getPack().getCardList(), initialCard);
                if(card != null) {
                    card.removeFromHand(false);
                    hands[i].insert(card, false);
                }
            }
        }

        for(int i = 0; i < config.NB_PLAYERS; i++) {
            int cardsToDealt = config.NB_START_CARDS - hands[i].getNumberOfCards();
            for(int j = 0; j < cardsToDealt; j++) {
                if(pack.isEmpty())
                    return;
                Card dealt = cardManager.randomCard(cardManager.getPack().getCardList());
                dealt.removeFromHand(false);
                hands[i].insert(dealt, false);
            }
        }
    }

    // Set up automatic movements for players based on configuration
    // Used for testing and simulating player actions
    private void setupPlayerAutoMovements() {
        String[] playerMovements = new String[4];
        for(int i = 0; i < 4; i++) {
            playerMovements[i] = config.properties.getProperty("players." + i + ".cardsPlayed", "");
        }

        for(String movementString : playerMovements) {
            List<String> movements = Arrays.asList(movementString.split(","));
            playerAutoMovements.add(movements);
        }
    }

    // Main game loop controlling the flow of the game
    // Handle player turns, card selection, scoring, and round progression
    private void playGame() {
        int roundNumber = 1;
        for(int i = 0; i < config.NB_PLAYERS; i++)
            updateScore(i);

        List<Card> cardsPlayed = new ArrayList<>();
        logManager.addRoundInfoToLog(roundNumber);
        observerManager.notifyRoundStart(roundNumber);

        int nextPlayer = 0;
        while(roundNumber <= 4) {
            selected = null;
            boolean finishedAuto = false;

            if(config.isAuto) {
                int nextPlayerAutoIndex = autoIndexHands[nextPlayer];
                List<String> nextPlayerMovement = playerAutoMovements.get(nextPlayer);
                String nextMovement = "";

                if(nextPlayerMovement.size() > nextPlayerAutoIndex) {
                    nextMovement = nextPlayerMovement.get(nextPlayerAutoIndex);
                    nextPlayerAutoIndex++;

                    autoIndexHands[nextPlayer] = nextPlayerAutoIndex;
                    Hand nextHand = hands[nextPlayer];

                    selected = cardManager.applyAutoMovement(nextHand, nextMovement);
                    delay(config.delayTime);
                    if(selected != null) {
                        selected.removeFromHand(true);
                    } else {
                        selected = cardManager.getRandomCard(hands[nextPlayer]);
                        selected.removeFromHand(true);
                    }
                } else {
                    finishedAuto = true;
                }
            }

            if(!config.isAuto || finishedAuto) {
                if(0 == nextPlayer) {
                    hands[0].setTouchEnabled(true);

                    gameUI.setStatus("Player 0 is playing. Please double click on a card to discard");
                    selected = null;
                    cardManager.dealACardToHand(hands[0]);
                    while(null == selected)
                        delay(config.delayTime);
                    selected.removeFromHand(true);
                } else {
                    gameUI.setStatus("Player " + nextPlayer + " thinking...");
                    selected = cardManager.getRandomCard(hands[nextPlayer]);
                    selected.removeFromHand(true);
                }
            }

            logManager.addCardPlayedToLog(nextPlayer, hands[nextPlayer].getCardList());
            if(selected != null) {
                cardsPlayed.add(selected);
                selected.setVerso(false);
                delay(config.delayTime);
                observerManager.notifyCardPlayed(nextPlayer, selected);
            }

            scores[nextPlayer] = scoreForHiFive(nextPlayer);
            updateScore(nextPlayer);
            observerManager.notifyScoreUpdate(nextPlayer, scores[nextPlayer]);
            nextPlayer = (nextPlayer + 1) % config.NB_PLAYERS;

            if(nextPlayer == 0) {
                roundNumber++;
                logManager.addEndOfRoundToLog(scores);

                if(roundNumber <= 4) {
                    logManager.addRoundInfoToLog(roundNumber);
                    observerManager.notifyRoundStart(roundNumber);
                }
            }

            if(roundNumber > 4) {
                calculateScoreEndOfRound();
            }
            delay(config.delayTime);
        }
    }

    // Calculate and update scores for all players at the end of a round
    private void calculateScoreEndOfRound() {
        for(int i = 0; i < hands.length; i++) {
            scores[i] = scoreForHiFive(i);
        }
    }

    // Calculate the score for a specific player based on their hand
    private int scoreForHiFive(int playerIndex) {
        List<Card> privateCards = hands[playerIndex].getCardList();
        return scoringStrategies.stream().mapToInt(strategy -> strategy.calculateScore(privateCards)).max().orElse(0);
    }

    // Update the score display for a specific player
    private void updateScore(int player) {
        gameUI.updateScore(player, scores[player]);
    }

    // Main method to run the HiFive game application
    // Control overall flow: initialization, gameplay, and end game
    public String runApp() {
        setTitle("HiFive (V" + config.VERSION + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        gameUI.setStatus("Initializing...");
        initScores();
        initScore();
        setupPlayerAutoMovements();
        initGame();
        playGame();

        for(int i = 0; i < config.NB_PLAYERS; i++)
            updateScore(i);
        int maxScore = Arrays.stream(scores).max().orElse(0);
        List<Integer> winners = new ArrayList<>();
        for(int i = 0; i < config.NB_PLAYERS; i++)
            if(scores[i] == maxScore)
                winners.add(i);

        gameUI.showGameOver(winners);
        logManager.addEndOfGameToLog(scores, winners);
        observerManager.notifyGameOver(scores, winners);

        return logManager.getLogResult();
    }
}
