package ru.abzaltdinov.algorithms.ea.tsp;

import ru.abzaltdinov.algorithms.ea.LocalSearch;
import ru.abzaltdinov.model.ttp.AbstractTTPInstance;
import ru.abzaltdinov.model.ttp.solution.TTPSolution;

import java.util.*;

public class OptimalSubTourSearch {

    private AbstractTTPInstance ttp;
    private int subTourLength;

    public OptimalSubTourSearch(AbstractTTPInstance ttp, int subTourLength) {
        this.ttp = ttp;
        this.subTourLength = subTourLength;
    }

    public TTPSolution improve(TTPSolution solution, int cityIndex) {
        List<Integer> tour = new ArrayList<>(solution.pi);
        List<Boolean> packingPlan = new ArrayList<>(solution.z);

        int leftCityIndex = Math.min(
                Math.max(1, cityIndex - subTourLength/2),
                tour.size() - subTourLength - 1
        );

        // attributes in the beginning of the tour
        double weight = 0;

        // iterate over all possible citiesPermutation
        for (int i = 0; i < leftCityIndex - 1; i++) {
            // the city where the thief currently is
            int city = tour.get(i);
            // for each item index this city
            for (int j : ttp.itemsAtCity.get(city)) {

                // if the thief picks that item
                if (packingPlan.get(j)) {
                    // update the current weight and profit
                    weight += ttp.weight[j];
                }
            }
        }

        Map<Integer, Double> weightOfCity = new HashMap<>();
        for (int i = -1; i <= subTourLength; i++) {
            int city = tour.get(leftCityIndex + i);
            double weightOfCurrentCity = 0d;
            for (int j : ttp.itemsAtCity.get(city)) {
                // if the thief picks that item
                if (packingPlan.get(j)) {
                    // update the current weight and profit
                    weightOfCurrentCity += ttp.weight[j];
                }
            }
            weightOfCity.put(city, weightOfCurrentCity);
        }

        Integer[] citiesPermutation = new Integer[subTourLength];
        for (int i = 0; i < subTourLength; ++i) {
            citiesPermutation[i] = i;
        }

        Integer[] bestPermutationOfCitites = null;
        Double bestTime = Double.POSITIVE_INFINITY;
        boolean initialTimeCalculated = false;
        double initialTime = Double.NEGATIVE_INFINITY;
        // check all permutations
        boolean hasNext = true;
        while (hasNext) {
            // calc tour time
            double newTime = 0;
            double newWeight = 0;

            int[] extendedCitiesPermutation = new int[subTourLength + 2];
            extendedCitiesPermutation[0] = -1;
            for (int i = 0; i < subTourLength; ++i) {
                extendedCitiesPermutation[i + 1] = citiesPermutation[i];
            }
            extendedCitiesPermutation[subTourLength + 1] = subTourLength;

            for (int i = 0; i < subTourLength + 1; i++) {

                // the city where the thief currently is
                int city = tour.get(leftCityIndex + extendedCitiesPermutation[i]);

                newWeight += weightOfCity.get(city);

                // update the speed accordingly
                double speed = ttp.maxSpeed - ((weight + newWeight) / ttp.maxWeight) * (ttp.maxSpeed - ttp.minSpeed);

                // increase time by considering the speed - do not forget the way from the last city to the first!
                int next = tour.get(leftCityIndex + extendedCitiesPermutation[i + 1]);
                long distance = (long) Math.ceil(ttp.distance(city, next));

                newTime += distance / speed;
            }
            if (newTime < bestTime) {
                bestTime = newTime;
                bestPermutationOfCitites = Arrays.copyOf(citiesPermutation, subTourLength);
                if (!initialTimeCalculated) {
                    initialTime = newTime;
                    initialTimeCalculated = true;
                }
            }
            //calc next permutation
            int k = 0, l = 0;
            hasNext = false;
            for (int i = citiesPermutation.length - 1; i > 0; i--) {
                if (citiesPermutation[i].compareTo(citiesPermutation[i - 1]) > 0) {
                    k = i - 1;
                    hasNext = true;
                    break;
                }
            }

            for (int i = citiesPermutation.length - 1; i > k; i--) {
                if (citiesPermutation[i].compareTo(citiesPermutation[k]) > 0) {
                    l = i;
                    break;
                }
            }
            swap(citiesPermutation, k, l);
            Collections.reverse(Arrays.asList(citiesPermutation).subList(k + 1, citiesPermutation.length));
        }

        //update tour
        int[] citiesInBestOrder = new int[subTourLength];
        for (int i = 0; i < subTourLength; ++i) {
            citiesInBestOrder[i] = tour.get(leftCityIndex + bestPermutationOfCitites[i]);
        }
        for (int i = 0; i < subTourLength; ++i) {
            tour.set(leftCityIndex + i, citiesInBestOrder[i]);
        }

        TTPSolution newSolution = new TTPSolution();
        newSolution.pi = tour;
        newSolution.z = packingPlan;
        newSolution.weigthOfItems = solution.weigthOfItems;
        newSolution.profit = solution.profit;
        newSolution.time = solution.time + bestTime - initialTime;
        newSolution.objective = newSolution.profit - ttp.R * newSolution.time;

        return newSolution;
    }

    private void swap(Integer[] elements, int i, int j) {
        int tmp = elements[i];
        elements[i] = elements[j];
        elements[j] = tmp;
    }
}
