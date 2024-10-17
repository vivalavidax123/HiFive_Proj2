package hifive;

import java.util.ArrayList;
import java.util.List;

public class ScoringStrategyFactory {
    public static List<ScoringStrategy> createScoringStrategies(GameConfigurations config) {
        List<ScoringStrategy> strategies = new ArrayList<>();
        strategies.add(new FiveScoring(config.FIVE_GOAL, config.FIVE_POINTS));
        strategies.add(new SumFiveScoring(config.FIVE_GOAL, config.SUM_FIVE_POINTS));
        strategies.add(new DifferenceFiveScoring(config.FIVE_GOAL, config.DIFFERENCE_FIVE_POINTS));
        strategies.add(new NoneFiveScoring());
        return strategies;
    }
}