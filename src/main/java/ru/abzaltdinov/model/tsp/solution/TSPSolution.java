package ru.abzaltdinov.model.tsp.solution;

import java.util.List;

/**
 * This is a solution objective which stores the tour, packing plan and the objective values.
 */
public class TSPSolution implements Tour, Comparable<TSPSolution> {

    public List<Integer> pi;

    public double distance = -1d;

    @Override
    public List<Integer> getTour() {
        return pi;
    }

    @Override
    public int compareTo(TSPSolution o) {
        return 0;
    }
}
