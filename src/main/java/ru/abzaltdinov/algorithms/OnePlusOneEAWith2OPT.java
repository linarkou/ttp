package ru.abzaltdinov.algorithms;

import ru.abzaltdinov.model.ttp.AbstractThiefProblem;
import ru.abzaltdinov.model.ttp.solution.TTPSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * This algorithm is a naive random search, where on random tours items are added in random order.
 * Every time an item is added, it is evaluated and the non-dominated set is updated.
 * <p>
 * If the attribute pi is set the tour is kept fixed and only the order if adding items to it are
 * modified.
 */
public class OnePlusOneEAWith2OPT extends PackingAlgorithm {

    //! number of randomly created tours
    private int maxIterations = 100;

    //! default constructor for this very naive algorithm
    public OnePlusOneEAWith2OPT(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    @Override
    public String getName() {
        return "(1+1)EA";
    }

    @Override
    public List<TTPSolution> solve(AbstractThiefProblem problem) {

        // set the evaluation counter to 0 and initialize the non-dominated set
        int counter = 0;
        TTPSolution s = null;

        // either sample a random tour or use the tour provided to the algorithm
        List<Integer> pi;
        if (this.getPi() == null) {
            // Create a random permutation
            pi = getIndex(1, problem.numOfCities);
            java.util.Collections.shuffle(pi);
            pi.add(0, 0);
        } else {
            pi = this.getPi();
        }

        // Create an empty packing plan
        List<Boolean> z = new ArrayList<>(problem.numOfItems);
        for (int j = 0; j < problem.numOfItems; j++) {
            z.add(false);
        }

        double weight = 0.0;
        s = problem.evaluate(pi, z, true);
        ++counter;

        Random random = new Random();
        double probabilityOfMutation = 1.0 / problem.numOfItems;

        // loop while the function evaluation limit is reached
        while (true) {

            for (int j = 0; j < problem.numOfItems; ++j) {
                if (random.nextDouble() < probabilityOfMutation) {
                    boolean isPackingItem = !z.get(j);
                    z.set(j, isPackingItem);

                    if (isPackingItem) {
                        weight += problem.weight[j];
                    } else {
                        weight -= problem.weight[j];
                    }
                }
            }

            if (weight <= problem.maxWeight) {

                TTPSolution evaluatedSolution = problem.evaluate(pi, z, true);

                if (evaluatedSolution.singleObjective > s.singleObjective) {
                    s = evaluatedSolution;
                    System.out.println(String.format("%s: %d without improvements", this.getName(), counter));
                    counter = 0;
                } else {
                    ++counter;
                }

            } else {
                counter++;
            }

            if (counter == this.maxIterations) {
                break;
            }
        }

        return Arrays.asList(s);

    }

    private List<Integer> getIndex(int low, int high) {
        List<Integer> l = new ArrayList<>();
        for (int j = low; j < high; j++) {
            l.add(j);
        }
        return l;
    }


}
