package hifive.CardComponent;

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;

public interface ICardManager {
    Card getRandomCard(Hand hand);
    void dealACardToHand(Hand hand);
    Hand getPack();
    Card getCardFromList(ArrayList<Card> cardList, String initialCard);
    Card randomCard(ArrayList<Card> cardList);
    Card applyAutoMovement(Hand nextHand, String nextMovement);
}
