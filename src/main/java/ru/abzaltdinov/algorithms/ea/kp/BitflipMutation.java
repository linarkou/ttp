package ru.abzaltdinov.algorithms.ea.kp;

import ru.abzaltdinov.algorithms.ea.MutationOperator;
import ru.abzaltdinov.model.Pair;
import ru.abzaltdinov.model.ttp.AbstractTTPInstance;
import ru.abzaltdinov.model.ttp.solution.TTPSolution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BitflipMutation extends MutationOperator<Integer, List<Boolean>> {

    private AbstractTTPInstance ttp;

    public BitflipMutation(AbstractTTPInstance ttp, double probability) {
        super(probability);
        this.ttp = ttp;
    }

    @Override
    public Pair<Integer, List<Boolean>> mutate(TTPSolution solution) {
        List<Boolean> packingPlan = new ArrayList<>(solution.z);

        if (Math.random() < getProbability()) {
            while (true) {
                int item = new Random().nextInt(packingPlan.size());
                packingPlan.set(item, !packingPlan.get(item));
                if (!(packingPlan.get(item) && ttp.weight[item] + solution.weigthOfItems > ttp.maxWeight)) {
                    return new Pair<>(item, packingPlan);
                }
            }
        } else {
            return new Pair<>(null, packingPlan);
        }
    }
}
