package ru.abzaltdinov.model.ttp;

import ru.abzaltdinov.model.tsp.TravellingSalesmanProblem;

/**
 * This class represents the single-objective travelling thief problem (TTP1).
 */
public class SingleObjectiveThiefProblem extends AbstractThiefProblem {

    //Travelling salesman subproblem
    public TravellingSalesmanProblem TSP;

    public SingleObjectiveThiefProblem() {
    }

    public TravellingSalesmanProblem getTSP() {
        return TSP;
    }

    public SingleObjectiveThiefProblem(BiObjectiveThiefProblem problem) {
        super(problem);
    }

    @Override
    public int getNumberOfObjectives() {
        return 1;
    }


    @Override
    public void initializeTSP() {
        String tspProblemName = name.split("[_-]")[0];
        TSP = new TravellingSalesmanProblem(tspProblemName, numOfCities, coordinates);
    }
}
