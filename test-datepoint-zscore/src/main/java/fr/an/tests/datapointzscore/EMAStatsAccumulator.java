package fr.an.tests.datapointzscore;

import lombok.Getter;
import lombok.val;

/**
 * stats accumulator using EMA = Exponential Moving Average (=exponential smoothing)
 */
@Getter
public class EMAStatsAccumulator {
    protected double sumCoefs;
    protected double sumCoefValues;
    protected double sumCoefSquareValues;

    //---------------------------------------------------------------------------------------------

    public EMAStatsAccumulator() {
    }

    public EMAStatsAccumulator(WeightedMeanAndStddev src) {
        val n = this.sumCoefs = src.sumCoefs;
        val sum = this.sumCoefValues = src.average * n;
        // stddev = Math.sqrt(variance);
        double variance = src.stddev * src.stddev;
        // double variance = (sumOfSquares - (sum * sum / n)) / (n - 1);
        this.sumCoefSquareValues = (n > 1.0)? variance * (n-1) + sum * sum / n : 0;
    }


    //---------------------------------------------------------------------------------------------

    public void add(double coef, double value) {
        final double complCoef = 1.0 - coef;
        this.sumCoefs = coef + complCoef * sumCoefs;
        this.sumCoefValues = coef * value + complCoef * sumCoefValues;
        this.sumCoefSquareValues = coef * value * value + complCoef * sumCoefSquareValues;
    }

    public WeightedMeanAndStddev toMeanAndStddev() {
        if (sumCoefs != 0.0) {
            val mean = sumCoefValues / sumCoefs;
            val stddev = MeanAndStddev.stdDevFromSumCoefs(sumCoefs, sumCoefValues, sumCoefSquareValues);
            return new WeightedMeanAndStddev(sumCoefs, mean, stddev);
        } else {
            return new WeightedMeanAndStddev(0, 0, 0);
        }
    }

}
