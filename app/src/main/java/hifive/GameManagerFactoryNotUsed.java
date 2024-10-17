package hifive;

import ch.aplu.jcardgame.CardGame;

import java.util.Random;

public class GameManagerFactoryNotUsed {
    private static CardManager cardManager;
    private static UIManager uiManager;
    private static LogManager logManager;

    public static CardManager getCardManager(Random random, GameConfigurations config) {
        if (cardManager == null) {
            cardManager = new CardManager(random, config);
        }
        return cardManager;
    }

    public static UIManager getUIManager(GameConfigurations config, CardGame game) {
        if (uiManager == null) {
            uiManager = new UIManager(config, game);
        }
        return uiManager;
    }

    public static LogManager getLogManager() {
        if (logManager == null) {
            logManager = new LogManager();
        }
        return logManager;
    }
}