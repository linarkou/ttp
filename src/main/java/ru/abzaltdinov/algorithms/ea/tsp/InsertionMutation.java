package ru.abzaltdinov.algorithms.ea.tsp;

import ru.abzaltdinov.algorithms.ea.MutationOperator;
import ru.abzaltdinov.model.Pair;
import ru.abzaltdinov.model.ttp.AbstractTTPInstance;
import ru.abzaltdinov.model.ttp.solution.TTPSolution;

import java.util.*;

public class InsertionMutation extends MutationOperator<Integer, TTPSolution> {

    private AbstractTTPInstance ttp;

    public InsertionMutation(AbstractTTPInstance ttp, double probability) {
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

        long minLength = Integer.MAX_VALUE;
        int nearestCityIndex = -1;
        int nearestCity = -1;

        // check this number of random cities and take nearest
        int amountOfCheckingCities = 3;

        while (amountOfCheckingCities > 0) {
            int index = random.nextInt(n - 1) + 1;
            if (index == cityIndex) continue;

            int otherCity = tour.get(index);
            long distance = ttp.distance(city, otherCity);
            if (distance < minLength) {
                minLength = distance;
                nearestCityIndex = index;
                nearestCity = otherCity;
            }
            amountOfCheckingCities--;
        }

        tour.set(cityIndex, nearestCity);
        tour.set(nearestCityIndex, city);

        TTPSolution newSolution = ttp.evaluate(tour, new ArrayList<>(solution.z));

        if (cityIndex < nearestCityIndex) {
            return new Pair<>(cityIndex, newSolution);
        } else {
            return new Pair<>(nearestCityIndex, newSolution);
        }
    }
}
