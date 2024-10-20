package hifive.Managers;

import ch.aplu.jcardgame.*;
import hifive.Enum.Rank;
import hifive.Enum.Suit;
import hifive.Game.GameConfigurations;

import java.util.*;
import static ch.aplu.jgamegrid.GameGrid.delay;

public class CardManager {
    private final GameConfigurations gameConfig;
    private final Random random;
    private final Deck deck;
    private final Hand pack;

    public CardManager(GameConfigurations config) {
        this.gameConfig = config;
        this.random = new Random(GameConfigurations.SEED);
        this.deck = new Deck(Suit.values(), Rank.values(), "cover");
        this.pack = deck.toHand(false);
    }

    // Select a random card from the given list
    public Card randomCard(ArrayList<Card> list) {
        if (list.isEmpty()) {
            return null;
        }
        int x = random.nextInt(list.size());
        return list.get(x);
    }

    // Get a specific card from the list based on its name
    Card getCardFromList(List<Card> cards, String cardName) {
        Rank cardRank = Rank.getRankFromString(cardName);
        Suit cardSuit = Suit.getSuitFromString(cardName);
        for (Card card : cards) {
            if (card.getSuit() == cardSuit && card.getRank() == cardRank) {
                return card;
            }
        }
        return null;
    }

    // Deal a card to the given hand
    public void dealACardToHand(Hand hand) {
        if (pack.isEmpty())
            return;
        Card dealt = randomCard(pack.getCardList());
        dealt.removeFromHand(false);
        hand.insert(dealt, true);
    }

    // Get a random card from the given hand
    public Card getRandomCard(Hand hand) {
        dealACardToHand(hand);
        delay(gameConfig.thinkingTime);
        int x = random.nextInt(hand.getCardList().size());
        return hand.getCardList().get(x);
    }

    // Getter for pack
    public Hand getPack() {
        return pack;
    }

    // Apply automatic movement for a player
    public Card applyAutoMovement(Hand hand, String nextMovement) {
        if (pack.isEmpty())
            return null;
        String[] cardStrings = nextMovement.split("-");
        String cardDealtString = cardStrings[0];
        Card dealt = getCardFromList(pack.getCardList(), cardDealtString);
        if (dealt != null) {
            dealt.removeFromHand(false);
            hand.insert(dealt, true);
        } else {
            System.out.println("cannot draw card: " + cardDealtString + " - hand: " + hand);
        }

        if (cardStrings.length > 1) {
            String cardDiscardString = cardStrings[1];
            return getCardFromList(hand.getCardList(), cardDiscardString);
        } else {
            return null;
        }
    }
    // Initializes player hands
    public Hand[] initHands(int numPlayers) {
        Hand[] hands = new Hand[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            hands[i] = new Hand(deck);
        }
        return hands;
    }

    // Deals cards to players based on the configuration
    public void dealingOut(Hand[] hands, GameConfigurations config) {
        for (int i = 0; i < GameConfigurations.NB_PLAYERS; i++) {
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
                Card card = getCardFromList(getPack().getCardList(), initialCard);
                if (card != null) {
                    card.removeFromHand(false);
                    hands[i].insert(card, false);
                }
            }
        }

        for (int i = 0; i < GameConfigurations.NB_PLAYERS; i++) {
            int cardsToDeal = GameConfigurations.NB_START_CARDS - hands[i].getNumberOfCards();
            for (int j = 0; j < cardsToDeal; j++) {
                if (pack.isEmpty())
                    return;
                Card dealt = randomCard(getPack().getCardList());
                dealt.removeFromHand(false);
                hands[i].insert(dealt, false);
            }
        }
    }
}
