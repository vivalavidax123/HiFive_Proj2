package hifive;

import ch.aplu.jcardgame.*;
import java.util.*;
import static ch.aplu.jgamegrid.GameGrid.delay;

public class GameEngine {
    private final GameConfigurations config;
    private final CardManager cardManager;
    private final UIManager gameUI;
    private final ILogManager logManager;
    private final IObserverManager observerManager;
    private final ScoringManager scoringManager;
    private final PlayerStrategy[] playerStrategies;
    private final int[] scores;
    private final List<List<String>> playerAutoMovements;
    private final Hand[] hands;
    private final Hand playingArea;
    private final Hand pack;
    private final int[] autoIndexHands;
    private Card selected;
    private List<Integer> winners;

    public GameEngine(GameConfigurations config, CardManager cardManager, UIManager gameUI, ILogManager logManager,
                      IObserverManager observerManager, ScoringManager scoringManager, PlayerStrategy[] playerStrategies,
                      int[] scores, List<List<String>> playerAutoMovements, Hand[] hands, Hand playingArea, Hand pack) {
        this.config = config;
        this.cardManager = cardManager;
        this.gameUI = gameUI;
        this.logManager = logManager;
        this.observerManager = observerManager;
        this.scoringManager = scoringManager;
        this.playerStrategies = playerStrategies;
        this.scores = scores;
        this.playerAutoMovements = playerAutoMovements;
        this.hands = hands;
        this.playingArea = playingArea;
        this.pack = pack;
        this.autoIndexHands = new int[config.NB_PLAYERS];
    }

    // Main game loop controlling the flow of the game
    public void playGame() {
        gameUI.setStatus("Initializing...");  // Moved setStatus here

        initScores();
        gameUI.initScore();

        int roundNumber = 1;
        for (int i = 0; i < config.NB_PLAYERS; i++)
            updateScore(i);

        logManager.addRoundInfoToLog(roundNumber);
        observerManager.notifyRoundStart(roundNumber);

        int nextPlayer = 0;
        while (roundNumber <= 4) {
            selected = null;
            boolean finishedAuto = false;

            if (config.isAuto) {
                int nextPlayerAutoIndex = autoIndexHands[nextPlayer];
                List<String> nextPlayerMovement = playerAutoMovements.get(nextPlayer);
                String nextMovement = "";

                if (nextPlayerMovement.size() > nextPlayerAutoIndex) {
                    nextMovement = nextPlayerMovement.get(nextPlayerAutoIndex);
                    nextPlayerAutoIndex++;

                    autoIndexHands[nextPlayer] = nextPlayerAutoIndex;
                    Hand nextHand = hands[nextPlayer];

                    selected = cardManager.applyAutoMovement(nextHand, nextMovement);
                    delay(config.delayTime);
                    if (selected != null) {
                        selected.removeFromHand(true);
                    } else {
                        selected = cardManager.getRandomCard(hands[nextPlayer]);
                        selected.removeFromHand(true);
                    }
                } else {
                    finishedAuto = true;
                }
            }

            if (!config.isAuto || finishedAuto) {
                if (0 == nextPlayer) {
                    hands[0].setTouchEnabled(true);

                    gameUI.setStatus("Player 0 is playing. Please double click on a card to discard");
                    selected = null;
                    cardManager.dealACardToHand(hands[0]);
                    while (null == selected)
                        delay(config.delayTime);
                    selected.removeFromHand(true);
                } else {
                    gameUI.setStatus("Player " + nextPlayer + " thinking...");
                    selected = playerStrategies[nextPlayer].playCard(hands[nextPlayer], cardManager);
                    selected.removeFromHand(true);
                }
            }

            logManager.addCardPlayedToLog(nextPlayer, hands[nextPlayer].getCardList());
            if (selected != null) {
                selected.setVerso(false);
                delay(config.delayTime);
                observerManager.notifyCardPlayed(nextPlayer, selected);
            }

            scores[nextPlayer] = scoringManager.calculateScoreForPlayer(hands[nextPlayer].getCardList());
            updateScore(nextPlayer);
            observerManager.notifyScoreUpdate(nextPlayer, scores[nextPlayer]);
            nextPlayer = (nextPlayer + 1) % config.NB_PLAYERS;

            if (nextPlayer == 0) {
                roundNumber++;
                logManager.addEndOfRoundToLog(scores);

                if (roundNumber <= 4) {
                    logManager.addRoundInfoToLog(roundNumber);
                    observerManager.notifyRoundStart(roundNumber);
                }
            }

            if (roundNumber > 4) {
                calculateScoreEndOfRound();
            }
            delay(config.delayTime);
        }

        finalizeGame();
    }

    // Reset all player scores to zero
    private void initScores() {
        Arrays.fill(scores, 0);
    }

    // Calculates and updates scores for all players at the end of the game
    private void calculateScoreEndOfRound() {
        for (int i = 0; i < hands.length; i++) {
            scores[i] = scoringManager.calculateScoreForPlayer(hands[i].getCardList());
        }
    }

    // Updates the score display for a specific player
    private void updateScore(int player) {
        gameUI.updateScore(player, scores[player]);
    }

    // Set up card listeners for the human player's hand
    public void setupCardListeners() {
        CardListener cardListener = new CardAdapter() {
            public void leftDoubleClicked(Card card) {
                selected = card;
                hands[0].setTouchEnabled(false);
            }
        };
        hands[0].addCardListener(cardListener);
    }

    // Finalize the game, calculate winners, and update UI
    private void finalizeGame() {
        for (int i = 0; i < config.NB_PLAYERS; i++)
            updateScore(i);
        int maxScore = Arrays.stream(scores).max().orElse(0);
        winners = new ArrayList<>();
        for (int i = 0; i < config.NB_PLAYERS; i++)
            if (scores[i] == maxScore)
                winners.add(i);

        gameUI.showGameOver(winners);
        logManager.addEndOfGameToLog(scores, winners);
        observerManager.notifyGameOver(scores, winners);
    }

    // Getters for scores and winners
    public int[] getScores() {
        return scores;
    }

    public List<Integer> getWinners() {
        return winners;
    }
}
