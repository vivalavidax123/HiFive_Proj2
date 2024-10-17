package hifive;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CleverPlayerStrategy implements PlayerStrategy {
    private final Set<Card> playedCards = new HashSet<>();

    @Override
    public Card playCard(Hand hand, CardManager cardManager) {
        List<Card> cards = hand.getCardList();
        Card bestCard = null;
        int maxScore = Integer.MIN_VALUE;

        for(Card card : cards) {
            int potentialScore = calculatePotentialScore(card, hand);
            if(potentialScore > maxScore) {
                maxScore = potentialScore;
                bestCard = card;
            }
        }

        playedCards.add(bestCard);
        return bestCard;
    }

    private int calculatePotentialScore(Card card, Hand hand) {
        // Implement logic to calculate potential score based on played cards and current hand
        // This is a simplified example and should be expanded for better clever play
        Rank rank = (Rank)card.getRank();
        Suit suit = (Suit)card.getSuit();
        int score = rank.getRankCardValue() + suit.getBonusFactor();

        // Consider the probability of getting a good combination
        if(canFormFive(card, hand)) {
            score += 50;
        }

        // Adjust score based on played cards
        if(!playedCards.contains(card)) {
            score += 10;
        }

        return score;
    }

    private boolean canFormFive(Card card, Hand hand) {
        // Check if the card can form a five combination with other cards in hand
        // This is a simplified check and should be expanded for better clever play
        Rank rank = (Rank)card.getRank();
        return hand.getCardList().stream().anyMatch(c -> ((Rank)c.getRank()).getRankCardValue() + rank.getRankCardValue() == 5);
    }

    public void updatePlayedCards(Card playedCard) {
        playedCards.add(playedCard);
    }
}