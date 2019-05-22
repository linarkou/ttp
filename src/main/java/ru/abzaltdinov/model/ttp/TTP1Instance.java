package ru.abzaltdinov.model.ttp;

import ru.abzaltdinov.model.tsp.TSPInstance;
import ru.abzaltdinov.runner.Util;
import ru.abzaltdinov.util.ConfigHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * This class represents the single-objective travelling thief problem (TTP1).
 */
public class TTP1Instance extends AbstractTTPInstance {

    //Travelling salesman subproblem
    public TSPInstance TSP;

    public TTP1Instance() {
    }

    public TSPInstance getTSP() {
        return TSP;
    }

    public TTP1Instance(AbstractTTPInstance problem) {
        super(problem);
    }

    @Override
    public int getNumberOfObjectives() {
        return 1;
    }


    @Override
    public void initializeTSP() {
        String tspProblemName = name.split("[_-]")[0];
        TSP = new TSPInstance(tspProblemName, numOfCities, coordinates);
    }
}
