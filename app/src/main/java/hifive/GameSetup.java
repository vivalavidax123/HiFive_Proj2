package hifive;

import hifive.GameEngine.GameComponentFactory;
import hifive.GameEngine.ScoringStrategy;
import hifive.GameEngine.StandardGameComponentFactory;

import java.util.List;

public class GameSetup {
    private final List<ScoringStrategy> scoringStrategies;

    public GameSetup(GameConfigurations config) {
        GameComponentFactory factory = new StandardGameComponentFactory();
        this.scoringStrategies = factory.createScoringStrategies(config);
    }

    public List<ScoringStrategy> getScoringStrategies() {
        return scoringStrategies;
    }
}
