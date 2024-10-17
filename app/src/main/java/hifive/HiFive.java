package hifive;

import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.Actor;
import ch.aplu.jgamegrid.TextActor;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
public class HiFive extends CardGame {

    private final GameConfig config;
    private final Random random;
    private final Deck deck;
    private final Actor[] scoreActors;
    private final int[] scores;
    private final int[] autoIndexHands;
    private final StringBuilder logResult = new StringBuilder();
    private final List<List<String>> playerAutoMovements = new ArrayList<>();
    private final CardManager cardManager;
    private final PlayerStrategy[] playerStrategies;
    private Hand[] hands;
    private Hand playingArea;
    private Hand pack;
    private Card selected;
    private List<ScoringStrategy> scoringStrategies;

    public HiFive(Properties properties) {
        super(700, 700, 30);
        this.config = new GameConfig(properties);
        this.random = new Random(config.SEED);
        this.deck = new Deck(Suit.values(), Rank.values(), "cover");
        this.scoreActors = new Actor[config.NB_PLAYERS];
        this.scores = new int[config.NB_PLAYERS];
        this.autoIndexHands = new int[config.NB_PLAYERS];
        this.cardManager = new CardManager(random, config);
        this.playerStrategies = new PlayerStrategy[config.NB_PLAYERS];
        initializeScoringStrategies();
        initializePlayerStrategies();
    }

    // return random Enum value
    public <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    // ==================== 2. Game Setup and Initialization ====================
    private void initGame() {
        hands = new Hand[config.NB_PLAYERS];
        for(int i = 0; i < config.NB_PLAYERS; i++) {
            hands[i] = new Hand(deck);
        }
        playingArea = new Hand(deck);
        dealingOut(hands);
        playingArea.setView(this, new RowLayout(config.TRICK_LOCATION, (playingArea.getNumberOfCards() + 2) * config.TRICK_WIDTH));
        playingArea.draw();

        for(int i = 0; i < config.NB_PLAYERS; i++) {
            hands[i].sort(Hand.SortType.SUITPRIORITY, false);
        }

        // Set up human player for interaction
        CardListener cardListener = new CardAdapter() {
            public void leftDoubleClicked(Card card) {
                selected = card;
                hands[0].setTouchEnabled(false);
            }
        };
        hands[0].addCardListener(cardListener);

        // graphics
        RowLayout[] layouts = new RowLayout[config.NB_PLAYERS];
        for(int i = 0; i < config.NB_PLAYERS; i++) {
            layouts[i] = new RowLayout(config.HAND_LOCATIONS[i], config.HAND_WIDTH);
            layouts[i].setRotationAngle(90 * i);
            hands[i].setView(this, layouts[i]);
            hands[i].setTargetArea(new TargetArea(config.TRICK_LOCATION));
            hands[i].draw();
        }
    }

    private void initScore() {
        for(int i = 0; i < config.NB_PLAYERS; i++) {
            String text = "[" + scores[i] + "]";
            scoreActors[i] = new TextActor(text, Color.WHITE, bgColor, new Font("Arial", Font.BOLD, 36));
            addActor(scoreActors[i], config.SCORE_LOCATIONS[i]);
        }
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

    // ==================== 3. Game Logic ====================
    private void playGame() {
        int roundNumber = 1;
        for(int i = 0; i < config.NB_PLAYERS; i++)
            updateScore(i);

        List<Card> cardsPlayed = new ArrayList<>();
        addRoundInfoToLog(roundNumber);

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

                    // Apply movement for player
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

                    setStatus("Player 0 is playing. Please double click on a card to discard");
                    selected = null;
                    cardManager.dealACardToHand(hands[0]);
                    while(null == selected)
                        delay(config.delayTime);
                    selected.removeFromHand(true);
                } else {
                    setStatusText("Player " + nextPlayer + " thinking...");
                    selected = cardManager.getRandomCard(hands[nextPlayer]);
                    selected.removeFromHand(true);
                }
            }

