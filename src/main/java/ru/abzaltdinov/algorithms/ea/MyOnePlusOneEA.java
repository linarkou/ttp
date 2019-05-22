package ru.abzaltdinov.algorithms.ea;

import ru.abzaltdinov.algorithms.Algorithm;
import ru.abzaltdinov.algorithms.PackIterative;
import ru.abzaltdinov.algorithms.ea.kp.BitflipMutation;
import ru.abzaltdinov.algorithms.ea.kp.EqualAndBetterItemsLocalSearch;
import ru.abzaltdinov.algorithms.ea.kp.EqualItemsLocalSearch;
import ru.abzaltdinov.algorithms.ea.kp.PackLaterLocalSearch;
import ru.abzaltdinov.algorithms.ea.tsp.InsertionMutation;
import ru.abzaltdinov.algorithms.ea.tsp.OptimalSubTourSearch;
import ru.abzaltdinov.model.Pair;
import ru.abzaltdinov.model.tsp.TSPInstance;
import ru.abzaltdinov.model.ttp.AbstractTTPInstance;
import ru.abzaltdinov.model.ttp.TTP1Instance;
import ru.abzaltdinov.model.ttp.solution.TTPSolution;

import java.util.List;

public class MyOnePlusOneEA implements Algorithm {
    private double packingPlanMutationProbability;
    private double tourMutationProbability;
    private int maxIterations;

    public MyOnePlusOneEA(double packingPlanMutationProbability,
                          double tourMutationProbability,
                          int maxItertions) {
        this.packingPlanMutationProbability = packingPlanMutationProbability;
        this.tourMutationProbability = tourMutationProbability;
        this.maxIterations = maxItertions;
    }

    @Override
    public TTPSolution solve(TTP1Instance problem) {
        final EqualItemsLocalSearch equalItemsLocalSearch = new EqualItemsLocalSearch(problem);
        final EqualAndBetterItemsLocalSearch equalAndBetterItemsLocalSearch = new EqualAndBetterItemsLocalSearch(problem);

        final PackLaterLocalSearch packLaterLocalSearch = new PackLaterLocalSearch(problem);
        final OptimalSubTourSearch optimalSubTourSearch = new OptimalSubTourSearch(problem, 5);
        final BitflipMutation bitflipMutation = new BitflipMutation(problem, packingPlanMutationProbability);
        final InsertionMutation insertionMutation = new InsertionMutation(problem, tourMutationProbability);

        TTPSolution solution = getInitialSolution(problem);
        if (problem.numOfItems < 100000) {
            solution = equalAndBetterItemsLocalSearch.improve(solution);
        } else {
            solution = equalItemsLocalSearch.improve(solution);
        }
        int iterationsWithoutImprovements = 0;
        while (iterationsWithoutImprovements < maxIterations) {
            Pair<Integer, List<Integer>> mutatedTour = insertionMutation.mutate(solution);
            Pair<Integer, List<Boolean>> mutatedPackPlan = bitflipMutation.mutate(solution);
            TTPSolution newSolution = problem.evaluate(mutatedTour.getSecond(), mutatedPackPlan.getSecond());
            if (mutatedTour.getFirst() != null) {
                newSolution = optimalSubTourSearch.improve(newSolution, mutatedTour.getFirst());
            }
            if (mutatedPackPlan.getFirst() != null) {
                newSolution = packLaterLocalSearch.improve(newSolution, mutatedPackPlan.getFirst());
            }
            if (newSolution.objective > solution.objective) {
                solution = newSolution;
                iterationsWithoutImprovements = 0;
            } else {
                iterationsWithoutImprovements++;
            }
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
        }
        if (problem.numOfItems < 1e+4) {
            solution = equalAndBetterItemsLocalSearch.improve(solution);
        } else {
            solution = equalItemsLocalSearch.improve(solution);
        }

        return solution;
    }

    private TTPSolution getInitialSolution(AbstractTTPInstance problem) {
        PackIterative packIterativeAlgorithm = new PackIterative(problem);
        List<Integer> tour = TSPInstance.linkernSolution(((TTP1Instance)problem).TSP);
        return packIterativeAlgorithm.packIterative(tour);
    }

    @Override
    public String getName() {
        return "New (1+1) EA";
    }
}
