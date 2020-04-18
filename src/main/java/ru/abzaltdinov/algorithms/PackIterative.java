package ru.abzaltdinov.algorithms;

import ru.abzaltdinov.model.ttp.AbstractTTPInstance;
import ru.abzaltdinov.model.ttp.solution.TTPSolution;
import ru.abzaltdinov.util.Quicksort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PackIterative {
    private static final Double EPSILON = 1e-1;
    private AbstractTTPInstance ttp;

    public PackIterative(AbstractTTPInstance ttp) {
        this.ttp = ttp;
    }

    private long[] calcDistToTheEnd(List<Integer> tour) {
        int n = ttp.numOfCities;
        int m = ttp.numOfItems;

        long[] distToTheEnd = new long[n];
        distToTheEnd[n - 1] = ttp.distance(tour.get(n - 1), 0);
        for (int i = n - 2; i >= 0; i--) {
            distToTheEnd[i] = distToTheEnd[i + 1] + ttp.distance(tour.get(i), tour.get(i + 1));
        }
        return distToTheEnd;
    }

    private int[] sortItemsByScore(long[] distToTheEnd, double alpha) {
        int m = ttp.numOfItems;

        Double[] scores = new Double[m];
        for (int i = 0; i < m; ++i) {
            scores[i] = Math.pow(ttp.profit[i] / ttp.weight[i], alpha) / distToTheEnd[ttp.cityOfItem[i]];
        }
        Quicksort<Double> scoresQS = new Quicksort<>(scores);
        scoresQS.sort();
        return scoresQS.getIndices();
    }

    public TTPSolution pack(List<Integer> tour, double alpha, long[] distToTheEnd) {
        if (tour.size() != ttp.numOfCities) {
            throw new RuntimeException("number of cities in tour not equals to instance.numOfCitites");
        }
        int n = ttp.numOfCities;
        int m = ttp.numOfItems;

        int[] itemsDescByScore = sortItemsByScore(distToTheEnd, alpha);

        int tau = Math.min(100, m / 10);
        int mu = m / tau; // every mu iterations objective value will be evaluated
        List<Boolean> currentPP = new ArrayList<>(Collections.nCopies(m, false));
        double currentWeight = 0;
        TTPSolution bestSolution = ttp.evaluate(tour, currentPP);
        int k = 1, kStar = 1;
        while (currentWeight < ttp.maxWeight && mu > 1 && k <= m) {
            int newItem = itemsDescByScore[k - 1];
            if (currentWeight + ttp.weight[newItem] <= ttp.maxWeight) {
                currentPP.set(newItem, true);
                currentWeight += ttp.weight[newItem];
                if (k % mu == 0) {
                    TTPSolution newSolution = ttp.evaluate(tour, new ArrayList<>(currentPP));
                    if (newSolution.compareTo(bestSolution) > 0) {
                        bestSolution = newSolution;
                        kStar = k;
                    } else {
                        //restore solution
                        currentPP = new ArrayList<>(bestSolution.z);
                        currentWeight = bestSolution.weigthOfItems;
                        k = kStar;
                        mu /= 2;
                    }
                }
            }
            k++;
        }
        if (!currentPP.equals(bestSolution.z)) {
            TTPSolution newSolution = ttp.evaluate(tour, new ArrayList<>(currentPP));
            if (newSolution.compareTo(bestSolution) > 0) {
                bestSolution = newSolution;
            }
        }
        TTPSolution evaluate = ttp.evaluate(bestSolution.pi, bestSolution.z);
        return bestSolution;
    }

    public TTPSolution packIterative(List<Integer> tour) {
        return packIterative(tour, 5, 2.5, 20);
    }

    public TTPSolution packIterative(List<Integer> tour, double c, double delta, int maxIterations) {
        long[] distToTheEnd = calcDistToTheEnd(tour);
        TTPSolution left = pack(tour, c - delta, distToTheEnd);
        TTPSolution mid = pack(tour, c, distToTheEnd);
        TTPSolution right = pack(tour, c + delta, distToTheEnd);
        TTPSolution bestSolution = mid;
        for (int i = 0; i < maxIterations; ++i) {
            if (left.compareTo(mid) > 0 && right.compareTo(mid) > 0) {
                if (left.compareTo(right) > 0) {
                    mid = left;
                    bestSolution = left;
                    c = c - delta;
                } else {
                    mid = right;
                    bestSolution = right;
                    c = c + delta;
                }
            } else if (left.compareTo(mid) > 0) {
                mid = left;
                bestSolution = left;
                c = c - delta;
            } else if (right.compareTo(mid) > 0) {
                mid = right;
                bestSolution = right;
                c = c + delta;
            }
            delta = delta / 2;
            if (left.objective - mid.objective < EPSILON
                    && right.objective - mid.objective < EPSILON) {
                break;
            }
            left = pack(tour, c - delta, distToTheEnd);
            right = pack(tour, c + delta, distToTheEnd);
        }
        return bestSolution;
    }
}
