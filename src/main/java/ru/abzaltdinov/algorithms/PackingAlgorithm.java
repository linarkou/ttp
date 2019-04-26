package ru.abzaltdinov.algorithms;

import ru.abzaltdinov.model.ttp.AbstractThiefProblem;
import ru.abzaltdinov.model.ttp.solution.TTPSolution;

import java.util.List;

public abstract class PackingAlgorithm implements Algorithm {

    private List<Integer> pi;

    public void setPi(List<Integer> pi) {
        this.pi = pi;
    }

    public List<Integer> getPi() {
        return pi;
    }
}
