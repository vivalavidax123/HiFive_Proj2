package hifive;

import hifive.Player.PlayerStrategy;
import hifive.ScoringStrategy.ScoringStrategy;

import java.util.List;

public interface GameComponentFactory {
    // Creates and returns a list of scoring strategies based on the given game configuration
    List<ScoringStrategy> createScoringStrategies(GameConfigurations config);

    // Creates and returns an array of player strategies based on the given game configuration
    PlayerStrategy[] createPlayerStrategies(GameConfigurations config);
}
