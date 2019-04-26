package ru.abzaltdinov.algorithms;

import ru.abzaltdinov.algorithms.tsp.LKH;
import ru.abzaltdinov.model.NonDominatedSet;
import ru.abzaltdinov.model.ttp.solution.TTPSolution;
import ru.abzaltdinov.model.ttp.BiObjectiveThiefProblem;
import ru.abzaltdinov.model.tsp.solution.TSPSolution;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * LKH for searching tours;
 * (1+1) EA for searching packing plan
 */
public class LKHAndPackInTheEnd {
    private final LKH lkhAlgorithm = new LKH();
    private final double probabilityOfInverting;
    // count of maximum iterations without improvement
    private final int maxIterations;

    public LKHAndPackInTheEnd(double probabilityOfInverting, int maxIterations) {
        this.probabilityOfInverting = probabilityOfInverting;
        this.maxIterations = maxIterations;
    }

    public List<TTPSolution> solve(BiObjectiveThiefProblem problem) {

        // start form an empty set of non-dominated solutions
        NonDominatedSet nds = new NonDominatedSet();

        // index vector to permute
        List<TSPSolution> tours = lkhAlgorithm.getTours(problem.TSP);

        // iterate over found TSP solutions
        for (TSPSolution tspSolution : tours) {

            List<Integer> pi = tspSolution.getTour();
            int iterationCounter = 0;

            // create empty packing plan
            List<Boolean> z = new ArrayList<>(problem.numOfItems);
            for (int j = 0; j < problem.numOfItems; j++) z.add(false);

            while (iterationCounter++ < maxIterations) {
                int i = problem.numOfCities - 1;
                while (i >= 0) {
                    if (problem.itemsAtCity.get(i).size() > 0) {
                        Integer itemIndex = problem.itemsAtCity.get(i).get(0);
                        boolean selected = z.get(itemIndex);
                           if (selected) {
                               z.set(itemIndex, false);
                           } else break;
                    }
                    i--;
                }
                if (i >= 0) {
                    LinkedList<Integer> itemsIndices = problem.itemsAtCity.get(i);
                    if (itemsIndices.size() > 0) {
                        z.set(itemsIndices.get(0), true);
                    }
                }
                // evaluate the solution and add to non-dominated set
                TTPSolution s = problem.evaluate(pi,z, true);
                boolean improvedSolution = nds.add(s);
                if (improvedSolution) {
                    iterationCounter = 0;
                }
            }
        }
        return nds.entries;
    }
}
