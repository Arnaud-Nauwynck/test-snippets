package fr.an.tests.hdrhistogram;

import org.HdrHistogram.AbstractHistogram;
import org.HdrHistogram.Histogram;
import org.HdrHistogram.HistogramIterationValue;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;

public class HdrHistogramTest {

    @Test
    public void testHistogram_recordValueWithCount() {
        // lowestDiscernibleValue, highestTrackableValue,
        int numberOfSignificantValueDigits = 1;
        Histogram histo = new Histogram(numberOfSignificantValueDigits);
        histo.setAutoResize(true);

        int expectedCount = 0;
        long expectedSum = 0;
        histo.recordValueWithCount(10, 4); expectedCount+= 4; expectedSum += 4 * 10;
        histo.recordValueWithCount(11, 5); expectedCount+= 5; expectedSum += 5 * 11;
        histo.recordValueWithCount(12, 6); expectedCount+= 6; expectedSum += 6 * 12;
        histo.recordValueWithCount(15, 8); expectedCount+= 8; expectedSum += 8 * 15;
        histo.recordValueWithCount(18, 10); expectedCount+= 10; expectedSum += 10 * 18;
        histo.recordValueWithCount(20, 8); expectedCount+= 8; expectedSum += 8 * 20;
        histo.recordValueWithCount(30, 5); expectedCount+= 5; expectedSum += 5 * 30;
        histo.recordValueWithCount(50, 2); expectedCount+= 2; expectedSum += 2 * 50;
        histo.recordValueWithCount(60, 1); expectedCount+= 1; expectedSum += 1 * 60;
        histo.recordValueWithCount(80, 1); expectedCount+= 1; expectedSum += 1 * 80;
        histo.recordValueWithCount(120, 1); expectedCount+= 1; expectedSum += 1 * 120;

        System.out.println("histo:" + histo);
        // [10] : 4
        // [11] : 5
        // [12] : 6
        // [15] : 8
        // [18] : 10
        // [20] : 8
        // [30] : 5
        // [41] : 2
        // [46] : 1
        // [52] : 1
        // [62] : 1

        long totalCount = histo.getTotalCount();
        Assert.assertEquals(expectedCount, totalCount);

        long minValue = histo.getMinValue();
        Assert.assertEquals(10L, minValue);
        long maxValue = histo.getMaxValue();
        //??? Assert.assertEquals(120L, maxValue);
        Assert.assertTrue(120 <= maxValue); // 123 !!
        Assert.assertTrue(maxValue <= 125);

        double mean = histo.getMean();
        double expectedMean = expectedSum / totalCount; // 22.0 ... actual 22.4313...
        Assert.assertTrue(expectedMean-0.5 <= mean && mean <= expectedMean+0.5);

        Assert.assertEquals(0, histo.getCountAtValue(0));
        Assert.assertEquals(0, histo.getCountAtValue(1));
        Assert.assertEquals(0, histo.getCountAtValue(2));
        Assert.assertEquals(4, histo.getCountAtValue(10));
        Assert.assertEquals(5, histo.getCountAtValue(11));
        Assert.assertEquals(6, histo.getCountAtValue(12));
        Assert.assertEquals(0, histo.getCountAtValue(13));
        Assert.assertEquals(0, histo.getCountAtValue(14));
        Assert.assertEquals(8, histo.getCountAtValue(15));
        Assert.assertEquals(0, histo.getCountAtValue(16));
        Assert.assertEquals(0, histo.getCountAtValue(17));
        Assert.assertEquals(10, histo.getCountAtValue(18));
        Assert.assertEquals(0, histo.getCountAtValue(19));
        Assert.assertEquals(8, histo.getCountAtValue(20));
        Assert.assertEquals(0, histo.getCountAtValue(21));

        Assert.assertEquals(0, histo.getCountAtValue(29));
        Assert.assertEquals(5, histo.getCountAtValue(30));
        Assert.assertEquals(0, histo.getCountAtValue(31));

        for(int i = 32; i < 50; i++) {
            Assert.assertEquals(0, histo.getCountAtValue(i));
        }
        Assert.assertEquals(2, histo.getCountAtValue(50));
        Assert.assertEquals(2, histo.getCountAtValue(51));
        Assert.assertEquals(0, histo.getCountAtValue(52));

        System.out.println("-----------------");
        System.out.println("percentiles:");
        int percentileTicksPerHalfDistance = 10;
        for (HistogramIterationValue v : histo.percentiles(percentileTicksPerHalfDistance)) {
            System.out.println(v);
        }

        // =>
        // valueIteratedTo:10, prevValueIteratedTo:0, countAtValueIteratedTo:4, countAddedInThisIterationStep:4, totalCountToThisValue:4, totalValueToThisValue:40, percentile:7.8431372549019605, percentileLevelIteratedTo:0.0
        //valueIteratedTo:10, prevValueIteratedTo:10, countAtValueIteratedTo:4, countAddedInThisIterationStep:0, totalCountToThisValue:4, totalValueToThisValue:40, percentile:7.8431372549019605, percentileLevelIteratedTo:5.0
        //valueIteratedTo:11, prevValueIteratedTo:10, countAtValueIteratedTo:5, countAddedInThisIterationStep:5, totalCountToThisValue:9, totalValueToThisValue:95, percentile:17.647058823529413, percentileLevelIteratedTo:10.0
        //valueIteratedTo:11, prevValueIteratedTo:11, countAtValueIteratedTo:5, countAddedInThisIterationStep:0, totalCountToThisValue:9, totalValueToThisValue:95, percentile:17.647058823529413, percentileLevelIteratedTo:15.0
        //valueIteratedTo:12, prevValueIteratedTo:11, countAtValueIteratedTo:6, countAddedInThisIterationStep:6, totalCountToThisValue:15, totalValueToThisValue:167, percentile:29.41176470588235, percentileLevelIteratedTo:20.0
        //valueIteratedTo:12, prevValueIteratedTo:12, countAtValueIteratedTo:6, countAddedInThisIterationStep:0, totalCountToThisValue:15, totalValueToThisValue:167, percentile:29.41176470588235, percentileLevelIteratedTo:25.0
        //valueIteratedTo:15, prevValueIteratedTo:12, countAtValueIteratedTo:8, countAddedInThisIterationStep:8, totalCountToThisValue:23, totalValueToThisValue:287, percentile:45.09803921568628, percentileLevelIteratedTo:30.0
        //valueIteratedTo:15, prevValueIteratedTo:15, countAtValueIteratedTo:8, countAddedInThisIterationStep:0, totalCountToThisValue:23, totalValueToThisValue:287, percentile:45.09803921568628, percentileLevelIteratedTo:35.0
        //valueIteratedTo:15, prevValueIteratedTo:15, countAtValueIteratedTo:8, countAddedInThisIterationStep:0, totalCountToThisValue:23, totalValueToThisValue:287, percentile:45.09803921568628, percentileLevelIteratedTo:40.0
        //valueIteratedTo:15, prevValueIteratedTo:15, countAtValueIteratedTo:8, countAddedInThisIterationStep:0, totalCountToThisValue:23, totalValueToThisValue:287, percentile:45.09803921568628, percentileLevelIteratedTo:45.0
        //valueIteratedTo:18, prevValueIteratedTo:15, countAtValueIteratedTo:10, countAddedInThisIterationStep:10, totalCountToThisValue:33, totalValueToThisValue:467, percentile:64.70588235294117, percentileLevelIteratedTo:50.0
        //valueIteratedTo:18, prevValueIteratedTo:18, countAtValueIteratedTo:10, countAddedInThisIterationStep:0, totalCountToThisValue:33, totalValueToThisValue:467, percentile:64.70588235294117, percentileLevelIteratedTo:52.5
        //valueIteratedTo:18, prevValueIteratedTo:18, countAtValueIteratedTo:10, countAddedInThisIterationStep:0, totalCountToThisValue:33, totalValueToThisValue:467, percentile:64.70588235294117, percentileLevelIteratedTo:55.0
        //valueIteratedTo:18, prevValueIteratedTo:18, countAtValueIteratedTo:10, countAddedInThisIterationStep:0, totalCountToThisValue:33, totalValueToThisValue:467, percentile:64.70588235294117, percentileLevelIteratedTo:57.5
        //valueIteratedTo:18, prevValueIteratedTo:18, countAtValueIteratedTo:10, countAddedInThisIterationStep:0, totalCountToThisValue:33, totalValueToThisValue:467, percentile:64.70588235294117, percentileLevelIteratedTo:60.0
        //valueIteratedTo:18, prevValueIteratedTo:18, countAtValueIteratedTo:10, countAddedInThisIterationStep:0, totalCountToThisValue:33, totalValueToThisValue:467, percentile:64.70588235294117, percentileLevelIteratedTo:62.5
        //valueIteratedTo:20, prevValueIteratedTo:18, countAtValueIteratedTo:8, countAddedInThisIterationStep:8, totalCountToThisValue:41, totalValueToThisValue:627, percentile:80.3921568627451, percentileLevelIteratedTo:65.0
        //valueIteratedTo:20, prevValueIteratedTo:20, countAtValueIteratedTo:8, countAddedInThisIterationStep:0, totalCountToThisValue:41, totalValueToThisValue:627, percentile:80.3921568627451, percentileLevelIteratedTo:67.5
        //valueIteratedTo:20, prevValueIteratedTo:20, countAtValueIteratedTo:8, countAddedInThisIterationStep:0, totalCountToThisValue:41, totalValueToThisValue:627, percentile:80.3921568627451, percentileLevelIteratedTo:70.0
        //valueIteratedTo:20, prevValueIteratedTo:20, countAtValueIteratedTo:8, countAddedInThisIterationStep:0, totalCountToThisValue:41, totalValueToThisValue:627, percentile:80.3921568627451, percentileLevelIteratedTo:72.5
        //valueIteratedTo:20, prevValueIteratedTo:20, countAtValueIteratedTo:8, countAddedInThisIterationStep:0, totalCountToThisValue:41, totalValueToThisValue:627, percentile:80.3921568627451, percentileLevelIteratedTo:75.0
        //valueIteratedTo:20, prevValueIteratedTo:20, countAtValueIteratedTo:8, countAddedInThisIterationStep:0, totalCountToThisValue:41, totalValueToThisValue:627, percentile:80.3921568627451, percentileLevelIteratedTo:76.25
        //valueIteratedTo:20, prevValueIteratedTo:20, countAtValueIteratedTo:8, countAddedInThisIterationStep:0, totalCountToThisValue:41, totalValueToThisValue:627, percentile:80.3921568627451, percentileLevelIteratedTo:77.5
        //valueIteratedTo:20, prevValueIteratedTo:20, countAtValueIteratedTo:8, countAddedInThisIterationStep:0, totalCountToThisValue:41, totalValueToThisValue:627, percentile:80.3921568627451, percentileLevelIteratedTo:78.75
        //valueIteratedTo:20, prevValueIteratedTo:20, countAtValueIteratedTo:8, countAddedInThisIterationStep:0, totalCountToThisValue:41, totalValueToThisValue:627, percentile:80.3921568627451, percentileLevelIteratedTo:80.0
        //valueIteratedTo:30, prevValueIteratedTo:20, countAtValueIteratedTo:5, countAddedInThisIterationStep:5, totalCountToThisValue:46, totalValueToThisValue:777, percentile:90.19607843137256, percentileLevelIteratedTo:81.25
        //valueIteratedTo:30, prevValueIteratedTo:30, countAtValueIteratedTo:5, countAddedInThisIterationStep:0, totalCountToThisValue:46, totalValueToThisValue:777, percentile:90.19607843137256, percentileLevelIteratedTo:82.5
        //valueIteratedTo:30, prevValueIteratedTo:30, countAtValueIteratedTo:5, countAddedInThisIterationStep:0, totalCountToThisValue:46, totalValueToThisValue:777, percentile:90.19607843137256, percentileLevelIteratedTo:83.75
        //valueIteratedTo:30, prevValueIteratedTo:30, countAtValueIteratedTo:5, countAddedInThisIterationStep:0, totalCountToThisValue:46, totalValueToThisValue:777, percentile:90.19607843137256, percentileLevelIteratedTo:85.0
        //valueIteratedTo:30, prevValueIteratedTo:30, countAtValueIteratedTo:5, countAddedInThisIterationStep:0, totalCountToThisValue:46, totalValueToThisValue:777, percentile:90.19607843137256, percentileLevelIteratedTo:86.25
        //valueIteratedTo:30, prevValueIteratedTo:30, countAtValueIteratedTo:5, countAddedInThisIterationStep:0, totalCountToThisValue:46, totalValueToThisValue:777, percentile:90.19607843137256, percentileLevelIteratedTo:87.5
        //valueIteratedTo:30, prevValueIteratedTo:30, countAtValueIteratedTo:5, countAddedInThisIterationStep:0, totalCountToThisValue:46, totalValueToThisValue:777, percentile:90.19607843137256, percentileLevelIteratedTo:88.125
        //valueIteratedTo:30, prevValueIteratedTo:30, countAtValueIteratedTo:5, countAddedInThisIterationStep:0, totalCountToThisValue:46, totalValueToThisValue:777, percentile:90.19607843137256, percentileLevelIteratedTo:88.75
        //valueIteratedTo:30, prevValueIteratedTo:30, countAtValueIteratedTo:5, countAddedInThisIterationStep:0, totalCountToThisValue:46, totalValueToThisValue:777, percentile:90.19607843137256, percentileLevelIteratedTo:89.375
        //valueIteratedTo:30, prevValueIteratedTo:30, countAtValueIteratedTo:5, countAddedInThisIterationStep:0, totalCountToThisValue:46, totalValueToThisValue:777, percentile:90.19607843137256, percentileLevelIteratedTo:90.0
        //valueIteratedTo:51, prevValueIteratedTo:30, countAtValueIteratedTo:2, countAddedInThisIterationStep:2, totalCountToThisValue:48, totalValueToThisValue:879, percentile:94.11764705882354, percentileLevelIteratedTo:90.625
        //valueIteratedTo:51, prevValueIteratedTo:51, countAtValueIteratedTo:2, countAddedInThisIterationStep:0, totalCountToThisValue:48, totalValueToThisValue:879, percentile:94.11764705882354, percentileLevelIteratedTo:91.25
        //valueIteratedTo:51, prevValueIteratedTo:51, countAtValueIteratedTo:2, countAddedInThisIterationStep:0, totalCountToThisValue:48, totalValueToThisValue:879, percentile:94.11764705882354, percentileLevelIteratedTo:91.875
        //valueIteratedTo:51, prevValueIteratedTo:51, countAtValueIteratedTo:2, countAddedInThisIterationStep:0, totalCountToThisValue:48, totalValueToThisValue:879, percentile:94.11764705882354, percentileLevelIteratedTo:92.5
        //valueIteratedTo:51, prevValueIteratedTo:51, countAtValueIteratedTo:2, countAddedInThisIterationStep:0, totalCountToThisValue:48, totalValueToThisValue:879, percentile:94.11764705882354, percentileLevelIteratedTo:93.125
        //valueIteratedTo:51, prevValueIteratedTo:51, countAtValueIteratedTo:2, countAddedInThisIterationStep:0, totalCountToThisValue:48, totalValueToThisValue:879, percentile:94.11764705882354, percentileLevelIteratedTo:93.75
        //valueIteratedTo:51, prevValueIteratedTo:51, countAtValueIteratedTo:2, countAddedInThisIterationStep:0, totalCountToThisValue:48, totalValueToThisValue:879, percentile:94.11764705882354, percentileLevelIteratedTo:94.0625
        //valueIteratedTo:61, prevValueIteratedTo:51, countAtValueIteratedTo:1, countAddedInThisIterationStep:1, totalCountToThisValue:49, totalValueToThisValue:940, percentile:96.07843137254902, percentileLevelIteratedTo:94.375
        //valueIteratedTo:61, prevValueIteratedTo:61, countAtValueIteratedTo:1, countAddedInThisIterationStep:0, totalCountToThisValue:49, totalValueToThisValue:940, percentile:96.07843137254902, percentileLevelIteratedTo:94.6875
        //valueIteratedTo:61, prevValueIteratedTo:61, countAtValueIteratedTo:1, countAddedInThisIterationStep:0, totalCountToThisValue:49, totalValueToThisValue:940, percentile:96.07843137254902, percentileLevelIteratedTo:95.0
        //valueIteratedTo:61, prevValueIteratedTo:61, countAtValueIteratedTo:1, countAddedInThisIterationStep:0, totalCountToThisValue:49, totalValueToThisValue:940, percentile:96.07843137254902, percentileLevelIteratedTo:95.3125
        //valueIteratedTo:61, prevValueIteratedTo:61, countAtValueIteratedTo:1, countAddedInThisIterationStep:0, totalCountToThisValue:49, totalValueToThisValue:940, percentile:96.07843137254902, percentileLevelIteratedTo:95.625
        //valueIteratedTo:61, prevValueIteratedTo:61, countAtValueIteratedTo:1, countAddedInThisIterationStep:0, totalCountToThisValue:49, totalValueToThisValue:940, percentile:96.07843137254902, percentileLevelIteratedTo:95.9375
        //valueIteratedTo:83, prevValueIteratedTo:61, countAtValueIteratedTo:1, countAddedInThisIterationStep:1, totalCountToThisValue:50, totalValueToThisValue:1023, percentile:98.03921568627452, percentileLevelIteratedTo:96.25
        //valueIteratedTo:83, prevValueIteratedTo:83, countAtValueIteratedTo:1, countAddedInThisIterationStep:0, totalCountToThisValue:50, totalValueToThisValue:1023, percentile:98.03921568627452, percentileLevelIteratedTo:96.5625
        //valueIteratedTo:83, prevValueIteratedTo:83, countAtValueIteratedTo:1, countAddedInThisIterationStep:0, totalCountToThisValue:50, totalValueToThisValue:1023, percentile:98.03921568627452, percentileLevelIteratedTo:96.875
        //valueIteratedTo:83, prevValueIteratedTo:83, countAtValueIteratedTo:1, countAddedInThisIterationStep:0, totalCountToThisValue:50, totalValueToThisValue:1023, percentile:98.03921568627452, percentileLevelIteratedTo:97.03125
        //valueIteratedTo:83, prevValueIteratedTo:83, countAtValueIteratedTo:1, countAddedInThisIterationStep:0, totalCountToThisValue:50, totalValueToThisValue:1023, percentile:98.03921568627452, percentileLevelIteratedTo:97.1875
        //valueIteratedTo:83, prevValueIteratedTo:83, countAtValueIteratedTo:1, countAddedInThisIterationStep:0, totalCountToThisValue:50, totalValueToThisValue:1023, percentile:98.03921568627452, percentileLevelIteratedTo:97.34375
        //valueIteratedTo:83, prevValueIteratedTo:83, countAtValueIteratedTo:1, countAddedInThisIterationStep:0, totalCountToThisValue:50, totalValueToThisValue:1023, percentile:98.03921568627452, percentileLevelIteratedTo:97.5
        //valueIteratedTo:83, prevValueIteratedTo:83, countAtValueIteratedTo:1, countAddedInThisIterationStep:0, totalCountToThisValue:50, totalValueToThisValue:1023, percentile:98.03921568627452, percentileLevelIteratedTo:97.65625
        //valueIteratedTo:83, prevValueIteratedTo:83, countAtValueIteratedTo:1, countAddedInThisIterationStep:0, totalCountToThisValue:50, totalValueToThisValue:1023, percentile:98.03921568627452, percentileLevelIteratedTo:97.8125
        //valueIteratedTo:83, prevValueIteratedTo:83, countAtValueIteratedTo:1, countAddedInThisIterationStep:0, totalCountToThisValue:50, totalValueToThisValue:1023, percentile:98.03921568627452, percentileLevelIteratedTo:97.96875
        //valueIteratedTo:123, prevValueIteratedTo:83, countAtValueIteratedTo:1, countAddedInThisIterationStep:1, totalCountToThisValue:51, totalValueToThisValue:1146, percentile:100.0, percentileLevelIteratedTo:98.125
        //valueIteratedTo:123, prevValueIteratedTo:123, countAtValueIteratedTo:1, countAddedInThisIterationStep:0, totalCountToThisValue:51, totalValueToThisValue:1146, percentile:100.0, percentileLevelIteratedTo:100.0

        System.out.println("-----------------");
        System.out.println("linearBucketValues:");
        int valueUnitsPerBucket = 10;
        for (HistogramIterationValue v : histo.linearBucketValues(valueUnitsPerBucket)) {
            System.out.println(v);
        }
        // =>
        //valueIteratedTo:9, prevValueIteratedTo:0, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:0, totalValueToThisValue:0, percentile:0.0, percentileLevelIteratedTo:0.0
        //valueIteratedTo:19, prevValueIteratedTo:9, countAtValueIteratedTo:0, countAddedInThisIterationStep:33, totalCountToThisValue:33, totalValueToThisValue:467, percentile:64.70588235294117, percentileLevelIteratedTo:64.70588235294117
        //valueIteratedTo:29, prevValueIteratedTo:19, countAtValueIteratedTo:0, countAddedInThisIterationStep:8, totalCountToThisValue:41, totalValueToThisValue:627, percentile:80.3921568627451, percentileLevelIteratedTo:80.3921568627451
        //valueIteratedTo:39, prevValueIteratedTo:29, countAtValueIteratedTo:0, countAddedInThisIterationStep:5, totalCountToThisValue:46, totalValueToThisValue:777, percentile:90.19607843137256, percentileLevelIteratedTo:90.19607843137256
        //valueIteratedTo:49, prevValueIteratedTo:39, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:46, totalValueToThisValue:777, percentile:90.19607843137256, percentileLevelIteratedTo:90.19607843137256
        //valueIteratedTo:59, prevValueIteratedTo:49, countAtValueIteratedTo:0, countAddedInThisIterationStep:2, totalCountToThisValue:48, totalValueToThisValue:879, percentile:94.11764705882354, percentileLevelIteratedTo:94.11764705882354
        //valueIteratedTo:69, prevValueIteratedTo:59, countAtValueIteratedTo:0, countAddedInThisIterationStep:1, totalCountToThisValue:49, totalValueToThisValue:940, percentile:96.07843137254902, percentileLevelIteratedTo:96.07843137254902
        //valueIteratedTo:79, prevValueIteratedTo:69, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:49, totalValueToThisValue:940, percentile:96.07843137254902, percentileLevelIteratedTo:96.07843137254902
        //valueIteratedTo:89, prevValueIteratedTo:79, countAtValueIteratedTo:0, countAddedInThisIterationStep:1, totalCountToThisValue:50, totalValueToThisValue:1023, percentile:98.03921568627452, percentileLevelIteratedTo:98.03921568627452
        //valueIteratedTo:99, prevValueIteratedTo:89, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:50, totalValueToThisValue:1023, percentile:98.03921568627452, percentileLevelIteratedTo:98.03921568627452
        //valueIteratedTo:109, prevValueIteratedTo:99, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:50, totalValueToThisValue:1023, percentile:98.03921568627452, percentileLevelIteratedTo:98.03921568627452
        //valueIteratedTo:119, prevValueIteratedTo:109, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:50, totalValueToThisValue:1023, percentile:98.03921568627452, percentileLevelIteratedTo:98.03921568627452
        //valueIteratedTo:129, prevValueIteratedTo:119, countAtValueIteratedTo:0, countAddedInThisIterationStep:1, totalCountToThisValue:51, totalValueToThisValue:1146, percentile:100.0, percentileLevelIteratedTo:100.0

        System.out.println();
        System.out.println("-----------------");
        System.out.println("allValues:");
        for (HistogramIterationValue v : histo.allValues()) {
            System.out.println(v);
        }
        // =>
        //valueIteratedTo:0, prevValueIteratedTo:0, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:0, totalValueToThisValue:0, percentile:0.0, percentileLevelIteratedTo:0.0
        //valueIteratedTo:1, prevValueIteratedTo:0, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:0, totalValueToThisValue:0, percentile:0.0, percentileLevelIteratedTo:0.0
        //valueIteratedTo:2, prevValueIteratedTo:1, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:0, totalValueToThisValue:0, percentile:0.0, percentileLevelIteratedTo:0.0
        //valueIteratedTo:3, prevValueIteratedTo:2, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:0, totalValueToThisValue:0, percentile:0.0, percentileLevelIteratedTo:0.0
        //valueIteratedTo:4, prevValueIteratedTo:3, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:0, totalValueToThisValue:0, percentile:0.0, percentileLevelIteratedTo:0.0
        //valueIteratedTo:5, prevValueIteratedTo:4, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:0, totalValueToThisValue:0, percentile:0.0, percentileLevelIteratedTo:0.0
        //valueIteratedTo:6, prevValueIteratedTo:5, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:0, totalValueToThisValue:0, percentile:0.0, percentileLevelIteratedTo:0.0
        //valueIteratedTo:7, prevValueIteratedTo:6, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:0, totalValueToThisValue:0, percentile:0.0, percentileLevelIteratedTo:0.0
        //valueIteratedTo:8, prevValueIteratedTo:7, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:0, totalValueToThisValue:0, percentile:0.0, percentileLevelIteratedTo:0.0
        //valueIteratedTo:9, prevValueIteratedTo:8, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:0, totalValueToThisValue:0, percentile:0.0, percentileLevelIteratedTo:0.0
        //valueIteratedTo:10, prevValueIteratedTo:9, countAtValueIteratedTo:4, countAddedInThisIterationStep:4, totalCountToThisValue:4, totalValueToThisValue:40, percentile:7.8431372549019605, percentileLevelIteratedTo:7.8431372549019605
        //valueIteratedTo:11, prevValueIteratedTo:10, countAtValueIteratedTo:5, countAddedInThisIterationStep:5, totalCountToThisValue:9, totalValueToThisValue:95, percentile:17.647058823529413, percentileLevelIteratedTo:17.647058823529413
        //valueIteratedTo:12, prevValueIteratedTo:11, countAtValueIteratedTo:6, countAddedInThisIterationStep:6, totalCountToThisValue:15, totalValueToThisValue:167, percentile:29.41176470588235, percentileLevelIteratedTo:29.41176470588235
        //valueIteratedTo:13, prevValueIteratedTo:12, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:15, totalValueToThisValue:167, percentile:29.41176470588235, percentileLevelIteratedTo:29.41176470588235
        //valueIteratedTo:14, prevValueIteratedTo:13, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:15, totalValueToThisValue:167, percentile:29.41176470588235, percentileLevelIteratedTo:29.41176470588235
        //valueIteratedTo:15, prevValueIteratedTo:14, countAtValueIteratedTo:8, countAddedInThisIterationStep:8, totalCountToThisValue:23, totalValueToThisValue:287, percentile:45.09803921568628, percentileLevelIteratedTo:45.09803921568628
        //valueIteratedTo:16, prevValueIteratedTo:15, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:23, totalValueToThisValue:287, percentile:45.09803921568628, percentileLevelIteratedTo:45.09803921568628
        //valueIteratedTo:17, prevValueIteratedTo:16, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:23, totalValueToThisValue:287, percentile:45.09803921568628, percentileLevelIteratedTo:45.09803921568628
        //valueIteratedTo:18, prevValueIteratedTo:17, countAtValueIteratedTo:10, countAddedInThisIterationStep:10, totalCountToThisValue:33, totalValueToThisValue:467, percentile:64.70588235294117, percentileLevelIteratedTo:64.70588235294117
        //valueIteratedTo:19, prevValueIteratedTo:18, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:33, totalValueToThisValue:467, percentile:64.70588235294117, percentileLevelIteratedTo:64.70588235294117
        //valueIteratedTo:20, prevValueIteratedTo:19, countAtValueIteratedTo:8, countAddedInThisIterationStep:8, totalCountToThisValue:41, totalValueToThisValue:627, percentile:80.3921568627451, percentileLevelIteratedTo:80.3921568627451
        //valueIteratedTo:21, prevValueIteratedTo:20, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:41, totalValueToThisValue:627, percentile:80.3921568627451, percentileLevelIteratedTo:80.3921568627451
        //valueIteratedTo:22, prevValueIteratedTo:21, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:41, totalValueToThisValue:627, percentile:80.3921568627451, percentileLevelIteratedTo:80.3921568627451
        //valueIteratedTo:23, prevValueIteratedTo:22, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:41, totalValueToThisValue:627, percentile:80.3921568627451, percentileLevelIteratedTo:80.3921568627451
        //valueIteratedTo:24, prevValueIteratedTo:23, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:41, totalValueToThisValue:627, percentile:80.3921568627451, percentileLevelIteratedTo:80.3921568627451
        //valueIteratedTo:25, prevValueIteratedTo:24, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:41, totalValueToThisValue:627, percentile:80.3921568627451, percentileLevelIteratedTo:80.3921568627451
        //valueIteratedTo:26, prevValueIteratedTo:25, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:41, totalValueToThisValue:627, percentile:80.3921568627451, percentileLevelIteratedTo:80.3921568627451
        //valueIteratedTo:27, prevValueIteratedTo:26, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:41, totalValueToThisValue:627, percentile:80.3921568627451, percentileLevelIteratedTo:80.3921568627451
        //valueIteratedTo:28, prevValueIteratedTo:27, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:41, totalValueToThisValue:627, percentile:80.3921568627451, percentileLevelIteratedTo:80.3921568627451
        //valueIteratedTo:29, prevValueIteratedTo:28, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:41, totalValueToThisValue:627, percentile:80.3921568627451, percentileLevelIteratedTo:80.3921568627451
        //valueIteratedTo:30, prevValueIteratedTo:29, countAtValueIteratedTo:5, countAddedInThisIterationStep:5, totalCountToThisValue:46, totalValueToThisValue:777, percentile:90.19607843137256, percentileLevelIteratedTo:90.19607843137256
        //valueIteratedTo:31, prevValueIteratedTo:30, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:46, totalValueToThisValue:777, percentile:90.19607843137256, percentileLevelIteratedTo:90.19607843137256
        //valueIteratedTo:33, prevValueIteratedTo:31, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:46, totalValueToThisValue:777, percentile:90.19607843137256, percentileLevelIteratedTo:90.19607843137256
        //valueIteratedTo:35, prevValueIteratedTo:33, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:46, totalValueToThisValue:777, percentile:90.19607843137256, percentileLevelIteratedTo:90.19607843137256
        //valueIteratedTo:37, prevValueIteratedTo:35, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:46, totalValueToThisValue:777, percentile:90.19607843137256, percentileLevelIteratedTo:90.19607843137256
        //valueIteratedTo:39, prevValueIteratedTo:37, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:46, totalValueToThisValue:777, percentile:90.19607843137256, percentileLevelIteratedTo:90.19607843137256
        //valueIteratedTo:41, prevValueIteratedTo:39, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:46, totalValueToThisValue:777, percentile:90.19607843137256, percentileLevelIteratedTo:90.19607843137256
        //valueIteratedTo:43, prevValueIteratedTo:41, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:46, totalValueToThisValue:777, percentile:90.19607843137256, percentileLevelIteratedTo:90.19607843137256
        //valueIteratedTo:45, prevValueIteratedTo:43, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:46, totalValueToThisValue:777, percentile:90.19607843137256, percentileLevelIteratedTo:90.19607843137256
        //valueIteratedTo:47, prevValueIteratedTo:45, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:46, totalValueToThisValue:777, percentile:90.19607843137256, percentileLevelIteratedTo:90.19607843137256
        //valueIteratedTo:49, prevValueIteratedTo:47, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:46, totalValueToThisValue:777, percentile:90.19607843137256, percentileLevelIteratedTo:90.19607843137256
        //valueIteratedTo:51, prevValueIteratedTo:49, countAtValueIteratedTo:2, countAddedInThisIterationStep:2, totalCountToThisValue:48, totalValueToThisValue:879, percentile:94.11764705882354, percentileLevelIteratedTo:94.11764705882354
        //valueIteratedTo:53, prevValueIteratedTo:51, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:48, totalValueToThisValue:879, percentile:94.11764705882354, percentileLevelIteratedTo:94.11764705882354
        //valueIteratedTo:55, prevValueIteratedTo:53, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:48, totalValueToThisValue:879, percentile:94.11764705882354, percentileLevelIteratedTo:94.11764705882354
        //valueIteratedTo:57, prevValueIteratedTo:55, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:48, totalValueToThisValue:879, percentile:94.11764705882354, percentileLevelIteratedTo:94.11764705882354
        //valueIteratedTo:59, prevValueIteratedTo:57, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:48, totalValueToThisValue:879, percentile:94.11764705882354, percentileLevelIteratedTo:94.11764705882354
        //valueIteratedTo:61, prevValueIteratedTo:59, countAtValueIteratedTo:1, countAddedInThisIterationStep:1, totalCountToThisValue:49, totalValueToThisValue:940, percentile:96.07843137254902, percentileLevelIteratedTo:96.07843137254902
        //valueIteratedTo:63, prevValueIteratedTo:61, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:49, totalValueToThisValue:940, percentile:96.07843137254902, percentileLevelIteratedTo:96.07843137254902
        //valueIteratedTo:67, prevValueIteratedTo:63, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:49, totalValueToThisValue:940, percentile:96.07843137254902, percentileLevelIteratedTo:96.07843137254902
        //valueIteratedTo:71, prevValueIteratedTo:67, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:49, totalValueToThisValue:940, percentile:96.07843137254902, percentileLevelIteratedTo:96.07843137254902
        //valueIteratedTo:75, prevValueIteratedTo:71, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:49, totalValueToThisValue:940, percentile:96.07843137254902, percentileLevelIteratedTo:96.07843137254902
        //valueIteratedTo:79, prevValueIteratedTo:75, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:49, totalValueToThisValue:940, percentile:96.07843137254902, percentileLevelIteratedTo:96.07843137254902
        //valueIteratedTo:83, prevValueIteratedTo:79, countAtValueIteratedTo:1, countAddedInThisIterationStep:1, totalCountToThisValue:50, totalValueToThisValue:1023, percentile:98.03921568627452, percentileLevelIteratedTo:98.03921568627452
        //valueIteratedTo:87, prevValueIteratedTo:83, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:50, totalValueToThisValue:1023, percentile:98.03921568627452, percentileLevelIteratedTo:98.03921568627452
        //valueIteratedTo:91, prevValueIteratedTo:87, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:50, totalValueToThisValue:1023, percentile:98.03921568627452, percentileLevelIteratedTo:98.03921568627452
        //valueIteratedTo:95, prevValueIteratedTo:91, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:50, totalValueToThisValue:1023, percentile:98.03921568627452, percentileLevelIteratedTo:98.03921568627452
        //valueIteratedTo:99, prevValueIteratedTo:95, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:50, totalValueToThisValue:1023, percentile:98.03921568627452, percentileLevelIteratedTo:98.03921568627452
        //valueIteratedTo:103, prevValueIteratedTo:99, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:50, totalValueToThisValue:1023, percentile:98.03921568627452, percentileLevelIteratedTo:98.03921568627452
        //valueIteratedTo:107, prevValueIteratedTo:103, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:50, totalValueToThisValue:1023, percentile:98.03921568627452, percentileLevelIteratedTo:98.03921568627452
        //valueIteratedTo:111, prevValueIteratedTo:107, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:50, totalValueToThisValue:1023, percentile:98.03921568627452, percentileLevelIteratedTo:98.03921568627452
        //valueIteratedTo:115, prevValueIteratedTo:111, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:50, totalValueToThisValue:1023, percentile:98.03921568627452, percentileLevelIteratedTo:98.03921568627452
        //valueIteratedTo:119, prevValueIteratedTo:115, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:50, totalValueToThisValue:1023, percentile:98.03921568627452, percentileLevelIteratedTo:98.03921568627452
        //valueIteratedTo:123, prevValueIteratedTo:119, countAtValueIteratedTo:1, countAddedInThisIterationStep:1, totalCountToThisValue:51, totalValueToThisValue:1146, percentile:100.0, percentileLevelIteratedTo:100.0
        //valueIteratedTo:127, prevValueIteratedTo:123, countAtValueIteratedTo:0, countAddedInThisIterationStep:0, totalCountToThisValue:51, totalValueToThisValue:1146, percentile:100.0, percentileLevelIteratedTo:100.0


        System.out.println();
        System.out.println("--------------------");
        System.out.println("percent below values");
        double percentBelow10 = histo.getPercentileAtOrBelowValue(10);
        double percentBelow11 = histo.getPercentileAtOrBelowValue(11);
        double percentBelow12 = histo.getPercentileAtOrBelowValue(12);
        double percentBelow13 = histo.getPercentileAtOrBelowValue(13);
        double percentBelow14 = histo.getPercentileAtOrBelowValue(14);
        double percentBelow15 = histo.getPercentileAtOrBelowValue(15);
        double percentBelow16 = histo.getPercentileAtOrBelowValue(16);
        double percentBelow17 = histo.getPercentileAtOrBelowValue(17);
        double percentBelow18 = histo.getPercentileAtOrBelowValue(18);
        double percentBelow19 = histo.getPercentileAtOrBelowValue(19);
        double percentBelow20 = histo.getPercentileAtOrBelowValue(20);
        double percentBelow25 = histo.getPercentileAtOrBelowValue(25);
        double percentBelow30 = histo.getPercentileAtOrBelowValue(30);
        double percentBelow40 = histo.getPercentileAtOrBelowValue(40);
        double percentBelow50 = histo.getPercentileAtOrBelowValue(50);
        double percentBelow60 = histo.getPercentileAtOrBelowValue(60);
        double percentBelow100 = histo.getPercentileAtOrBelowValue(100);
        double percentBelow120 = histo.getPercentileAtOrBelowValue(120);
        double percentBelow130 = histo.getPercentileAtOrBelowValue(130);
        System.out.println("value  : 10  11   12   13   14   15   16   17   18   19   20   25   30   40   50   60   100  120  130");
        System.out.println("percent: " //
                + f1(percentBelow10) + " " //
                + f1(percentBelow11) + " " //
                + f1(percentBelow12) + " " //
                + f1(percentBelow13) + " " //
                + f1(percentBelow14) + " " //
                + f1(percentBelow15) + " " //
                + f1(percentBelow16) + " " //
                + f1(percentBelow17) + " " //
                + f1(percentBelow18) + " " //
                + f1(percentBelow19) + " " //
                + f1(percentBelow20) + " " //
                + f1(percentBelow25) + " " //
                + f1(percentBelow30) + " " //
                + f1(percentBelow40) + " " //
                + f1(percentBelow50) + " " //
                + f1(percentBelow60) + " " //
                + f1(percentBelow100) + " " //
                + f1(percentBelow120) + " " //
                + f1(percentBelow130) + " " //
        );
        // =>
        // value  : 10  11   12   13   14   15   16   17   18   19   20   25   30   40   50   60   100  120  130
        // percent: 7,8 17,6 29,4 29,4 29,4 45,1 45,1 45,1 64,7 64,7 80,4 80,4 90,2 90,2 94,1 96,1 98,0 100,0 100,0


        System.out.println();
        System.out.println("------------");
        System.out.println("valueAtPercentile");
        double valueAtPercent3 = histo.getValueAtPercentile(3);
        double valueAtPercent5 = histo.getValueAtPercentile(5);
        double valueAtPercent10 = histo.getValueAtPercentile(10);
        double valueAtPercent20 = histo.getValueAtPercentile(20);
        double valueAtPercent25 = histo.getValueAtPercentile(25);
        double valueAtPercent50 = histo.getValueAtPercentile(50);
        double valueAtPercent75 = histo.getValueAtPercentile(75);
        double valueAtPercent80 = histo.getValueAtPercentile(80);
        double valueAtPercent85 = histo.getValueAtPercentile(85);
        double valueAtPercent90 = histo.getValueAtPercentile(90);
        double valueAtPercent95 = histo.getValueAtPercentile(95);
        double valueAtPercent97 = histo.getValueAtPercentile(97);
        System.out.println("percent: 3    5    10   20   25   50   75   80   85   90   95   97");
        System.out.println("value  : "
                        + f1(valueAtPercent3) + " " //
                        + f1(valueAtPercent5) + " " //
                        + f1(valueAtPercent10) + " " //
                        + f1(valueAtPercent20) + " " //
                        + f1(valueAtPercent25) + " " //
                        + f1(valueAtPercent50) + " " //
                        + f1(valueAtPercent75) + " " //
                        + f1(valueAtPercent80) + " " //
                        + f1(valueAtPercent85) + " " //
                        + f1(valueAtPercent90) + " " //
                        + f1(valueAtPercent95) + " " //
                        + f1(valueAtPercent97) + " " //
                        );


        System.out.println();
        System.out.println("------------");

        int requiredBytes = histo.getNeededByteBufferCapacity();
        ByteBuffer encodeBuffer = ByteBuffer.allocate(requiredBytes);
        int bytesCount = histo.encodeIntoByteBuffer(encodeBuffer);
        System.out.println("encodeIntoByteBuffer => " + bytesCount);
        // => encodeIntoByteBuffer => 60

        encodeBuffer.rewind();

        long minBarForHighestTrackableValue = 0; // ???
        Histogram reloadedHisto = Histogram.decodeFromByteBuffer(encodeBuffer,
                minBarForHighestTrackableValue);
        Assert.assertEquals(histo, reloadedHisto);
        Assert.assertEquals(totalCount, reloadedHisto.getTotalCount());
        Assert.assertEquals(percentBelow25, reloadedHisto.getPercentileAtOrBelowValue(25), 1e-9);
        Assert.assertEquals(percentBelow30, reloadedHisto.getPercentileAtOrBelowValue(30), 1e-9);

        System.out.println();
        System.out.println("----------");
        ByteBuffer encodeCompressedBuffer = ByteBuffer.allocate(requiredBytes);
        int compressedBytesCount = histo.encodeIntoCompressedByteBuffer(encodeCompressedBuffer);
        System.out.println("encodeIntoCompressedByteBuffer => " + compressedBytesCount);
        // => compressedBytesCount => 54

        encodeCompressedBuffer.rewind();
        Histogram reloadedCompressedHisto;
        try {
            reloadedCompressedHisto = Histogram.decodeFromCompressedByteBuffer(encodeCompressedBuffer,
                    minBarForHighestTrackableValue);
        } catch (DataFormatException e) {
            throw new RuntimeException(e);
        }
        Assert.assertEquals(histo, reloadedCompressedHisto);
        Assert.assertEquals(totalCount, reloadedCompressedHisto.getTotalCount());
        Assert.assertEquals(percentBelow25, reloadedCompressedHisto.getPercentileAtOrBelowValue(25), 1e-9);
        Assert.assertEquals(percentBelow30, reloadedCompressedHisto.getPercentileAtOrBelowValue(30), 1e-9);


    }

    protected static String f1(double d) {
        return String.format("%2.1f", d);
    }
}
