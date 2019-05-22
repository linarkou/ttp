package ru.abzaltdinov.algorithms;

import ru.abzaltdinov.model.ttp.TTP1Instance;
import ru.abzaltdinov.model.ttp.solution.TTPSolution;

public interface Algorithm {

    /**
     * This method should be overriden by your algorithm to solve the problem
     * @param problem traveling thief problem instance
     * @return A non-dominated set of solutions
     */
    TTPSolution solve(TTP1Instance problem);

    /**
     *
     * @return Algorithm name
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }

}
