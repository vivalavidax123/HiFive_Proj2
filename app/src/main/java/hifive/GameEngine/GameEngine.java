package hifive.GameEngine;

import ch.aplu.jcardgame.*;
import hifive.CardComponent.ICardManager;
import hifive.GameObserver;
import hifive.LogComponent.ILogManager;
import hifive.UIComponent.IUIManager;
import hifive.GameConfigurations;

import java.util.*;

public class GameEngine {
    // Configuration and game components
    private final GameConfigurations config;
    private final List<ScoringStrategy> scoringStrategies;
    private final ICardManager cardManager;
    private final IUIManager gameUI;
    private final ILogManager logManager;

    // Player-related fields
    private final int[] scores;
    private final int[] autoIndexHands;
    private final List<List<String>> playerAutoMovements = new ArrayList<>();

    // Game state
    private final Hand[] hands;

    // Observers and utilities
    private final List<GameObserver> observers;
    private final IGameUtilities gameUtilities; // Reference to utilities

    // Constructor
    public GameEngine(GameConfigurations config, List<ScoringStrategy> scoringStrategies, ICardManager cardManager,
                      IUIManager gameUI, ILogManager logManager, Hand[] hands,
                      List<GameObserver> observers, IGameUtilities gameUtilities) {
        this.config = config;
        this.scoringStrategies = scoringStrategies;
        this.cardManager = cardManager;
        this.gameUI = gameUI;
        this.logManager = logManager;
        this.hands = hands;
        this.scores = new int[GameConfigurations.NB_PLAYERS];
        this.autoIndexHands = new int[GameConfigurations.NB_PLAYERS];
        this.observers = observers;
        this.gameUtilities = gameUtilities;
    }


    // Initialize scores
    public void initScores() {
        Arrays.fill(scores, 0);
    }

    // Set up automatic movements for players based on configuration
    public void setupPlayerAutoMovements() {
        for (int i = 0; i < GameConfigurations.NB_PLAYERS; i++) {
            String movements = config.properties.getProperty("players." + i + ".cardsPlayed", "");
            List<String> movementList = Arrays.asList(movements.split(","));
            playerAutoMovements.add(movementList);
        }
    }

    // Deal initial cards to players
    public void dealingOut() {
        Hand pack = cardManager.getPack();

        for (int i = 0; i < GameConfigurations.NB_PLAYERS; i++) {
            String initialCardsKey = "players." + i + ".initialcards";
            String initialCardsValue = config.properties.getProperty(initialCardsKey);
            if (initialCardsValue != null) {
                String[] initialCards = initialCardsValue.split(",");
                for (String initialCard : initialCards) {
                    if (initialCard.length() > 1) {
                        Card card = cardManager.getCardFromList(pack.getCardList(), initialCard);
                        if (card != null) {
                            card.removeFromHand(false);
                            hands[i].insert(card, false);
                        }
                    }
                }
            }
        }

        for (int i = 0; i < GameConfigurations.NB_PLAYERS; i++) {
            int cardsToDeal = GameConfigurations.NB_START_CARDS - hands[i].getNumberOfCards();
            for (int j = 0; j < cardsToDeal; j++) {
                if (pack.isEmpty())
                    return;
                Card dealt = cardManager.randomCard(pack.getCardList());
                dealt.removeFromHand(false);
                hands[i].insert(dealt, false);
            }
        }
    }

    // Main game loop
    public void playGame() {
        int roundNumber = 1;
        for (int i = 0; i < GameConfigurations.NB_PLAYERS; i++)
            updateScore(i);

        logManager.addRoundInfoToLog(roundNumber);
        notifyRoundStart(roundNumber);

        int nextPlayer = 0;
        while (roundNumber <= 4) {
            Card selected = null;
            boolean finishedAuto = false;

            if (config.isAuto) {
                int nextPlayerAutoIndex = autoIndexHands[nextPlayer];
                List<String> nextPlayerMovement = playerAutoMovements.get(nextPlayer);
                String nextMovement;

                if (nextPlayerMovement.size() > nextPlayerAutoIndex) {
                    nextMovement = nextPlayerMovement.get(nextPlayerAutoIndex);
                    nextPlayerAutoIndex++;

                    autoIndexHands[nextPlayer] = nextPlayerAutoIndex;
                    Hand nextHand = hands[nextPlayer];

                    selected = cardManager.applyAutoMovement(nextHand, nextMovement);
                    gameUtilities.delay(config.delayTime);
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
                    cardManager.dealACardToHand(hands[0]);
                    while (null == gameUtilities.getSelectedCard())
                        gameUtilities.delay(config.delayTime);
                    selected = gameUtilities.getSelectedCard();
                    gameUtilities.setSelectedCard(null);
                    selected.removeFromHand(true);
                    hands[0].setTouchEnabled(false);
                } else {
                    gameUI.setStatus("Player " + nextPlayer + " is thinking...");
                    gameUtilities.delay(config.delayTime);
                    selected = cardManager.getRandomCard(hands[nextPlayer]);
                    selected.removeFromHand(true);
                }
            }

            logManager.addCardPlayedToLog(nextPlayer, hands[nextPlayer].getCardList());
            selected.setVerso(false);
            gameUtilities.delay(config.delayTime);
            notifyCardPlayed(nextPlayer, selected);

            scores[nextPlayer] = scoreForHiFive(nextPlayer);
            updateScore(nextPlayer);
            notifyScoreUpdate(nextPlayer, scores[nextPlayer]);
            nextPlayer = (nextPlayer + 1) % GameConfigurations.NB_PLAYERS;

            if (nextPlayer == 0) {
                roundNumber++;
                logManager.addEndOfRoundToLog(scores);

                if (roundNumber <= 4) {
                    logManager.addRoundInfoToLog(roundNumber);
                    notifyRoundStart(roundNumber);
                }
            }

            if (roundNumber > 4) {
                calculateScoreEndOfRound();
            }
            gameUtilities.delay(config.delayTime);
        }
    }

    // Calculate final scores at the end of the game
    public void calculateScoreEndOfRound() {
        for (int i = 0; i < hands.length; i++) {
            scores[i] = scoreForHiFive(i);
        }
    }

    // Calculate the score for a specific player
    public int scoreForHiFive(int playerIndex) {
        List<Card> privateCards = hands[playerIndex].getCardList();
        return scoringStrategies.stream()
                .mapToInt(strategy -> strategy.calculateScore(privateCards))
                .max()
                .orElse(0);
    }

    // Update the UI with the new score
    public void updateScore(int player) {
        gameUI.updateScore(player, scores[player]);
    }

    // Get the final scores
    public int[] getFinalScores() {
        return scores;
    }

    // Observer notifications
    private void notifyRoundStart(int roundNumber) {
        for (GameObserver observer : observers) {
            observer.onRoundStart(roundNumber);
        }
    }

    private void notifyCardPlayed(int player, Card card) {
        for (GameObserver observer : observers) {
            observer.onCardPlayed(player, card);
        }
    }

    private void notifyScoreUpdate(int player, int newScore) {
        for (GameObserver observer : observers) {
            observer.onScoreUpdate(player, newScore);
        }
    }
}
