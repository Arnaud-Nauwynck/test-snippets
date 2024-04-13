package fr.an.tests.datapointzscore;

import lombok.Value;
import lombok.val;

@Value
public class MeanAndStddev {
    public final int count;
    public final double average;
    public final double stddev;

    public static MeanAndStddev fromSum(int count, double sum, double sumSquares) {
        if (count > 1) {
            val mean = sum / count;
            val stddev = stdDevFromSumCoefs(count, sum, sumSquares);
            return new MeanAndStddev(count, mean, stddev);
        } else {
            return new MeanAndStddev(count, 0, 0);
        }
    }

    public static double stdDevFromSumCoefs(int n, double sum, double sumOfSquares) {
        double variance = (sumOfSquares - (sum * sum / n)) / (n - 1);
        return Math.sqrt(variance);
    }
    public static double stdDevFromSumCoefs(double sumCoefs, double sumCoefValues, double sumCoefValueSquares) {
        double variance = (sumCoefValueSquares - (sumCoefValues * sumCoefValues / sumCoefs)) / sumCoefs; // instead of (sumCoefs-1) ??
        return Math.sqrt(variance);
    }

    public double zscoreFor(double value) {
        if (count < 3) {
            return 0;
        }
        if (stddev == 0.0) {
            if (value == average) return 0.0;
            return (value < average) ? -1.0 : 1.0;
        }
        return (value - average) / stddev;
    }

    @Override
    public String toString() {
        return "{" +
                "count=" + count +
                ", average=" + average +
                ", stddev=" + stddev +
                '}';
    }

}
