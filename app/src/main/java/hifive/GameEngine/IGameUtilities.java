package hifive.GameEngine;

import ch.aplu.jcardgame.Card;

public interface IGameUtilities {
    void delay(int time);
    Card getSelectedCard();
    void setSelectedCard(Card card);
}
