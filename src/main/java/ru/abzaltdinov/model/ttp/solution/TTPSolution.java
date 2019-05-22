package ru.abzaltdinov.model.ttp.solution;

import java.util.List;

/**
 * This is a solution objective which stores the tour, packing plan and the objective values.
 */
public class TTPSolution implements Comparable<TTPSolution> {

    //! the tour of the thief
    public List<Integer> pi;

    //! the packing plan
    public List<Boolean> z;

    //! the time the thief needed for traveling
    public double time = -1.0;

    //! the profit the thief made on that tour
    public double profit = -1.0;

    //! objective value if you want to solve the single-objective problem using R
    public double objective = Double.NEGATIVE_INFINITY;

    public double weigthOfItems;

    public TTPSolution(List<Integer> pi, List<Boolean> z) {
        this.pi = pi;
        this.z = z;
    }

    public TTPSolution() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TTPSolution that = (TTPSolution) o;

        return pi.equals(that.pi) && z.equals(that.z);

    }

    @Override
    public int hashCode() {
        int result = pi.hashCode();
        result = 31 * result + z.hashCode();
        return result;
    }

    @Override
    public int compareTo(TTPSolution other) {
        double o1 = this.objective;
        double o2 = other.objective;
        if (Math.abs(o1 - o2) < 1e-6) return 0;
        return Double.compare(o1, o2);
    }
}
