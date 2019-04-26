package ru.abzaltdinov.algorithms.tsp.nsga2;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.solution.PermutationSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;
import ru.abzaltdinov.model.tsp.solution.BiObjectiveTSPSolution;

import java.util.ArrayList;
import java.util.List;

public class MyPMXCrossover implements CrossoverOperator<BiObjectiveTSPSolution> {
    private double crossoverProbability;
    private BoundedRandomGenerator<Integer> cuttingPointRandomGenerator;
    private RandomGenerator<Double> crossoverRandomGenerator;

    public MyPMXCrossover(double crossoverProbability) {
        this(crossoverProbability, () -> {
            return JMetalRandom.getInstance().nextDouble();
        }, (a, b) -> {
            return JMetalRandom.getInstance().nextInt(a, b);
        });
    }

    public MyPMXCrossover(double crossoverProbability, RandomGenerator<Double> randomGenerator) {
        this(crossoverProbability, randomGenerator, BoundedRandomGenerator.fromDoubleToInteger(randomGenerator));
    }

    public MyPMXCrossover(double crossoverProbability, RandomGenerator<Double> crossoverRandomGenerator, BoundedRandomGenerator<Integer> cuttingPointRandomGenerator) {
        this.crossoverProbability = 1.0D;
        if (crossoverProbability >= 0.0D && crossoverProbability <= 1.0D) {
            this.crossoverProbability = crossoverProbability;
            this.crossoverRandomGenerator = crossoverRandomGenerator;
            this.cuttingPointRandomGenerator = cuttingPointRandomGenerator;
        } else {
            throw new JMetalException("Crossover probability value invalid: " + crossoverProbability);
        }
    }

    public double getCrossoverProbability() {
        return this.crossoverProbability;
    }

    public void setCrossoverProbability(double crossoverProbability) {
        this.crossoverProbability = crossoverProbability;
    }

    @Override
    public List<BiObjectiveTSPSolution> execute(List<BiObjectiveTSPSolution> parents) {
        if (null == parents) {
            throw new JMetalException("Null parameter");
        } else if (parents.size() != 2) {
            throw new JMetalException("There must be two parents instead of " + parents.size());
        } else {
            return this.doCrossover(this.crossoverProbability, parents);
        }
    }


    public List<BiObjectiveTSPSolution> doCrossover(double probability, List<BiObjectiveTSPSolution> parents) {
        List<BiObjectiveTSPSolution> offspring = new ArrayList(2);
        offspring.add(parents.get(0).copy());
        offspring.add(parents.get(1).copy());
        int permutationLength = ((PermutationSolution) parents.get(0)).getNumberOfVariables();
        if ((Double) this.crossoverRandomGenerator.getRandomValue() < probability) {
            int cuttingPoint1 = (Integer) this.cuttingPointRandomGenerator.getRandomValue(1, permutationLength - 1);

            int cuttingPoint2;
            for (cuttingPoint2 = (Integer) this.cuttingPointRandomGenerator.getRandomValue(1, permutationLength - 1); cuttingPoint2 == cuttingPoint1; cuttingPoint2 = (Integer) this.cuttingPointRandomGenerator.getRandomValue(0, permutationLength - 1)) {
            }

            if (cuttingPoint1 > cuttingPoint2) {
                int swap = cuttingPoint1;
                cuttingPoint1 = cuttingPoint2;
                cuttingPoint2 = swap;
            }

            int[] replacement1 = new int[permutationLength];
            int[] replacement2 = new int[permutationLength];

            int i;
            for (i = 0; i < permutationLength; ++i) {
                replacement1[i] = replacement2[i] = -1;
            }

            for (i = cuttingPoint1; i <= cuttingPoint2; ++i) {
                ((PermutationSolution) offspring.get(0)).setVariableValue(i, ((PermutationSolution) parents.get(1)).getVariableValue(i));
                ((PermutationSolution) offspring.get(1)).setVariableValue(i, ((PermutationSolution) parents.get(0)).getVariableValue(i));
                replacement1[(Integer) ((PermutationSolution) parents.get(1)).getVariableValue(i)] = (Integer) ((PermutationSolution) parents.get(0)).getVariableValue(i);
                replacement2[(Integer) ((PermutationSolution) parents.get(0)).getVariableValue(i)] = (Integer) ((PermutationSolution) parents.get(1)).getVariableValue(i);
            }

            for (i = 0; i < permutationLength; ++i) {
                if (i < cuttingPoint1 || i > cuttingPoint2) {
                    int n1 = (Integer) ((PermutationSolution) parents.get(0)).getVariableValue(i);
                    int m1 = replacement1[n1];
                    int n2 = (Integer) ((PermutationSolution) parents.get(1)).getVariableValue(i);

                    int m2;
                    for (m2 = replacement2[n2]; m1 != -1; m1 = replacement1[m1]) {
                        n1 = m1;
                    }

                    while (m2 != -1) {
                        n2 = m2;
                        m2 = replacement2[m2];
                    }

                    ((PermutationSolution) offspring.get(0)).setVariableValue(i, n1);
                    ((PermutationSolution) offspring.get(1)).setVariableValue(i, n2);
                }
            }
        }

        return offspring;
    }

    @Override
    public int getNumberOfRequiredParents() {
        return 2;
    }

    @Override
    public int getNumberOfGeneratedChildren() {
        return 2;
    }

}
