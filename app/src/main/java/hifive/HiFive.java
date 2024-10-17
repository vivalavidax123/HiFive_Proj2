package hifive;

import ch.aplu.jcardgame.*;

import java.util.*;

@SuppressWarnings("serial")
public class HiFive extends CardGame {

    // Configuration and game setup
    private final GameConfigurations config;
    private final Random random;
    private final Deck deck;
    private final CardManager cardManager;
    private final UIManager gameUI;

    // Player-related fields
    private final int[] scores;
    private final int[] autoIndexHands;
    private final PlayerStrategy[] playerStrategies;
    private final List<List<String>> playerAutoMovements = new ArrayList<>();
    // Logging
    private final LogManager logManager = new LogManager();
    // Game state
    private Hand[] hands;
    private Hand playingArea;
    private Hand pack;
    private Card selected;
    // Scoring
    private List<ScoringStrategy> scoringStrategies;

    // Constructor
    public HiFive(Properties properties) {
        super(700, 700, 30);
        this.config = new GameConfigurations(properties);
        this.random = new Random(config.SEED);
        this.deck = new Deck(Suit.values(), Rank.values(), "cover");
        this.scores = new int[config.NB_PLAYERS];
        this.autoIndexHands = new int[config.NB_PLAYERS];
        this.cardManager = new CardManager(random, config);
        this.playerStrategies = new PlayerStrategy[config.NB_PLAYERS];
        this.gameUI = new UIManager(config, this);
        initializeScoringStrategies();
        initializePlayerStrategies();
    }

    // Game initialization methods
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

    private void initScore() {
        gameUI.initScore();
    }

    private void initScores() {
        Arrays.fill(scores, 0);
    }

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

    // Main game loop
    private void playGame() {
        int roundNumber = 1;
        for(int i = 0; i < config.NB_PLAYERS; i++)
            updateScore(i);

        List<Card> cardsPlayed = new ArrayList<>();
        logManager.addRoundInfoToLog(roundNumber);

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
            }

            scores[nextPlayer] = scoreForHiFive(nextPlayer);
            updateScore(nextPlayer);
            nextPlayer = (nextPlayer + 1) % config.NB_PLAYERS;

            if(nextPlayer == 0) {
                roundNumber++;
                logManager.addEndOfRoundToLog(scores);

                if(roundNumber <= 4) {
                    logManager.addRoundInfoToLog(roundNumber);
                }
            }

            if(roundNumber > 4) {
                calculateScoreEndOfRound();
            }
            delay(config.delayTime);
        }
    }

    // Scoring methods
    private void calculateScoreEndOfRound() {
        for(int i = 0; i < hands.length; i++) {
            scores[i] = scoreForHiFive(i);
        }
    }

    private int scoreForHiFive(int playerIndex) {
        List<Card> privateCards = hands[playerIndex].getCardList();
        return scoringStrategies.stream().mapToInt(strategy -> strategy.calculateScore(privateCards)).max().orElse(0);
    }

    private void initializeScoringStrategies() {
        scoringStrategies = new ArrayList<>();
        scoringStrategies.add(new FiveScoring(config.FIVE_GOAL, config.FIVE_POINTS));
        scoringStrategies.add(new SumFiveScoring(config.FIVE_GOAL, config.SUM_FIVE_POINTS));
        scoringStrategies.add(new DifferenceFiveScoring(config.FIVE_GOAL, config.DIFFERENCE_FIVE_POINTS));
        scoringStrategies.add(new NoneFiveScoring());
    }

    private void initializePlayerStrategies() {
        for(int i = 0; i < config.NB_PLAYERS; i++) {
            String playerType = config.properties.getProperty("players." + i, "random").trim().toLowerCase();
            playerStrategies[i] = PlayerStrategyFactory.createStrategy(playerType, config);
        }
    }

    private void updateScore(int player) {
        gameUI.updateScore(player, scores[player]);
    }

    // Main application method
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

        return logManager.getLogResult();
    }
}