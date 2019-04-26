package ru.abzaltdinov.model.tsp.solution;

import java.util.List;

public interface Tour {

    List<Integer> getTour();

    /**
     * @param other solution to compare with
     * @return True if tour is equal
     */
    default boolean equalsInDesignSpace(TSPSolution other) {
        return getTour().equals(other.getTour());
    }

}
