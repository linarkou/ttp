package ru.abzaltdinov.runner;

import ru.abzaltdinov.algorithms.LKHAndPackInTheEnd;
import ru.abzaltdinov.model.ttp.solution.TTPSolution;
import ru.abzaltdinov.model.ttp.BiObjectiveThiefProblem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;

public class RunnerBiObjective {


    static final ClassLoader LOADER = RunnerBiObjective.class.getClassLoader();

    public static void main(String[] args) throws IOException {

        //List<String> instanceToRun = Arrays.asList("a280-n279");
        List<String> instanceToRun = ru.abzaltdinov.runner.Competition.INSTANCES;

        for (String instance : instanceToRun) {

            // readProblem the problem from the file
            String fname = String.format("resources/%s.txt", instance);
            InputStream is = LOADER.getResourceAsStream(fname);

            BiObjectiveThiefProblem problem = Util.readProblem(is);
            problem.name = instance;
            problem.initialize();

            // number of solutions that will be finally necessary for submission - not used here
            int numOfSolutions = Competition.numberOfSolutions(problem);

            // initialize your algorithm
            LKHAndPackInTheEnd algorithm = new LKHAndPackInTheEnd(0.3, 100);
//            Algorithm algorithm = new RandomLocalSearch(100);

            // use it to to solve the problem and return the non-dominated set
            List<TTPSolution> nds = algorithm.solve(problem);

            // sort by time and printSolutions it
            nds.sort(Comparator.comparing(a -> a.time));

            System.out.println(nds.size());
            for(TTPSolution s : nds) {
                System.out.println(s.time + " " + s.profit);
            }

            Util.printSolutions(nds, true);
            System.out.println(problem.name + " " + nds.size());

            File dir = new File("results");
            if (!dir.exists()) dir.mkdirs();
            Util.writeSolutions("results", Competition.TEAM_NAME, problem, nds);
        }
    }

}