package ru.abzaltdinov.algorithms;

import ru.abzaltdinov.model.tsp.TSPInstance;
import ru.abzaltdinov.model.ttp.TTP1Instance;
import ru.abzaltdinov.model.ttp.solution.TTPSolution;

import java.util.List;

public class S5 implements Algorithm{

    public TTPSolution solve(TTP1Instance problem) {
        PackIterative packIterativeAlgorithm = new PackIterative(problem);

        List<Integer> tspSolution = TSPInstance.linkernSolution(problem.TSP);

        TTPSolution bestSolution = new TTPSolution();
        bestSolution.objective = Double.NEGATIVE_INFINITY;
        for (int i = 1; i < 10; ++i) {
            TTPSolution newSolution = packIterativeAlgorithm.packIterative(tspSolution, i, i/2.0, 10);
            if (newSolution.compareTo(bestSolution) > 0) {
                bestSolution = newSolution;
            }
            if (Thread.currentThread().isInterrupted()) {
                return bestSolution;
            }
        }
        return bestSolution;
    }

    public String getName() {
        return "S5";
    }
}
