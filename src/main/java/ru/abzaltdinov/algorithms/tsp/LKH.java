package ru.abzaltdinov.algorithms.tsp;

import ru.abzaltdinov.model.tsp.TSPInstance;
import ru.abzaltdinov.model.tsp.solution.TSPSolution;
import ru.abzaltdinov.util.BashExecutor;
import ru.abzaltdinov.util.Parameters;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static ru.abzaltdinov.util.Parameters.*;

public class LKH {

    public TSPSolution getBestTour(TSPInstance tsp) {
        File bestTourFile = getBestTourFile(tsp.name);

        if (bestTourFile == null) {  //if TSP not already solved
            runLKH(tsp.name);
            bestTourFile = getBestTourFile(tsp.name);
        }
        List<Integer> tour = readTour(bestTourFile);
        return tsp.evaluate(tour);
    }

    public List<TSPSolution> getTours(TSPInstance tsp) {
        File[] allTourFiles = getAllTourFiles(tsp.name);

        if (allTourFiles.length == 0) {  //if TSP not already solved
            runLKH(tsp.name);
            allTourFiles = getAllTourFiles(tsp.name);
        }

        List<TSPSolution> tours = new ArrayList<>();
        for (File tourFile : allTourFiles) {
            List<Integer> tour = readTour(tourFile);
            tours.add(tsp.evaluate(tour));
        }

        return tours;
    }

    public List<Integer> readTour(File tourFile) {
        List<Integer> result = new ArrayList<>();
        try (FileReader fr = new FileReader(tourFile)) {
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();
            while (!line.startsWith("TOUR_SECTION")) line = br.readLine();
            line = br.readLine();

            while (!line.equals("-1")) {
                result.add(Integer.valueOf(line) - 1);
                line = br.readLine();
            }
        } catch (Exception e) {
            throw new RuntimeException("Can't read tour from file " + tourFile.getPath());
        }
        return result;
    }

    public void runLKH(String problemName) {
        try {
            File LKHparams = generateLKHParametersFile(problemName);
            BashExecutor.exec(PATH_LKH, LKHparams.getAbsolutePath(), true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Can't solve TSP subproblem using LKH!");
        }
    }

    private File[] getAllTourFiles(String problemName) {
        File toursDir = new File(PATH_TSP_TOURS_ABSOLUTE);
        File[] foundFiles = toursDir.listFiles((dir, name) -> name.startsWith(problemName));
        return foundFiles;
    }

    public File getBestTourFile(String problemName) {
        File toursDir = new File(PATH_TSP_TOURS_ABSOLUTE);
        File[] allTourFiles = toursDir.listFiles((dir, name) -> name.startsWith(problemName));
        return allTourFiles.length == 0 ? null : allTourFiles[0];
    }

    private File generateLKHParametersFile(String problemName) throws Exception {
        InputStream is = new FileInputStream(Parameters.PATH_LKH_PARAMS);
        Properties lkhProps = new Properties();
        lkhProps.load(is);

        File lkhParams = new File(PATH_RESOURCES_LKH, String.format("%s.par", problemName));
        if (!lkhParams.exists()) {
            lkhParams.createNewFile();
        }
        PrintWriter lkhParamsWriter = new PrintWriter(lkhParams);

        for (String param : lkhProps.stringPropertyNames()) {
            switch (param) {
                case "PROBLEM_FILE":
                    String problemFileName = String.format(lkhProps.getProperty(param), PATH_RESOURCES_TSP + "/" + problemName);
                    lkhParamsWriter.println(String.format("%s = %s", param, problemFileName));
                    break;
                case "TOUR_FILE":
                    String outputFileName = String.format(lkhProps.getProperty(param), PATH_TSP_TOURS_RELATIVE + "/" + problemName);
                    lkhParamsWriter.println(String.format("%s = %s", param, outputFileName));
                    break;
                default:
                    lkhParamsWriter.println(String.format("%s = %s", param, lkhProps.getProperty(param)));
                    break;
            }
        }
        lkhParamsWriter.close();
        return lkhParams;
    }
}
