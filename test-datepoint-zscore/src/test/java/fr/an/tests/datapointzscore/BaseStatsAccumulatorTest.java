package fr.an.tests.datapointzscore;

import lombok.val;
import org.junit.Assert;
import org.junit.Test;

public class BaseStatsAccumulatorTest {

    @Test
    public void test() {
        BaseStatsAccumulator acc = new BaseStatsAccumulator();
        acc.add(1.0);
        acc.add(2.0);
        acc.add(3.0);
        Assert.assertEquals(3, acc.getCount());
        Assert.assertEquals(6.0, acc.getSumValues(), 1e-8);
        Assert.assertEquals(14.0, acc.getSumSquares(), 1e-8);

        val meanAndStddev = acc.toMeanAndStddev();
        Assert.assertEquals(3, meanAndStddev.getCount());
        Assert.assertEquals(2.0, meanAndStddev.getAverage(), 1e-8);
        Assert.assertEquals(1.0, meanAndStddev.getStddev(), 1e-8);

        BaseStatsAccumulator acc2 = new BaseStatsAccumulator(meanAndStddev);
        Assert.assertEquals(acc.getCount(), acc2.getCount());
        Assert.assertEquals(acc.getSumValues(), acc2.getSumValues(), 1e-8);
        Assert.assertEquals(acc.getSumSquares(), acc2.getSumSquares(), 1e-8);
    }
}
