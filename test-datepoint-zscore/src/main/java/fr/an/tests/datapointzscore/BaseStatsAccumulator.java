package fr.an.tests.datapointzscore;

import lombok.Getter;
import lombok.val;

@Getter
public class BaseStatsAccumulator {
    protected int count;
    protected double sumValues;
    protected double sumSquares;

    //---------------------------------------------------------------------------------------------

    public BaseStatsAccumulator() {
    }

    public BaseStatsAccumulator(MeanAndStddev src) {
        val n = this.count = src.count;
        val sum = this.sumValues = src.average * n;
        // stddev = Math.sqrt(variance);
        double variance = src.stddev * src.stddev;
        // double variance = (sumOfSquares - (sum * sum / n)) / (n - 1);
        this.sumSquares = (n > 1)? variance * (n-1) + sum * sum / n : 0;
    }


    //---------------------------------------------------------------------------------------------

    public void add(double value) {
        this.count++;
        this.sumValues += value;
        this.sumSquares += value * value;
    }

    public MeanAndStddev toMeanAndStddev() {
        if (count > 1) {
            val mean = sumValues / count;
            val stddev = MeanAndStddev.stdDevFromSumCoefs(count, sumValues, sumSquares);
            return new MeanAndStddev(count, mean, stddev);
        } else {
            return new MeanAndStddev(count, 0, 0);
        }
    }

}
