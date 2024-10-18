package hifive.PlayerComponent;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;
import hifive.CardComponent.CardManager;
import hifive.Enumeration.Rank;
import hifive.Enumeration.Suit;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

// This class implements a more advanced strategy for playing cards in the HiFive game
public class CleverPlayerStrategy implements PlayerStrategy {
    // Keep track of cards that have been played
    private final Set<Card> playedCards = new HashSet<>();

    @Override
    public Card playCard(Hand hand, CardManager cardManager) {
        List<Card> cards = hand.getCardList();
        Card bestCard = null;
        int maxScore = Integer.MIN_VALUE;

        // Evaluate each card in the hand to find the best one to play
        for(Card card : cards) {
            int potentialScore = calculatePotentialScore(card, hand);
            if(potentialScore > maxScore) {
                maxScore = potentialScore;
                bestCard = card;
            }
        }

        // Add the chosen card to the set of played cards
        playedCards.add(bestCard);
        return bestCard;
    }

    // Calculate a potential score for playing a specific card
    private int calculatePotentialScore(Card card, Hand hand) {
        Rank rank = (Rank)card.getRank();
        Suit suit = (Suit)card.getSuit();
        int score = rank.getRankCardValue() + suit.getBonusFactor();

        // Bonus points if the card can form a "five" combination
        if(canFormFive(card, hand)) {
            score += 50;
        }

        // Bonus points for playing a card that hasn't been played before
        if(!playedCards.contains(card)) {
            score += 10;
        }

        return score;
    }

    // Check if the card can form a "five" combination with other cards in hand
    private boolean canFormFive(Card card, Hand hand) {
        Rank rank = (Rank)card.getRank();
        return hand.getCardList().stream()
            .anyMatch(c -> ((Rank)c.getRank()).getRankCardValue() + rank.getRankCardValue() == 5);
    }

    // Update the set of played cards (can be called externally to update strategy)
    public void updatePlayedCards(Card playedCard) {
        playedCards.add(playedCard);
    }
}
