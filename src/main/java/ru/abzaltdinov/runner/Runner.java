package ru.abzaltdinov.runner;

import ru.abzaltdinov.algorithms.Algorithm;
import ru.abzaltdinov.algorithms.S5;
import ru.abzaltdinov.algorithms.ea.MyOnePlusOneEA;
import ru.abzaltdinov.model.ttp.TTP1Instance;
import ru.abzaltdinov.model.ttp.solution.TTPSolution;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.*;

public class Runner {

    static final ClassLoader LOADER = Runner.class.getClassLoader();
    static final Integer NUM_OF_ITERATIONS = 10000;
    // initialize your algorithm
    static double TOUR_MUTATION_PROBABILITY = 0.9;
    static double PACK_PLAN_MUTATION_PROBABILITY = 0.9;

    public static void main(String[] args) throws IOException {

        if (args.length < 2) {
            args = new String[]{"bier127_n1260_bounded-strongly-corr_03.ttp", "MyAlgo"};
        }

        String[] spl = args[0].split("_", 2);

        // TTP instance name
        final String instance = args[0];

        // algorithm name
        final String algoName = args[1];

        // output file
        final String outputFile = "./output/" + algoName + ".csv";

        // runtime limit
        long runtimeLimit = 600;
        if (args.length >= 3) {
            runtimeLimit = Long.parseLong(args[2]);
        }

        // repeats
        long repeats = 1;
        if (args.length >= 4) {
            repeats = Long.parseLong(args[3]);
        }

        // TTP instance
        final TTP1Instance problem = Util.readProblem(spl[0] + "-ttp/" + instance);

        /* algorithm to run */
        final Algorithm algorithm;
        switch (algoName) {
            case "s5":
                algorithm = new S5();
                break;

            case "MyAlgo":
                algorithm = new MyOnePlusOneEA(PACK_PLAN_MUTATION_PROBABILITY, TOUR_MUTATION_PROBABILITY, NUM_OF_ITERATIONS);
                break;

            default:
                algorithm = new MyOnePlusOneEA(PACK_PLAN_MUTATION_PROBABILITY, TOUR_MUTATION_PROBABILITY, NUM_OF_ITERATIONS);
                break;
        }
        // runnable class
        class TTPRunnable implements Runnable {

            String resultLine;
            TTPSolution solution = new TTPSolution();

            @Override
            public void run() {
                solution = algorithm.solve(problem);

                /* print result */
                resultLine = instance + ";" + algoName + ";" + Math.round(solution.objective);
            }
        }

        for (int runs = 0; runs < repeats; ++runs) {

            // my TTP runnable
            TTPRunnable ttprun = new TTPRunnable();
            ExecutorService executor = Executors.newFixedThreadPool(1);
            Future<?> future = executor.submit(ttprun);
            executor.shutdown();  // reject all further submissions

            // limit execution time to 600 seconds
            try {
                future.get(runtimeLimit, TimeUnit.SECONDS);  // wait X seconds to finish
            } catch (InterruptedException e) {
                System.out.println("job was interrupted");
            } catch (ExecutionException e) {
                System.out.println("caught exception: " + e.getCause());
            } catch (TimeoutException e) {
                future.cancel(true);
                System.out.println("/!\\ Timeout");
            }

            // wait for execution to be done
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // print to console
            System.out.println(ttprun.resultLine);

            // log results into text file
            try {
                File file = new File(outputFile);
                if (!file.exists()) file.createNewFile();
                Files.write(Paths.get(outputFile), (ttprun.resultLine + "\n").getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
