package fr.an.tests.datapointzscore;

import lombok.val;
import org.junit.Assert;
import org.junit.Test;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Random;
import java.util.function.Function;

public class RecentDatePointTrackerTest {

    @Test
    public void testSeriesIncreasingIncomplete() {
        doTestSeriesIncreasing("testSeriesIncreasingIncomplete" , 11, 1, 1);
    }
    @Test
    public void testSeriesDecreasingIncomplete() {
        doTestSeriesIncreasing("testSeriesDecreasingIncomplete" , 11, 1, -1);
    }

    @Test
    public void testSeriesIncreasing50() {
        doTestSeriesIncreasing("testSeriesIncreasing50", 50, 1, 1);
    }
    @Test
    public void testSeriesDecreasing50() {
        doTestSeriesIncreasing("testSeriesDecreasing50", 50, 1, -1);
    }

    @Test
    public void testSeriesIncreasing50_by7days() {
        doTestSeriesIncreasing("testSeriesIncreasing50_by7days", 50, 7, 1);
    }

    public void doTestSeriesIncreasing(String testName, int count, int incrDay, int incrValue) {
        doTestSeries(testName, count, incrDay, i -> 1000.0 + i*incrValue);
    }

    @Test
    public void testSeriesRand() {
        Random rnd = new Random(0);
        doTestSeries("testSeriesRand", 50, 1, i -> 1000 + 100*rnd.nextGaussian());
    }

    @Test
    public void testDuplicateDate() {
        val tracker = new RecentDatePointTracker(12);
        val startDate = LocalDate.of(2023, 1, 1);
        tracker.addPoint(startDate, 1);
        tracker.addPoint(startDate, 2);
        Assert.assertEquals(1, tracker.getDatePoints().size());

        val d1 = startDate.plusDays(1);
        tracker.addPoint(d1, 3);
        val d2 = startDate.plusDays(2);
        tracker.addPoint(d2, 4);

        // re-add (overwrite) d1
        tracker.addPoint(d1, 5);
        Assert.assertEquals(3, tracker.getDatePoints().size());
        Assert.assertEquals(5.0, tracker.getDatePoints().get(1).getValue(), 1e-6);

        LocalDate currDate = d2;
        for(int i = 0; i < 20; i++, currDate=currDate.plusDays(1)) {
            tracker.addPoint(currDate, i);
        }
        // re-add insert at begining => clear all
        tracker.addPoint(d1, 3);
        Assert.assertEquals(1, tracker.getDatePoints().size());

        val middleDate = currDate.plusDays(6*2+1);
        for(int i = 0; i < 12; i++, currDate=currDate.plusDays(2)) {
            tracker.addPoint(currDate, i);
        }
        tracker.addPoint(middleDate, 100);
        // re-add in the middle => clear remaining
        Assert.assertEquals(7, tracker.getDatePoints().size());
        Assert.assertEquals(middleDate, tracker.getDatePoints().get(tracker.getDatePoints().size()-1).getDate());
    }


    protected void doTestSeries(String testName, int count, int incrDay, Function<Integer,Double> valueFunc) {
        val tracker = new RecentDatePointTracker(12);
        val startDate = LocalDate.of(2023, 1, 1);
        LocalDate currDate = startDate;
        for(int i = 0; i < count; i++, currDate=currDate.plusDays(incrDay)) {
            double currValue = valueFunc.apply(i);
            tracker.addPoint(currDate, currValue);
        }

        System.out.println(testName);
        val datePoints = tracker.getDatePoints();
        int datePointCount = datePoints.size();
        for(int i = 0; i < datePointCount; i++) {
            val datePoint = datePoints.get(i);
            System.out.println("[" + i + "] " + datePoint.getDate() + " " + fmt2Digits(datePoint.getValue())
                    + " value-zscore:" + fmt2Digits(datePoint.getValueZScore())
                    + ((datePoint.getValueCategory() != null)? " " + datePoint.getValueCategory() : "")
                    + " delay-zscore:" +  fmt2Digits(datePoint.getDateDelayZScore())
                    + ((datePoint.getDateDelayCategory() != null)? " " + datePoint.getDateDelayCategory() : "")
            );
        }
        System.out.println();
    }

    private static final DecimalFormat decimalFormat_2digits = new DecimalFormat("#.##");
    private static String fmt2Digits(double value) {
        return decimalFormat_2digits.format(value);
    }

}
