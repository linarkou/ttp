package ru.abzaltdinov.runner;

import javafx.util.Pair;
import ru.abzaltdinov.algorithms.OnePlusOneEA;
import ru.abzaltdinov.algorithms.PackingAlgorithm;
import ru.abzaltdinov.algorithms.RandomLocalSearch;
import ru.abzaltdinov.algorithms.RandomLocalSearchWith2OPT;
import ru.abzaltdinov.algorithms.tsp.LKH;
import ru.abzaltdinov.model.tsp.solution.TSPSolution;
import ru.abzaltdinov.model.ttp.SingleObjectiveThiefProblem;
import ru.abzaltdinov.model.ttp.solution.TTPSolution;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class RunnerSingleObjective {

    static final ClassLoader LOADER = RunnerSingleObjective.class.getClassLoader();
    static final Integer RUNS_PER_PROBLEM = 3;
    static final Integer NUM_OF_ITERATIONS = 1000;

    public static void main(String[] args) throws IOException {

        List<String> tspInstancesToRun = Arrays.asList(
                "eil51"
//                "eil76",
//                "kroA100",
//                "u159",
//                "ts225",
//                "a280"
        );

        LKH lkh = new LKH();
        String tspName = "";
        List<Integer> tour = null;

        Map<String, List<TTPSolution>> problemNameAndAlgoSolutions = new HashMap<>();

        for (String tspInstanceName : tspInstancesToRun) {
            List<String> instancesToRun = new ArrayList<>();

//            String ttpFolderName = String.format("src/main/resources/TTP-original-instances/%s-ttp", tspInstanceName);
//            File folder = new File(ttpFolderName);
//            if (!folder.exists()) {
//                throw new RuntimeException("Can't find folder " + folder.getAbsolutePath());
//            }
//            for (final File fileEntry : folder.listFiles()) {
//                if (!fileEntry.isDirectory()) {
//                    instancesToRun.add(fileEntry.getName());
//                }
//            }
            String capacityCategory = "01";
            String correlatedWeights = "uncorr";
            int numItemsFactor = 1;
            int firstNumericIndex = tspInstanceName.length() - 1;
            while (Character.isDigit(tspInstanceName.charAt(firstNumericIndex))) {
                firstNumericIndex--;
            }
            firstNumericIndex++;
            Integer numOfCities = Integer.valueOf(tspInstanceName.substring(firstNumericIndex));
            String instanceName = String.format("%s_n%s_%s_%s.ttp", tspInstanceName, (numOfCities - 1) * numItemsFactor, correlatedWeights, capacityCategory);

            instancesToRun.add(instanceName);

            for (String instance : instancesToRun) {
                // readProblem the problem from the file
//            String fname = String.format("resources/%s.txt", instance);
                String fname = String.format("resources/TTP-original-instances/%s-ttp/%s", tspInstanceName, instance);
                InputStream is = LOADER.getResourceAsStream(fname);

                SingleObjectiveThiefProblem problem = new SingleObjectiveThiefProblem(Util.readProblem(is));
                problem.name = instance;
                problem.initialize();

                //creating output folder & prepare writer
                File dir = new File("results");
                if (!dir.exists()) dir.mkdirs();
                BufferedWriter problemResultsWriter = Util.createBufferedWriter("results", instance + ".csv");

                //solve TSP with LKH
                if (!tspName.equals(problem.getTSP().name)) {
                    TSPSolution tspSolution = lkh.getBestTour(problem.getTSP());
                    tour = tspSolution.getTour();
                    tspName = problem.getTSP().name;
                }
                System.out.println(" === Solving " + instance + " ===");

                //print tour
                for (int i = 0; i < tour.size(); ++i) {
                    int current = tour.get(i);
                    int next = tour.get((i+1)%numOfCities);
                    int distance = (int) Math.ceil(problem.euclideanDistance(current, next));
                    System.out.println(current + " " + next + " " + distance);
                }


                // initialize your algorithm
                List<PackingAlgorithm> algorithms = Arrays.asList(
                        new RandomLocalSearch(NUM_OF_ITERATIONS)
                        //new OnePlusOneEA(NUM_OF_ITERATIONS)
//                        new RandomLocalSearchWith2OPT(NUM_OF_ITERATIONS)
                );

                for (PackingAlgorithm algorithm : algorithms) {
                    String problemNameAndAlgo = instance + ";" + algorithm.getName();
                    problemNameAndAlgoSolutions.put(problemNameAndAlgo, new ArrayList<>());
                    algorithm.setPi(tour);

                    for (int run = 0; run < RUNS_PER_PROBLEM; ++run) {
                        List<TTPSolution> solutions = algorithm.solve(problem);

                        problemNameAndAlgoSolutions.get(problemNameAndAlgo).addAll(solutions);

                        System.out.println(problem.name + " " + solutions.size());
                        for (TTPSolution s : solutions) {
                            System.out.println(s.singleObjective);
                        }

                        Util.writeSolution(problemResultsWriter, instance, algorithm.getName(), solutions);
                    }
                }


                problemResultsWriter.close();
            }
        }
        Util.writeStatistics("results",
                String.format("statistics_%s.csv", System.currentTimeMillis()),
                problemNameAndAlgoSolutions
        );

    }

}