            addCardPlayedToLog(nextPlayer, hands[nextPlayer].getCardList());
            if(selected != null) {
                cardsPlayed.add(selected);
                selected.setVerso(false);  // In case it is upside down
                delay(config.delayTime);
            }

            scores[nextPlayer] = scoreForHiFive(nextPlayer);
            updateScore(nextPlayer);
            nextPlayer = (nextPlayer + 1) % config.NB_PLAYERS;

            if(nextPlayer == 0) {
                roundNumber++;
                addEndOfRoundToLog();

                if(roundNumber <= 4) {
                    addRoundInfoToLog(roundNumber);
                }
            }

            if(roundNumber > 4) {
                calculateScoreEndOfRound();
            }
            delay(config.delayTime);
        }
    }

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
        scoringStrategies.add(new NoFiveScoring());
    }

    // ==================== 5. UI and Graphics ====================
    public void setStatus(String string) {
        setStatusText(string);
    }

    private void updateScore(int player) {
        removeActor(scoreActors[player]);
        int displayScore = Math.max(scores[player], 0);
        String text = "P" + player + "[" + displayScore + "]";
        scoreActors[player] = new TextActor(text, Color.WHITE, bgColor, new Font("Arial", Font.BOLD, 36));
        addActor(scoreActors[player], config.SCORE_LOCATIONS[player]);
    }

    // ==================== 6. Logging and Result Tracking ====================
    private void addCardPlayedToLog(int player, List<Card> cards) {
        if(cards.size() < 2) {
            return;
        }
        logResult.append("P").append(player).append("-");

        for(int i = 0; i < cards.size(); i++) {
            Rank cardRank = (Rank)cards.get(i).getRank();
            Suit cardSuit = (Suit)cards.get(i).getSuit();
            logResult.append(cardRank.getRankCardLog()).append(cardSuit.getSuitShortHand());
            if(i < cards.size() - 1) {
                logResult.append("-");
            }
        }
        logResult.append(",");
    }

    private void addRoundInfoToLog(int roundNumber) {
        logResult.append("Round").append(roundNumber).append(":");
    }

    private void addEndOfRoundToLog() {
        logResult.append("Score:");
        for(int score : scores) {
            logResult.append(score).append(",");
        }
        logResult.append("\n");
    }

    private void addEndOfGameToLog(List<Integer> winners) {
        logResult.append("EndGame:");
        for(int score : scores) {
            logResult.append(score).append(",");
        }
        logResult.append("\n");
        logResult.append("Winners:").append(winners.stream().map(String::valueOf).collect(Collectors.joining(", ")));
    }

    // ==================== 8. Main Game Control ====================
    public String runApp() {
        setTitle("HiFive (V" + config.VERSION + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        setStatusText("Initializing...");
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
        String winText;
        if(winners.size() == 1) {
            winText = "Game over. Winner is player: " + winners.get(0);
        } else {
            winText = "Game Over. Drawn winners are players: " + winners.stream().map(String::valueOf).collect(Collectors.joining(", "));
        }
        addActor(new Actor("sprites/gameover.gif"), config.TEXT_LOCATION);
        setStatusText(winText);
        refresh();
        addEndOfGameToLog(winners);

        return logResult.toString();
    }

    @Override
    public String getVersion() {
        return config.VERSION;
    }

    private void initializePlayerStrategies() {
        for(int i = 0; i < config.NB_PLAYERS; i++) {
            String playerType = config.properties.getProperty("players." + i, "random").trim().toLowerCase();
            switch(playerType) {
                case "human":
                    playerStrategies[i] = new HumanPlayerStrategy(config);
                    break;
                case "basic":
                    playerStrategies[i] = new BasicPlayerStrategy();
                    break;
                case "clever":
                    playerStrategies[i] = new CleverPlayerStrategy();
                    break;
                default:
                    playerStrategies[i] = new RandomPlayerStrategy();
            }
        }
    }
}
