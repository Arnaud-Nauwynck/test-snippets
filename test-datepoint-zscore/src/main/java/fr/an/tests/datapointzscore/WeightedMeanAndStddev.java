package fr.an.tests.datapointzscore;

import lombok.Value;
import lombok.val;

@Value
public class WeightedMeanAndStddev {
    public final double sumCoefs;
    public final double average;
    public final double stddev;

    public static WeightedMeanAndStddev fromSum(double sumCoefs, double sum, double sumSquares) {
        if (sumCoefs != 0.0) {
            val mean = sum / sumCoefs;
            val stddev = stdDevFromSums(sumCoefs, sum, sumSquares);
            return new WeightedMeanAndStddev(sumCoefs, mean, stddev);
        } else {
            return new WeightedMeanAndStddev(0.0, 0, 0);
        }
    }

    public static double stdDevFromSums(double n, double sum, double sumOfSquares) {
        double variance = (sumOfSquares - (sum * sum / n)) / (n - 1);
        return Math.sqrt(variance);
    }

    public double zscoreFor(double value) {
        if (Math.abs(sumCoefs) < 1e-8) {
            return 0;
        }
        if (Math.abs(stddev) < 1e-8) {
            if (value == average) return 0.0;
            return (value < average) ? -1.0 : 1.0;
        }
        return (value - average) / stddev;
    }

    @Override
    public String toString() {
        return "{" +
                "sumCoefs=" + sumCoefs +
                ", average=" + average +
                ", stddev=" + stddev +
                '}';
    }
}
