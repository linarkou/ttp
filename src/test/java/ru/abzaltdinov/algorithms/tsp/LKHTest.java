package ru.abzaltdinov.algorithms.tsp;

import ru.abzaltdinov.model.tsp.TravellingSalesmanProblem;
import org.junit.Test;

public class LKHTest {
    private LKH lkhSolver = new LKH();

    @Test
    public void getTour() {
        TravellingSalesmanProblem tsp = new TravellingSalesmanProblem("a280", 0, new double[][]{});
        lkhSolver.getTours(tsp);
    }
}