package ru.abzaltdinov.algorithms.ea;

import ru.abzaltdinov.model.ttp.solution.TTPSolution;

public interface LocalSearch {
    TTPSolution improve(TTPSolution solution);
}
