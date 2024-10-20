package hifive;

import ch.aplu.jcardgame.*;
import java.util.*;

public class GameInitializer {
    private final GameConfigurations config;
    private final Deck deck;
    private final CardManager cardManager;
    private final UIManager gameUI;
    private final List<List<String>> playerAutoMovements;
    private final Hand[] hands;
    private Hand playingArea;
    private Hand pack;

    public GameInitializer(GameConfigurations config, Deck deck, CardManager cardManager, UIManager gameUI) {
        this.config = config;
        this.deck = deck;
        this.cardManager = cardManager;
        this.gameUI = gameUI;
        this.playerAutoMovements = new ArrayList<>();
        this.hands = new Hand[config.NB_PLAYERS];
    }

    // Initializes the game by setting up hands, dealing cards, and setting up player movements
    public void initGame() {
        initHands();
        dealingOut();
        setupPlayerAutoMovements();
        setupCardLayout();
    }

    // Initializes player hands and the playing area
    private void initHands() {
        for (int i = 0; i < config.NB_PLAYERS; i++) {
            hands[i] = new Hand(deck);
        }
        playingArea = new Hand(deck);
        pack = deck.toHand(false);
    }

    // Deals cards to players based on the configuration
    private void dealingOut() {
        for (int i = 0; i < config.NB_PLAYERS; i++) {
            String initialCardsKey = "players." + i + ".initialcards";
            String initialCardsValue = config.properties.getProperty(initialCardsKey);
            if (initialCardsValue == null) {
                continue;
            }
            String[] initialCards = initialCardsValue.split(",");
            for (String initialCard : initialCards) {
                if (initialCard.length() <= 1) {
                    continue;
                }
                Card card = cardManager.getCardFromList(cardManager.getPack().getCardList(), initialCard);
                if (card != null) {
                    card.removeFromHand(false);
                    hands[i].insert(card, false);
                }
            }
        }

        for (int i = 0; i < config.NB_PLAYERS; i++) {
            int cardsToDeal = config.NB_START_CARDS - hands[i].getNumberOfCards();
            for (int j = 0; j < cardsToDeal; j++) {
                if (pack.isEmpty())
                    return;
                Card dealt = cardManager.randomCard(cardManager.getPack().getCardList());
                dealt.removeFromHand(false);
                hands[i].insert(dealt, false);
            }
        }
    }

    // Sets up automatic player movements for testing or simulations
    private void setupPlayerAutoMovements() {
        String[] playerMovements = new String[config.NB_PLAYERS];
        for (int i = 0; i < config.NB_PLAYERS; i++) {
            playerMovements[i] = config.properties.getProperty("players." + i + ".cardsPlayed", "");
        }

        for (String movementString : playerMovements) {
            List<String> movements = Arrays.asList(movementString.split(","));
            playerAutoMovements.add(movements);
        }
    }

    // Sets up the card layout in the UI
    private void setupCardLayout() {
        gameUI.setupCardLayout(hands, playingArea);
    }

    // Getters for initialized components
    public Hand[] getHands() {
        return hands;
    }

    public Hand getPlayingArea() {
        return playingArea;
    }

    public Hand getPack() {
        return pack;
    }

    public List<List<String>> getPlayerAutoMovements() {
        return playerAutoMovements;
    }
}
