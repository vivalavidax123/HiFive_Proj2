package hifive.Player;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import hifive.Enum.Rank;
import hifive.Managers.CardManager;

import java.util.Comparator;
import java.util.List;

import static ch.aplu.jgamegrid.GameGrid.delay;

public class BasicPlayerStrategy implements PlayerStrategy {

    // Selects a card to play based on a basic strategy
    @Override
    public Card playCard(Hand hand, CardManager cardManager) {
        cardManager.dealACardToHand(hand);
        delay(1000);
        List<Card> cards = hand.getCardList();

        //System.out.println("BasicPlayerStrategy - Cards in hand: " + cards);

        // Separate value cards and picture cards
        List<Card> valueCards = cards.stream()
                .filter(card -> !isPictureCard(Rank.valueOf(card.getRank().toString())))
                .toList();
        List<Card> pictureCards = cards.stream()
                .filter(card -> isPictureCard(Rank.valueOf(card.getRank().toString())))
                .toList();

        //System.out.println("Value cards: " + valueCards);
        //System.out.println("Picture cards: " + pictureCards);

        Card selectedCard;
        if (!valueCards.isEmpty()) {
            // Sort value cards by rank (descending)
            selectedCard = valueCards.stream()
                    .max(Comparator.comparing((Card c) -> {
                        Rank rank = Rank.valueOf(c.getRank().toString());
                        return rank.ordinal(); // Use ordinal for correct ordering
                    }))
                    .orElse(null);
        } else {
            // If only picture cards, sort by rank (A < J < Q < K) then by suit
            selectedCard = pictureCards.stream()
                    .min(Comparator.comparing((Card c) -> getPictureCardValue(Rank.valueOf(c.getRank().toString())))
                            .thenComparing((Card c) -> c.getSuit().ordinal()))
                    .orElse(null);
        }

        //System.out.println("Selected card: " + selectedCard);

        if (selectedCard != null) {
            selectedCard.removeFromHand(true); // Properly remove the card from the hand
        }
        return selectedCard;
    }

    // Checks if the given rank is a picture card (Ace, Jack, Queen, or King)
    private boolean isPictureCard(Rank rank) {
        return rank == Rank.ACE || rank == Rank.JACK || rank == Rank.QUEEN || rank == Rank.KING;
    }

    // Returns the value of a picture card (Ace, Jack, Queen, or King)
    private int getPictureCardValue(Rank rank) {
        return switch(rank) {
            case ACE -> 1;
            case JACK -> 2;
            case QUEEN -> 3;
            case KING -> 4;
            default -> 5; // This should never happen
        };
    }
}
