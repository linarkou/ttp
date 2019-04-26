package ru.abzaltdinov.algorithms;

import ru.abzaltdinov.model.ttp.AbstractThiefProblem;
import ru.abzaltdinov.model.ttp.solution.TTPSolution;

import java.util.List;

public interface Algorithm {

    /**
     * This method should be overriden by your algorithm to solve the problem
     * @param problem traveling thief problem instance
     * @return A non-dominated set of solutions
     */
    List<TTPSolution> solve(AbstractThiefProblem problem);

    /**
     *
     * @return Algorithm name
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }

}
