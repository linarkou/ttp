package ru.abzaltdinov.algorithms.tsp.nsga2;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.mutation.PermutationSwapMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import ru.abzaltdinov.model.tsp.BiObjectiveTravellingSalesmanProblem;
import ru.abzaltdinov.model.tsp.solution.BiObjectiveTSPSolution;

import java.util.List;

public class NSGA2 {
    Problem<BiObjectiveTSPSolution> problem;
    Algorithm<List<BiObjectiveTSPSolution>> algorithm;
    CrossoverOperator<BiObjectiveTSPSolution> crossover;
    MutationOperator<BiObjectiveTSPSolution> mutation;
    SelectionOperator<List<BiObjectiveTSPSolution>, BiObjectiveTSPSolution> selection;
    String referenceParetoFront = "";

    public void initialize(BiObjectiveTravellingSalesmanProblem biObjTSP) {
        problem = biObjTSP;

        double crossoverProbability = 0.9;
        crossover = new MyPMXCrossover(crossoverProbability);

        double mutationProbability = 0.2;
        mutation = new PermutationSwapMutation(mutationProbability);

        selection = new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>());

        algorithm = new NSGAIIBuilder<BiObjectiveTSPSolution>(problem, crossover, mutation, 100)
                .setVariant(NSGAIIBuilder.NSGAIIVariant.NSGAII)
                .setSelectionOperator(selection)
                .setMaxEvaluations(25000)
                .build();

    }

    public List<BiObjectiveTSPSolution> solve() {
        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute();

        List<BiObjectiveTSPSolution> population = algorithm.getResult();
        long prevComputingTime = -1;
        while (prevComputingTime != algorithmRunner.getComputingTime()) {
            prevComputingTime = algorithmRunner.getComputingTime();
        }

        new SolutionListOutput(population)
                .setSeparator("\t")
                .print();

        return population;
    }

}
