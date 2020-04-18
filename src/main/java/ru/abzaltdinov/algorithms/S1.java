package ru.abzaltdinov.algorithms;

import ru.abzaltdinov.model.tsp.TSPInstance;
import ru.abzaltdinov.model.ttp.TTP1Instance;
import ru.abzaltdinov.model.ttp.solution.TTPSolution;

import java.util.List;

public class S1 implements Algorithm {

    public TTPSolution solve(TTP1Instance problem) {
        PackIterative packIterativeAlgorithm = new PackIterative(problem);

        List<Integer> tspSolution = TSPInstance.linkernSolution(problem.TSP);
        TTPSolution ttpSolution = packIterativeAlgorithm.packIterative(tspSolution);
        return ttpSolution;
    }

    public String getName() {
        return "S1";
    }
}
