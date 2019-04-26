package ru.abzaltdinov.model.tsp;

import org.uma.jmetal.problem.PermutationProblem;
import org.uma.jmetal.problem.impl.AbstractGenericProblem;
import ru.abzaltdinov.model.tsp.solution.BiObjectiveTSPSolution;

public class BiObjectiveTravellingSalesmanProblem extends AbstractGenericProblem<BiObjectiveTSPSolution> implements PermutationProblem<BiObjectiveTSPSolution> {
    //! name of the problem - set if read from a file
    public String name = "unknown";

    //! number of cities
    public int numOfCities = -1;

    // ! coordinate where the salesman could visit cities
    public double[][] coordinates;

    //! corresponding city of each item
    public int[] cityOfItem;

    // ! the weight of each item
    public double[] weight;

    public double[] weightOfCity;

    public BiObjectiveTravellingSalesmanProblem(int numOfCities,
                                                double[][] coordinates,
                                                double[] weight,
                                                int[] cityOfItem) {
        this("unknown", numOfCities, coordinates, weight, cityOfItem);
    }

    public BiObjectiveTravellingSalesmanProblem(String name,
                                                int numOfCities,
                                                double[][] coordinates,
                                                double[] weight,
                                                int[] cityOfItem) {
        this.name = name;
        this.numOfCities = numOfCities;
        this.coordinates = coordinates;
        this.weight = weight;
        this.cityOfItem = cityOfItem;
        this.weightOfCity = new double[numOfCities];
        for (int itemIndex = 0; itemIndex < cityOfItem.length; ++itemIndex) {
            int cityIndex = cityOfItem[itemIndex];
            weightOfCity[cityIndex] += weight[itemIndex];
        }

        this.setNumberOfVariables(this.numOfCities);
        this.setNumberOfObjectives(2);
        this.setName("TSP*");
    }

    @Override
    public int getPermutationLength() {
        return numOfCities;
    }

    @Override
    public void evaluate(BiObjectiveTSPSolution solution) {
        double fitness1;
        double fitness2;

        fitness1 = 0.0;
        fitness2 = 0.0;

        for (int i = 0; i < numOfCities; i++) {
            int x;
            int y;

            x = solution.getVariableValue(i % numOfCities) ;
            y = solution.getVariableValue((i + 1) % numOfCities);

            fitness1 += euclideanDistance(x, y);
            fitness2 += fitness1 * weightOfCity[y];
        }

        solution.setObjective(0, fitness1);
        solution.setObjective(1, fitness2);
    }

    @Override
    public BiObjectiveTSPSolution createSolution() {
        return new BiObjectiveTSPSolution(this);
    }

    public double euclideanDistance(int a, int b) {
        return Math.sqrt(Math.pow(this.coordinates[a][0] - this.coordinates[b][0], 2)
                + Math.pow(this.coordinates[a][1] - this.coordinates[b][1], 2));
    }
}
