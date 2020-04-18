package ru.abzaltdinov.algorithms.ea.kp;

import ru.abzaltdinov.algorithms.ea.LocalSearch;
import ru.abzaltdinov.model.ttp.AbstractTTPInstance;
import ru.abzaltdinov.model.ttp.solution.TTPSolution;
import ru.abzaltdinov.runner.Util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EqualAndBetterItemsLocalSearch implements LocalSearch {

    private AbstractTTPInstance ttp;

    public EqualAndBetterItemsLocalSearch(AbstractTTPInstance ttp) {
        this.ttp = ttp;
    }

    @Override
    public TTPSolution improve(TTPSolution solution) {
        int n = ttp.numOfCities;

        List<Boolean> packingPlan = new ArrayList<>(solution.z);
        List<Integer> tour = new ArrayList<>(solution.pi);

        int[] numberOfCityInTour = new int[n];
        for (int i = 0; i < n; ++i) {
            int city = tour.get(i);
            numberOfCityInTour[city] = i;
        }

        boolean improved = false;
        ttp.allListsOfEqualItems.sort(Comparator.comparingDouble(l -> ttp.weight[l.get(0)]));
        for (int i = 0; i < ttp.allListsOfEqualItems.size(); ++i) {
            for (Integer item : ttp.allListsOfEqualItems.get(i)) {
                if (packingPlan.get(item) == false) {
                    continue;
                }
                for (int j = 0; j < ttp.allListsOfEqualItems.size(); ++j) {
                    if (j > i && !Util.equals(ttp.weight[ttp.allListsOfEqualItems.get(j).get(0)], ttp.weight[item])) {
                        break;
                    }
                    Integer bestOtherItem = null;
                    for (Integer otherItem : ttp.allListsOfEqualItems.get(j)) {
                        if (otherItem == item) {
                            continue;
                        }
                        if (packingPlan.get(otherItem) == false
                                && isBetter(otherItem, item, numberOfCityInTour)
                                && (bestOtherItem == null || isBetter(otherItem, bestOtherItem, numberOfCityInTour))) {
                            bestOtherItem = otherItem;
                        }
                    }
                    if (bestOtherItem != null) {
                        packingPlan.set(bestOtherItem, true);
                        packingPlan.set(item, false);
                        improved = true;
                        break;
                    }
                }
            }

        }
        if (improved) {
            return ttp.evaluate(tour, packingPlan);
        } else {
            return solution;
        }
    }

    private boolean isBetter(Integer item1, Integer item2, int[] numberOfCityInTour) {
        return ttp.weight[item1] <= ttp.weight[item2] &&
                ttp.profit[item1] >= ttp.profit[item2] &&
                numberOfCityInTour[ttp.cityOfItem[item1]] >= numberOfCityInTour[ttp.cityOfItem[item2]];
    }
}
