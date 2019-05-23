package ru.abzaltdinov.algorithms.ea.tsp;

import ru.abzaltdinov.algorithms.ea.MutationOperator;
import ru.abzaltdinov.model.Pair;
import ru.abzaltdinov.model.ttp.AbstractTTPInstance;
import ru.abzaltdinov.model.ttp.solution.TTPSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class InsertionMutation2 extends MutationOperator<Integer, TTPSolution> {

    private AbstractTTPInstance ttp;

    public InsertionMutation2(AbstractTTPInstance ttp, double probability) {
        super(probability);
        this.ttp = ttp;
    }

    @Override
    public Pair<Integer, TTPSolution> mutate(TTPSolution solution) {
        List<Integer> tour = new ArrayList<>(solution.pi);

        if (Math.random() > getProbability()) {
            return new Pair<>(null, solution);
        }

        int n = tour.size();
        Random random = new Random();
        int cityIndex = random.nextInt(n - 1) + 1;
        int city = tour.get(cityIndex);

        for (int i = cityIndex; i < n-1; ++i) {
            tour.set(i, tour.get(i + 1));
        }
        tour.set(n - 1, city);
        TTPSolution oldSolution = solution;

        solution = ttp.evaluate(tour, solution.z, true);
        TTPSolution bestSolution = new TTPSolution(solution);
        int indexWhereInserted = n - 1;
        tour = solution.pi;
        for (int indexToInsert = n - 2; indexToInsert >= 1; --indexToInsert) {

            double oldSegmentTime = 0;
            for (int i = indexToInsert - 1; i <= indexToInsert + 1; ++i) {
                double weight = solution.weightAccumAtCity[i];
                // update the speed accordingly
                double speed = ttp.maxSpeed - (weight / ttp.maxWeight) * (ttp.maxSpeed - ttp.minSpeed);

                // increase time by considering the speed - do not forget the way from the last city to the first!
                int next = tour.get((i + 1) % n);
                long distance = (long) Math.ceil(ttp.distance(tour.get(i), next));

                oldSegmentTime += distance / speed;
            }

            //swap cities
            tour.set(indexToInsert + 1, tour.get(indexToInsert));
            tour.set(indexToInsert, city);
            Integer tmpWeight = solution.weightAtCity[indexToInsert];
            solution.weightAtCity[indexToInsert] = solution.weightAtCity[indexToInsert + 1];
            solution.weightAtCity[indexToInsert + 1] = tmpWeight;
            solution.weightAccumAtCity[indexToInsert] = solution.weightAccumAtCity[indexToInsert - 1] + solution.weightAtCity[indexToInsert];
            solution.weightAccumAtCity[indexToInsert + 1] = solution.weightAccumAtCity[indexToInsert] + solution.weightAtCity[indexToInsert + 1];

            double newSegmentTime = 0;
            for (int i = indexToInsert - 1; i <= indexToInsert + 1; ++i) {
                double weight = solution.weightAccumAtCity[i];
                // update the speed accordingly
                double speed = ttp.maxSpeed - (weight / ttp.maxWeight) * (ttp.maxSpeed - ttp.minSpeed);

                // increase time by considering the speed - do not forget the way from the last city to the first!
                int next = tour.get((i + 1) % n);
                long distance = (long) Math.ceil(ttp.distance(tour.get(i), next));

                newSegmentTime += distance / speed;
            }
            solution.time += newSegmentTime - oldSegmentTime;
            solution.objective = solution.profit - ttp.R * solution.time;

            if (solution.objective > bestSolution.objective) {
                bestSolution.pi = new ArrayList<>(tour);
                bestSolution.time = solution.time;
                bestSolution.objective = solution.objective;
                bestSolution.weightAtCity = Arrays.copyOf(solution.weightAtCity, n);
                bestSolution.weightAccumAtCity = Arrays.copyOf(solution.weightAccumAtCity, n);
                indexWhereInserted = indexToInsert;
            }
        }
        if (indexWhereInserted != cityIndex) {
            return new Pair<>(indexWhereInserted, bestSolution);
        } else {
            return new Pair<>(null, solution);
        }
    }
}
