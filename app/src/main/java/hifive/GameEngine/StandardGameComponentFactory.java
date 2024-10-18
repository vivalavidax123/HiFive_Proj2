package hifive.GameEngine;
import hifive.GameConfigurations;
import hifive.PlayerComponent.*;

import java.util.ArrayList;
import java.util.List;

public class StandardGameComponentFactory implements GameComponentFactory {

    // Creates and returns a list of scoring strategies based on game configuration
    @Override
    public List<ScoringStrategy> createScoringStrategies(GameConfigurations config) {
        List<ScoringStrategy> strategies = new ArrayList<>();
        strategies.add(new FiveScoring(GameConfigurations.FIVE_GOAL, GameConfigurations.FIVE_POINTS));
        strategies.add(new SumFiveScoring(GameConfigurations.FIVE_GOAL, GameConfigurations.SUM_FIVE_POINTS));
        strategies.add(new DifferenceFiveScoring(GameConfigurations.FIVE_GOAL, GameConfigurations.DIFFERENCE_FIVE_POINTS));
        strategies.add(new NoneFiveScoring());
        return strategies;
    }

    // Creates and returns an array of player strategies based on game configuration
    @Override
    public PlayerStrategy[] createPlayerStrategies(GameConfigurations config) {
        PlayerStrategy[] strategies = new PlayerStrategy[GameConfigurations.NB_PLAYERS];
        for(int i = 0; i < GameConfigurations.NB_PLAYERS; i++) {
            String playerType = config.properties.getProperty("players." + i, "random").trim().toLowerCase();
            strategies[i] = createStrategy(playerType, config);
        }
        return strategies;
    }

    // Creates and returns a specific player strategy based on the player type
    private PlayerStrategy createStrategy(String playerType, GameConfigurations config) {
        return switch(playerType.toLowerCase()) {
            case "human" -> new HumanPlayerStrategy(config.delayTime);
            case "basic" -> new BasicPlayerStrategy();
            case "clever" -> new CleverPlayerStrategy();
            default -> new RandomPlayerStrategy();
        };
    }
}
