package ru.abzaltdinov.algorithms.ea.tsp;

import ru.abzaltdinov.algorithms.ea.MutationOperator;
import ru.abzaltdinov.model.Pair;
import ru.abzaltdinov.model.ttp.AbstractTTPInstance;
import ru.abzaltdinov.model.ttp.solution.TTPSolution;

public class EmptyMutation extends MutationOperator<Integer, TTPSolution> {

    private AbstractTTPInstance ttp;

    public EmptyMutation(AbstractTTPInstance ttp, double probability) {
        super(probability);
        this.ttp = ttp;
    }

    @Override
    public Pair<Integer, TTPSolution> mutate(TTPSolution solution) {
        return new Pair<>(null, solution);
    }
}
