package hifive;

public class PlayerStrategyFactory {
    public static PlayerStrategy[] createPlayerStrategies(GameConfigurations config) {
        PlayerStrategy[] strategies = new PlayerStrategy[config.NB_PLAYERS];
        for(int i = 0; i < config.NB_PLAYERS; i++) {
            String playerType = config.properties.getProperty("players." + i, "random").trim().toLowerCase();
            strategies[i] = createStrategy(playerType, config);
        }
        return strategies;
    }

    private static PlayerStrategy createStrategy(String playerType, GameConfigurations config) {
        return switch(playerType.toLowerCase()) {
            case "human" -> new HumanPlayerStrategy(config);
            case "basic" -> new BasicPlayerStrategy();
            case "clever" -> new CleverPlayerStrategy();
            default -> new RandomPlayerStrategy();
        };
    }
}
