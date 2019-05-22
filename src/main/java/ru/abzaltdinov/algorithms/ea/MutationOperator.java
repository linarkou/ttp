package ru.abzaltdinov.algorithms.ea;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.abzaltdinov.model.Pair;
import ru.abzaltdinov.model.ttp.solution.TTPSolution;

@Getter
@AllArgsConstructor
public abstract class MutationOperator<T1, T2> {
    private double probability;

    public abstract Pair<T1, T2> mutate(TTPSolution solution);
}
