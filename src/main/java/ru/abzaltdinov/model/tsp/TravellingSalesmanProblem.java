package ru.abzaltdinov.model.tsp;

import ru.abzaltdinov.model.tsp.solution.TSPSolution;

import java.util.Collections;
import java.util.List;

/**
 * This class represents the main problem to be solved. It stores all variables which are necessary
 * to evaluate the objective function.
 * <p>
 * Either define the problem by yourself or load it from a file.
 */
public class TravellingSalesmanProblem {

    //! name of the problem - set if read from a file
    public String name = "unknown";

    //! number of cities
    public int numOfCities = -1;

    // ! coordinate where the salesman could visit cities
    public double[][] coordinates;

    public TravellingSalesmanProblem(int numOfCities, double[][] coordinates) {
        if (numOfCities == -1) {
            throw new RuntimeException("Error while loading problem. Some variables are not initialized");
        }

        this.numOfCities = numOfCities;
        this.coordinates = coordinates;
    }

    public TravellingSalesmanProblem(String name, int numOfCities, double[][] coordinates) {
        this(numOfCities, coordinates);
        this.name = name;
    }

    /**
     * The evaluation function of the problem to simulate the tour of the thief.
     *
     * @param pi the tour
     * @return A solution containing tour and its summary distance
     */
    public TSPSolution evaluate(List<Integer> pi) {

        if (pi.size() != this.numOfCities) {
            throw new RuntimeException("Wrong input for traveling thief evaluation!");
        }

        // TSP objective
        double tourDistance = 0;

        //let start tour in 0 city
        Collections.rotate(pi, -pi.indexOf(0));

        // iterate over all possible cities
        for (int i = 0; i < this.numOfCities; i++) {

            // the city where the thief currently is
            int city = pi.get(i);

            // next city
            int next = pi.get((i + 1) % this.numOfCities);

            double distance = euclideanDistance(city, next);

            tourDistance += distance;
        }

        // create the final solution object
        TSPSolution s = new TSPSolution();

        s.distance = tourDistance;
        s.pi = pi;

        return s;
    }

    public double euclideanDistance(int a, int b) {
        return Math.sqrt(Math.pow(this.coordinates[a][0] - this.coordinates[b][0], 2)
                + Math.pow(this.coordinates[a][1] - this.coordinates[b][1], 2));
    }
}
