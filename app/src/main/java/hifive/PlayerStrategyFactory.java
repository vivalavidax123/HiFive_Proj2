package hifive;

public class PlayerStrategyFactory {
    public static PlayerStrategy createStrategy(String playerType, GameConfigurations config) {
        switch(playerType.toLowerCase()) {
            case "human":
                return new HumanPlayerStrategy(config);
            case "basic":
                return new BasicPlayerStrategy();
            case "clever":
                return new CleverPlayerStrategy();
            default:
                return new RandomPlayerStrategy();
        }
    }
}