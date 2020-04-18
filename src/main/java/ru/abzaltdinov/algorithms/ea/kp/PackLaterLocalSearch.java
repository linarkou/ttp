package ru.abzaltdinov.algorithms.ea.kp;

import ru.abzaltdinov.algorithms.ea.LocalSearch;
import ru.abzaltdinov.model.Pair;
import ru.abzaltdinov.model.ttp.AbstractTTPInstance;
import ru.abzaltdinov.model.ttp.solution.TTPSolution;

import java.util.ArrayList;
import java.util.List;

public class PackLaterLocalSearch {

    private AbstractTTPInstance ttp;

    public PackLaterLocalSearch(AbstractTTPInstance ttp) {
        this.ttp = ttp;
    }

    public TTPSolution improve(TTPSolution solution, Integer itemFlipped) {
        int n = ttp.numOfCities;

        List<Boolean> packingPlan = new ArrayList<>(solution.z);
        List<Integer> tour = new ArrayList<>(solution.pi);

        // BAD THING, BUT IMPROVE RUNTIME
        if (packingPlan.get(itemFlipped) == false) {
            return solution;
        }

        int[] numberOfCityInTour = new int[n];
        for (int i = 0; i < n; ++i) {
            int city = tour.get(i);
            numberOfCityInTour[city] = i;
        }

        boolean improved = false;
        List<Integer> listOfEqualItems = ttp.equalItems.get(itemFlipped);
        int amountOfPackingItems = 0;
        for (Integer item : listOfEqualItems) {
            if (packingPlan.get(item)) {
                amountOfPackingItems++;
            }
        }
        if (amountOfPackingItems != 0 && amountOfPackingItems != listOfEqualItems.size()) {
            //items in later cities should be first in list
            listOfEqualItems.sort((item1, item2) -> {
                int numberOfCity1 = numberOfCityInTour[ttp.cityOfItem[item1]];
                int numberOfCity2 = numberOfCityInTour[ttp.cityOfItem[item2]];
                return -(numberOfCity1 - numberOfCity2);
            });
            for (Integer item : listOfEqualItems) {
                if (amountOfPackingItems > 0) {
                    if (packingPlan.get(item) == false) {
                        improved = true;
                    }
                    packingPlan.set(item, true);
                    amountOfPackingItems--;
                } else {
                    packingPlan.set(item, false);
                }
            }
        }
        if (improved) {
            return ttp.evaluate(tour, packingPlan);
        } else {
            return solution;
        }
    }
}
