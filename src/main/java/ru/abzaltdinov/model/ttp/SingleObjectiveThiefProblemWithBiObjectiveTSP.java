package ru.abzaltdinov.model.ttp;

import ru.abzaltdinov.model.tsp.BiObjectiveTravellingSalesmanProblem;

/**
 * This class represents the single-objective travelling thief problem (TTP1),
 * that include my extended bi-objective TSP*.
 */
public class SingleObjectiveThiefProblemWithBiObjectiveTSP extends AbstractThiefProblem {

    public BiObjectiveTravellingSalesmanProblem biObjectiveTSP = null;

    public SingleObjectiveThiefProblemWithBiObjectiveTSP() {
    }

    public SingleObjectiveThiefProblemWithBiObjectiveTSP(BiObjectiveThiefProblem problem) {
        super(problem);
    }

    @Override
    public int getNumberOfObjectives() {
        return 1;
    }


    @Override
    public void initializeTSP() {
        String tspProblemName = name.split("[_-]")[0];
        biObjectiveTSP = new BiObjectiveTravellingSalesmanProblem(tspProblemName,
                numOfCities, coordinates, weight, cityOfItem);
    }
}
