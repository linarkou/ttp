package ru.abzaltdinov.algorithms;

import ru.abzaltdinov.algorithms.tsp.nsga2.NSGA2;
import ru.abzaltdinov.model.NonDominatedSet;
import ru.abzaltdinov.model.tsp.solution.Tour;
import ru.abzaltdinov.model.ttp.SingleObjectiveThiefProblemWithBiObjectiveTSP;
import ru.abzaltdinov.model.ttp.solution.TTPSolution;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class BiObjectiveTSPAndPackInTheEnd {
    private final NSGA2 nsga2 = new NSGA2();
    private final double probabilityOfInverting;
    // count of maximum iterations without improvement
    private final int maxIterations;

    public BiObjectiveTSPAndPackInTheEnd(double probabilityOfInverting, int maxIterations) {
        this.probabilityOfInverting = probabilityOfInverting;
        this.maxIterations = maxIterations;
    }

    public List<TTPSolution> solve(SingleObjectiveThiefProblemWithBiObjectiveTSP problem) {
        nsga2.initialize(problem.biObjectiveTSP);

        // start form an empty set of non-dominated solutions
        NonDominatedSet nds = new NonDominatedSet();

        // index vector to permute
        List<? extends Tour> tours = nsga2.solve();

        // iterate over found TSP solutions
        for (Tour tour : tours) {

            List<Integer> pi = tour.getTour();
            int iterationCounter = 0;

            // create empty packing plan
            List<Boolean> z = new ArrayList<>(problem.numOfItems);
            for (int j = 0; j < problem.numOfItems; j++) {
                z.add(false);
            }

            while (iterationCounter++ < maxIterations) {
                int i = problem.numOfCities - 1;
                while (i >= 0) {
                    if (problem.itemsAtCity.get(i).size() > 0) {
                        Integer itemIndex = problem.itemsAtCity.get(i).get(0);
                        boolean selected = z.get(itemIndex);
                        if (selected) {
                            z.set(itemIndex, false);
                        } else {
                            break;
                        }
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
                TTPSolution s = problem.evaluate(pi, z, true);
                boolean improvedSolution = nds.add(s);
                if (improvedSolution) {
                    iterationCounter = 0;
                }
            }
        }
        return nds.entries;
    }
}
