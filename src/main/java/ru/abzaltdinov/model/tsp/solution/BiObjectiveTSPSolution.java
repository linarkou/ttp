package ru.abzaltdinov.model.tsp.solution;

import org.uma.jmetal.problem.PermutationProblem;
import org.uma.jmetal.solution.impl.DefaultIntegerPermutationSolution;

import java.util.Collections;
import java.util.List;

public class BiObjectiveTSPSolution extends DefaultIntegerPermutationSolution implements Tour {
    public BiObjectiveTSPSolution(PermutationProblem<?> problem) {
        super(problem);
        Collections.rotate(this.getVariables(), -this.getVariables().indexOf(0));
    }

    public BiObjectiveTSPSolution(DefaultIntegerPermutationSolution solution) {
        super(solution);
    }

    public BiObjectiveTSPSolution copy() {
        return new BiObjectiveTSPSolution(this);
    }

    @Override
    public int getNumberOfVariables() {
        return problem.getNumberOfVariables();
    }

    @Override
    public List<Integer> getTour() {
        return this.getVariables();
    }
}
