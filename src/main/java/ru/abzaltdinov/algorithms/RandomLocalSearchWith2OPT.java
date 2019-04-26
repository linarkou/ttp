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
public class RandomLocalSearchWith2OPT extends PackingAlgorithm {

    //! number of randomly created tours
    private int maxIterations = 100;

    //! default constructor for this very naive algorithm
    public RandomLocalSearchWith2OPT(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    @Override
    public String getName() {
        return "RLS + 2-OPT";
    }

    @Override
    public List<TTPSolution> solve(AbstractThiefProblem problem) {

        // set the evaluation counter to 0 and initialize the non-dominated set
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

        Random random = new Random();

        for (int edge1Start = 0; edge1Start < problem.numOfCities; ++edge1Start) {
            for (int edge2Start = 0; edge2Start < problem.numOfCities; ++edge2Start) {
                if (edge1Start == edge2Start) continue;
                int edge1End = pi.get(edge1Start);
                int edge2End = pi.get(edge2Start);
                if (edge1End == 0 || edge2End == 0) continue;
                pi.set(edge1Start, edge2End);
                pi.set(edge2Start, edge1End);

                // Create an empty packing plan
                List<Boolean> z = new ArrayList<>(problem.numOfItems);
                for (int j = 0; j < problem.numOfItems; j++) {
                    z.add(false);
                }

                double weight = 0;
                int counter = 0;

                // evaluate for this random tour
                s = problem.evaluate(pi, z, true);
                ++counter;

                // loop while the function evaluation limit is reached
                while (true) {
                    int item = random.nextInt(problem.numOfItems);
                    boolean isPackingItem = !z.get(item);
                    z.set(item, isPackingItem);
                    if (isPackingItem) {
                        weight += problem.weight[item];
                    } else {
                        weight -= problem.weight[item];
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

                pi.set(edge1Start, edge1End);
                pi.set(edge2Start, edge2End);

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
