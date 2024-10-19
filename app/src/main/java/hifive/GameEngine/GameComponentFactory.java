package hifive.GameEngine;

import hifive.GameConfigurations;

import java.util.List;

public interface GameComponentFactory {
    // Creates and returns a list of scoring strategies based on the given game configuration
    List<ScoringStrategy> createScoringStrategies(GameConfigurations config);
}
