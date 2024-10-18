package hifive;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import hifive.CardComponent.CardManager;
import hifive.Enumeration.Rank;

import java.util.Comparator;
import java.util.List;

public class BasicPlayerStrategy implements PlayerStrategy {

    // Selects a card to play based on a basic strategy
    @Override
    public Card playCard(Hand hand, CardManager cardManager) {
        List<Card> cards = hand.getCardList();
        List<Card> valueCards = cards.stream().filter(card -> !isPictureCard((Rank)card.getRank())).toList();

        if(!valueCards.isEmpty()) {
            return valueCards.stream().min(Comparator.comparing((Card c) -> c.getSuit().ordinal()).thenComparing((Card c) -> ((Rank)c.getRank()).getRankCardValue(), Comparator.reverseOrder())).orElse(null);
        } else {
            return cards.stream().min(Comparator.comparing((Card c) -> c.getRank().ordinal()).thenComparing((Card c) -> c.getSuit().ordinal())).orElse(null);
        }
    }

    // Checks if the given rank is a picture card (Ace, Jack, Queen, or King)
    private boolean isPictureCard(Rank rank) {
        return rank == Rank.ACE || rank == Rank.JACK || rank == Rank.QUEEN || rank == Rank.KING;
    }
}
