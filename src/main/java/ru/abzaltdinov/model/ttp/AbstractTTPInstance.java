package ru.abzaltdinov.model.ttp;

import ru.abzaltdinov.model.ttp.solution.TTPSolution;
import ru.abzaltdinov.runner.Util;
import ru.abzaltdinov.util.ConfigHelper;

import java.io.*;
import java.util.*;

/**
 * This class represents the main problem to be solved. It stores all variables which are necessary
 * to evaluate the objective function.
 * <p>
 * Either define the problem by yourself or load it from a file.
 */
public abstract class AbstractTTPInstance {

    //! name of the problem - set if read from a file
    public String name = "unknown";

    //! number of cities
    public int numOfCities = -1;

    //! number of items
    public int numOfItems = -1;

    // ! minimal speed of the salesman
    public double minSpeed = -1;

    // ! maximal speed of the salesman
    public double maxSpeed = -1;

    // ! maximal weight of the knapsack
    public int maxWeight = -1;

    //! Renting Rate (not needed for multi-objective version)
    public double R = Double.POSITIVE_INFINITY;

    // ! coordinate where the salesman could visit cities
    public double[][] coordinates;

    //! corresponding city of each item
    public int[] cityOfItem;

    // ! the weight of each item
    public double[] weight;

    // ! the profit of each item
    public double[] profit;

    //! used for faster evaluation
    public List<LinkedList<Integer>> itemsAtCity = null;

    public List<List<Integer>> allListsOfEqualItems = new ArrayList<>();

    public Map<Integer, List<Integer>> equalItems = new HashMap<>();

    public abstract int getNumberOfObjectives();

    public AbstractTTPInstance() {
    }

    public AbstractTTPInstance(AbstractTTPInstance problem) {
        this.numOfCities = problem.numOfCities;
        this.numOfItems = problem.numOfItems;
        this.minSpeed = problem.minSpeed;
        this.maxSpeed = problem.maxSpeed;
        this.R = problem.R;
        this.maxWeight = problem.maxWeight;
        this.coordinates = problem.coordinates;
        this.cityOfItem = problem.cityOfItem;
        this.weight = problem.weight;
        this.profit = problem.profit;
        this.initialize();
    }

    /**
     * Initialize the problem by saving for each city the items to pick
     */
    public void initialize() {

        // make the checks to avoid wrong parameters for the problem
        if (numOfCities == -1 || numOfItems == -1 || minSpeed == -1 || maxSpeed == -1 || maxWeight == -1
                || R == Double.POSITIVE_INFINITY) {
            throw new RuntimeException("Error while loading problem. Some variables are not initialized");
        }


        // initialize the itemsAtCity data structure
        this.itemsAtCity = new ArrayList<>(this.numOfCities);
        for (int i = 0; i < this.numOfCities; i++) {
            this.itemsAtCity.add(new LinkedList<>());
        }
        for (int i = 0; i < this.cityOfItem.length; i++) {
            this.itemsAtCity.get(this.cityOfItem[i]).add(i);
        }

        //build lists of equal items
        Map<Integer, Map<Integer, List<Integer>>> profitWeightToItems = new HashMap<>();
        for (int item = 0; item < this.numOfItems; ++item) {
            int profit = (int) this.profit[item];
            int weight = (int) this.weight[item];
            if (profitWeightToItems.get(profit) == null) {
                profitWeightToItems.put(profit, new HashMap<>());
            }
            List<Integer> listOfEqualItems = profitWeightToItems.get(profit).get(weight);
            if (listOfEqualItems == null) {
                ArrayList<Integer> newList = new ArrayList<>();
                newList.add(item);
                profitWeightToItems.get(profit).put(weight, newList);
                allListsOfEqualItems.add(newList);
                equalItems.put(item, newList);
            } else {
                listOfEqualItems.add(item);
                equalItems.put(item, listOfEqualItems);
            }
        }
        initializeTSP();
    }

    public abstract void initializeTSP();


    /**
     * See evaluate(pi,z,copy). Per default pi and z are not copied.
     */
    public TTPSolution evaluate(List<Integer> pi, List<Boolean> z) {
        return evaluate(pi, z, false);
    }

    /**
     * The evaluation function of the problem to simulate the tour of the thief.
     *
     * @param pi   the tour
     * @param z    the packing plan
     * @param copy if true the returned solution object has a copy of the tour and packing plan - otherwise
     *             just a reference. Be careful here, if you change the tour afterwards, the result will not match finally.
     * @return A solution objective containing
     */
    public TTPSolution evaluate(List<Integer> pi, List<Boolean> z, boolean copy) {

        if (pi.size() != this.numOfCities || z.size() != this.numOfItems) {
            throw new RuntimeException("Wrong input for traveling thief evaluation!");
        } else if (pi.get(0) != 0) {
            throw new RuntimeException("Thief must start at city 0!");
        }

        // the values that are evaluated in this function
        double time = 0;
        double profit = 0;

        // attributes in the beginning of the tour
        double weight = 0;

        // iterate over all possible cities
        for (int i = 0; i < this.numOfCities; i++) {

            // the city where the thief currently is
            int city = pi.get(i);

            // for each item index this city
            for (int j : this.itemsAtCity.get(city)) {

                // if the thief picks that item
                if (z.get(j)) {
                    // update the current weight and profit
                    weight += this.weight[j];
                    profit += this.profit[j];
                }

            }

            // if the maximum capacity constraint is reached
            if (weight > maxWeight) {
                time = Double.MAX_VALUE;
                profit = -Double.MAX_VALUE;
                break;
            }

            // update the speed accordingly
            double speed = this.maxSpeed - (weight / this.maxWeight) * (this.maxSpeed - this.minSpeed);

            // increase time by considering the speed - do not forget the way from the last city to the first!
            int next = pi.get((i + 1) % this.numOfCities);
            long distance = (long) Math.ceil(distance(city, next));

            time += distance / speed;

        }
        // create the final solution object
        TTPSolution s = new TTPSolution();
        if (copy) {
            s.pi = new ArrayList<>(pi);
            s.z = new ArrayList<>(z);
        } else {
            s.pi = pi;
            s.z = z;
        }
        s.time = time;
        s.profit = profit;
        s.objective = profit - this.R * time;
        s.weigthOfItems = weight;

        return s;
    }

    public long distance(int a, int b) {
        return (long) Math.ceil(Math.sqrt(
                Math.pow(this.coordinates[a][0] - this.coordinates[b][0], 2)
                        + Math.pow(this.coordinates[a][1] - this.coordinates[b][1], 2)
        ));
    }


    /**
     * This method verifies if the tour and packing plan is matching with the objective values saved in this object.
     *
     * @throws RuntimeException
     */
    public void verify(TTPSolution s) throws RuntimeException {
        TTPSolution correct = this.evaluate(s.pi, s.z);
        if (s.time != correct.time || s.profit != correct.profit) {
            throw new RuntimeException("Pi and Z are not matching with the objectives values time and profit.");
        }

    }


}
