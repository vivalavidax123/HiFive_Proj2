package hifive;

import hifive.GameEngine.GameComponentFactory;
import hifive.GameEngine.ScoringStrategy;
import hifive.GameEngine.StandardGameComponentFactory;
import hifive.PlayerComponent.PlayerStrategy;

import java.util.List;

public class GameSetup {
    private final GameConfigurations config;
    private final List<ScoringStrategy> scoringStrategies;
    private final PlayerStrategy[] playerStrategies;

    public GameSetup(GameConfigurations config) {
        this.config = config;
        GameComponentFactory factory = new StandardGameComponentFactory();
        this.scoringStrategies = factory.createScoringStrategies(config);
        this.playerStrategies = factory.createPlayerStrategies(config);
    }

    public List<ScoringStrategy> getScoringStrategies() {
        return scoringStrategies;
    }

    public PlayerStrategy[] getPlayerStrategies() {
        return playerStrategies;
    }
}
