package ru.abzaltdinov.runner;

import ru.abzaltdinov.model.ttp.AbstractTTPInstance;
import ru.abzaltdinov.model.ttp.TTP1Instance;
import ru.abzaltdinov.model.ttp.solution.TTPSolution;
import ru.abzaltdinov.util.ConfigHelper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * Readers / writers
 */
public abstract class Util {

    public static void writeSolution(BufferedWriter writer,
                                     String problemName,
                                     String algorithmName,
                                     List<TTPSolution> TTPSolutions) throws IOException {
        for (TTPSolution TTPSolution : TTPSolutions) {

            // add one to the index of each city to match the index of the input format
            List<Integer> modTour = new ArrayList<>(TTPSolution.pi);
            for (int i = 0; i < modTour.size(); i++) {
                modTour.set(i, modTour.get(i) + 1);
            }

            String tour = modTour.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(" "));

            String packingPlan = TTPSolution.z.stream()
                    .map(v -> v ? "1" : "0")
                    .collect(Collectors.joining(" "));

            String objectives = String.format("%.16f", TTPSolution.objective);

            // write the variables
            writer.write(String.join(";",
                    problemName,
                    algorithmName,
                    objectives,
                    tour,
                    packingPlan));
            writer.newLine();
        }
    }

    public static void writeStatistics(String folder, String filename, Map<String, List<TTPSolution>> solutions) throws IOException {
        BufferedWriter writer = createBufferedWriter(folder, filename);
        writer.write(String.join(";",
                "Instance",
                "Algorithm",
                "mean",
                "stdDev",
                "max",
                "min"));
        writer.newLine();
        Function<Double, String> doubleFormatter = doubleValue -> String.format("%.16f", doubleValue);
        for (Map.Entry<String, List<TTPSolution>> entry : solutions.entrySet()) {
            String name = entry.getKey();
            List<Double> objectives = entry.getValue().stream()
                    .map(s -> s.objective)
                    .collect(Collectors.toList());
            Double min = Statistics.min(objectives);
            Double max = Statistics.max(objectives);
            Double mean = Statistics.mean(objectives);
            Double stdDev = Statistics.stdDev(objectives);
            writer.write(String.join(";",
                    name,
                    doubleFormatter.apply(mean),
                    doubleFormatter.apply(stdDev),
                    doubleFormatter.apply(max),
                    doubleFormatter.apply(min)));
            writer.newLine();
        }
        writer.close();
    }
    public static void writeSolutions(String outputFolder, String teamName, AbstractTTPInstance problem, List<TTPSolution> TTPSolutions) throws IOException {

        BufferedWriter varBw = Files.newBufferedWriter(Paths.get(outputFolder,
                String.format("%s_%s.x", teamName, problem.name)));

        BufferedWriter objBw = Files.newBufferedWriter(Paths.get(outputFolder,
                String.format("%s_%s.f", teamName, problem.name)));


        for (TTPSolution TTPSolution : TTPSolutions) {

            // add one to the index of each city to match the index of the input format
            List<Integer> modTour = new ArrayList<>(TTPSolution.pi);
            for (int i = 0; i < modTour.size(); i++) {
                modTour.set(i, modTour.get(i) + 1);
            }

            // write the variables
            varBw.write(String.join(" ",
                    modTour.stream().map(Object::toString).collect(Collectors.toList())) + "\n");
            varBw.write(String.join(" ",
                    TTPSolution.z.stream().map(b -> b ? "1" : "0").collect(Collectors.toList())) + "\n");
            varBw.write("\n");

            // write into the objective file
            String objectiveStr = String.format("%.16f", TTPSolution.objective);

            objBw.write(objectiveStr + "\n");

        }

        varBw.close();
        objBw.close();


    }


    public static void printSolutions(List<TTPSolution> TTPSolutions, boolean printVariable) {

        System.out.println(String.format("Number of non-dominated TTPSolutions: %s", TTPSolutions.size()));

        for (TTPSolution TTPSolution : TTPSolutions) {

            if (printVariable) {
                System.out.print(String.join(" ",
                        TTPSolution.pi.stream().map(Object::toString).collect(Collectors.toList())));

                System.out.print(" , ");
                System.out.print(String.join(" ",
                        TTPSolution.z.stream().map(b -> b ? "1" : "0").collect(Collectors.toList())));
                System.out.print(" ");

            }
            System.out.println(String.format("%.2f %.2f", TTPSolution.time, TTPSolution.profit));
        }
    }


    public static TTP1Instance readProblem(String fileName) throws IOException {

        TTP1Instance problem = new TTP1Instance();

        //this.name = fileName;
        String[] sp = fileName.split("/", 2);
        problem.name = sp[1];

        String ttpData = ConfigHelper.getProperty("ttpdata");

        File ttpFile = new File(ttpData+fileName);
        BufferedReader br = new BufferedReader(new FileReader(ttpFile));

        String line = br.readLine();
        while (line != null) {

            if (line.contains("PROBLEM NAME")) {
            } else if (line.contains("KNAPSACK DATA TYPE")) {
            } else if (line.contains("DIMENSION")) {
                problem.numOfCities = Integer.valueOf(line.split(":")[1].trim());
                problem.coordinates = new double[problem.numOfCities][2];
            } else if (line.contains("NUMBER OF ITEMS")) {
                problem.numOfItems = Integer.valueOf(line.split(":")[1].trim());
                problem.cityOfItem = new int[problem.numOfItems];
                problem.weight = new double[problem.numOfItems];
                problem.profit = new double[problem.numOfItems];
            } else if (line.contains("RENTING RATIO")) {
                problem.R = Double.valueOf(line.split(":")[1].trim());
            } else if (line.contains("CAPACITY OF KNAPSACK")) {
                problem.maxWeight = Integer.valueOf(line.split(":")[1].trim());
            } else if (line.contains("MIN SPEED")) {
                problem.minSpeed = Double.valueOf(line.split(":")[1].trim());
            } else if (line.contains("MAX SPEED")) {
                problem.maxSpeed = Double.valueOf(line.split(":")[1].trim());
            } else if (line.contains("EDGE_WEIGHT_TYPE")) {
                String edgeWeightType = line.split(":")[1].trim();
                if (!edgeWeightType.equals("CEIL_2D")) {
                    throw new RuntimeException("Only edge weight type of CEIL_2D supported.");
                }
            } else if (line.contains("NODE_COORD_SECTION")) {
                for (int i = 0; i < problem.numOfCities; i++) {
                    line = br.readLine();
                    String[] a = line.split("\\s+");
                    problem.coordinates[i][0] = Double.valueOf(a[1].trim());
                    problem.coordinates[i][1] = Double.valueOf(a[2].trim());
                }

            } else if (line.contains("ITEMS SECTION")) {
                for (int i = 0; i < problem.numOfItems; i++) {
                    line = br.readLine();
                    String[] a = line.split("\\s+");
                    problem.profit[i] = Double.valueOf(a[1].trim());
                    problem.weight[i] = Double.valueOf(a[2].trim());
                    problem.cityOfItem[i] = Integer.valueOf(a[3].trim()) - 1;
                }
            }
            line = br.readLine();
        }

        problem.initialize();
        br.close();

        return problem;
    }

    public static BufferedWriter createBufferedWriter(String folder, String fileName) throws IOException {
        return Files.newBufferedWriter(Paths.get(folder, fileName));
    }



    public static String getTTPInstanceName(String tspInstanceName, Integer itemsFactor,
                                            String correlatedWeights, Integer capacityCategory) {

        int firstNumericIndex = tspInstanceName.length() - 1;
        while (Character.isDigit(tspInstanceName.charAt(firstNumericIndex))) {
            firstNumericIndex--;
        }
        firstNumericIndex++;
        Integer numOfCities = Integer.valueOf(tspInstanceName.substring(firstNumericIndex));
        Integer numOfItems = (numOfCities - 1) * itemsFactor;
        String instanceName = String.format("%s_n%s_%s_%02d.ttp", tspInstanceName, numOfItems, correlatedWeights, capacityCategory);
        return instanceName;
    }

    public static List<String> getAllTTPInstanceNames(String tspInstanceName) {
        List<String> ttpInstances = new ArrayList<>();
        String ttpFolderName = String.format("src/main/resources/TTP-original-instances/%s-ttp", tspInstanceName);
        File folder = new File(ttpFolderName);
        if (!folder.exists()) {
            throw new RuntimeException("Can't find folder " + folder.getAbsolutePath());
        }
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory()) {
                ttpInstances.add(fileEntry.getName());
            }
        }
        return ttpInstances;
    }

    public static boolean equals(double d1, double d2) {
        return Math.abs(d1 - d2) < 1e-6;
    }
}
