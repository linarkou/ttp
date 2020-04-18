package ru.abzaltdinov.runner;

import java.util.List;
import java.util.stream.DoubleStream;

public class Statistics {

    public static double max(List<Double> values) {
        return values.stream().max(Double::compareTo).get();
    }

    public static double min(List<Double> values) {
        return values.stream().min(Double::compareTo).get();
    }

    public static double mean(List<Double> values) {
        if (values.size() == 0) {
            return 0;
        }
        Double sum = 0d;
        for (Double v : values) {
            sum += v;
        }
        return sum / values.size();
    }

    public static double stdDev(List<Double> values) {
        if (values.size() == 0) {
            return 0;
        }
        double mean = mean(values);
        double sum = 0d;
        for (Double v : values) {
            sum += Math.pow(v - mean, 2);
        }
        return Math.sqrt(sum / values.size());
    }
}
