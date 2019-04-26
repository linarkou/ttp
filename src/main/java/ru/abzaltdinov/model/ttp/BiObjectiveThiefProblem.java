package ru.abzaltdinov.model.ttp;

import ru.abzaltdinov.model.tsp.TravellingSalesmanProblem;

/**
 * This class represents the bi-objective travelling thief problem (TTP2).
 */
public class BiObjectiveThiefProblem extends AbstractThiefProblem {

    //Travelling salesman subproblem
    public TravellingSalesmanProblem TSP;

    @Override
    public int getNumberOfObjectives() {
        return 2;
    }

    @Override
    public void initializeTSP() {
        String tspProblemName = name.split("[_-]")[0];
        TSP = new TravellingSalesmanProblem(tspProblemName, numOfCities, coordinates);
    }
}
