package ru.abzaltdinov.algorithms.ea;

import ru.abzaltdinov.algorithms.Algorithm;
import ru.abzaltdinov.algorithms.PackIterative;
import ru.abzaltdinov.algorithms.ea.kp.BitflipMutation;
import ru.abzaltdinov.algorithms.ea.kp.EqualAndBetterItemsLocalSearch;
import ru.abzaltdinov.algorithms.ea.kp.EqualItemsLocalSearch;
import ru.abzaltdinov.algorithms.ea.kp.PackLaterLocalSearch;
import ru.abzaltdinov.algorithms.ea.tsp.InsertionMutation;
import ru.abzaltdinov.algorithms.ea.tsp.InsertionMutation2;
import ru.abzaltdinov.algorithms.ea.tsp.OptimalSubTourSearch;
import ru.abzaltdinov.model.Pair;
import ru.abzaltdinov.model.tsp.TSPInstance;
import ru.abzaltdinov.model.ttp.AbstractTTPInstance;
import ru.abzaltdinov.model.ttp.TTP1Instance;
import ru.abzaltdinov.model.ttp.solution.TTPSolution;

import java.util.ArrayList;
import java.util.List;

public class MyOnePlusOneEA implements Algorithm {
    private double packingPlanMutationProbability;
    private double tourMutationProbability;
    private double secondTourMutationProbability;
    private int maxIterations;

    public MyOnePlusOneEA(double packingPlanMutationProbability,
                          double tourMutationProbability,
                          double secondTourMutationProbability,
                          int maxItertions) {
        this.packingPlanMutationProbability = packingPlanMutationProbability;
        this.tourMutationProbability = tourMutationProbability;
        this.secondTourMutationProbability = secondTourMutationProbability;
        this.maxIterations = maxItertions;
    }

    @Override
    public TTPSolution solve(TTP1Instance problem) {
        final EqualItemsLocalSearch equalItemsLocalSearch = new EqualItemsLocalSearch(problem);
        final EqualAndBetterItemsLocalSearch equalAndBetterItemsLocalSearch = new EqualAndBetterItemsLocalSearch(problem);

        PackLaterLocalSearch packLaterLocalSearch = new PackLaterLocalSearch(problem);
        OptimalSubTourSearch optimalSubTourSearch = new OptimalSubTourSearch(problem, 5);
        BitflipMutation bitflipMutation = new BitflipMutation(problem, packingPlanMutationProbability);
        MutationOperator<Integer, TTPSolution>[] insertionMutation = new MutationOperator[] {
                new InsertionMutation(problem, tourMutationProbability),
                new InsertionMutation2(problem, secondTourMutationProbability)
        };
        int mutator = 0;

        TTPSolution solution = getInitialSolution(problem);
        if (problem.numOfItems < 100000) {
            solution = equalAndBetterItemsLocalSearch.improve(solution);
        } else {
            solution = equalItemsLocalSearch.improve(solution);
        }
        int iterationsWithoutImprovements = 0;
        int iterWhenImprove = maxIterations - 1;
        while (iterationsWithoutImprovements < maxIterations) {
            Pair<Integer, List<Boolean>> mutatedPackPlan = bitflipMutation.mutate(solution);
            TTPSolution newSolution = new TTPSolution(solution.pi, mutatedPackPlan.getSecond());
            Pair<Integer, TTPSolution> mutatedTour = insertionMutation[mutator].mutate(newSolution);
            newSolution = mutatedTour.getSecond();
            if (mutatedTour.getFirst() != null) {
                newSolution = optimalSubTourSearch.improve(newSolution, mutatedTour.getFirst());
            }
            if (mutatedPackPlan.getFirst() != null) {
                newSolution = packLaterLocalSearch.improve(newSolution, mutatedPackPlan.getFirst());
            }
            if (newSolution.objective > solution.objective) {
//                if (mutatedTour.getFirst() != null) {
//                    System.out.println("Improved tour!");
//                }
                solution = newSolution;
                iterationsWithoutImprovements = 0;
            } else {
                iterationsWithoutImprovements++;
                if (iterationsWithoutImprovements == iterWhenImprove) {
                    if (problem.numOfItems < 1e+4) {
                        newSolution = equalAndBetterItemsLocalSearch.improve(solution);
                    } else {
                        newSolution = equalItemsLocalSearch.improve(solution);
                    }
                    if (newSolution.objective > solution.objective) {
                        solution = newSolution;
                        iterationsWithoutImprovements = 0;
                    }
                    else if (mutator == 0) {
                        mutator = 1;
                        iterationsWithoutImprovements = 0;
                        System.out.println("Changed! " + solution.objective);
                    }
                }
            }
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
//            System.out.println(solution.objective);
        }

        return solution;
    }

    private TTPSolution getInitialSolution(AbstractTTPInstance problem) {
        PackIterative packIterativeAlgorithm = new PackIterative(problem);
        List<Integer> tour = TSPInstance.linkernSolution(((TTP1Instance) problem).TSP);
        return packIterativeAlgorithm.packIterative(tour);
    }

    @Override
    public String getName() {
        return "New (1+1) EA";
    }
}
