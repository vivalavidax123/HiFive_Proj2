package hifive;

import java.util.List;

public interface GameComponentFactory {
    List<ScoringStrategy> createScoringStrategies(GameConfigurations config);
    PlayerStrategy[] createPlayerStrategies(GameConfigurations config);
}