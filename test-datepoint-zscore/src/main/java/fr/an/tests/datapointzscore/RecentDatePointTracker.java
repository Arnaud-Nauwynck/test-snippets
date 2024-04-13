package fr.an.tests.datapointzscore;

import lombok.Getter;
import lombok.val;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class RecentDatePointTracker {

    private static final String CATEGORY_OK = "ok";
    private static final String CATEGORY_date_too_recent = "date-too-recent";
    private static final String CATEGORY_date_too_old = "date-too-old";
    private static final String CATEGORY_value_too_low = "value-too-low";
    private static final String CATEGORY_value_too_high = "value-too-high";


    @Getter
    private final List<DatePoint> datePoints;
    private final int maxDataPoints;

    private final double amortizedCoef;


    // mean and standard deviation for last N * datePoints value and date delay
    @Getter
    private MeanAndStddev dateDelayStats;
    @Getter
    private MeanAndStddev valueStats;

    private final EMAStatsAccumulator dateDelayEMAStatsAccumulator;
    private final EMAStatsAccumulator valueEMAStatsAccumulator;

    @Getter
    public static class DatePoint {
        private final LocalDate date;
        private double value;

        private double valueZScore;
        private double dateDelayZScore;
        private String valueCategory; // ok, value-too-high, value-too-low, ..
        private String dateDelayCategory; // ok, date-too-recent, date-too-old

        public DatePoint(LocalDate date, double value) {
            this.date = date;
            this.value = value;
        }

    }


    //---------------------------------------------------------------------------------------------

    public RecentDatePointTracker(int maxDataPoints) {
        this(maxDataPoints, 0.3);
    }

    public RecentDatePointTracker(int maxDataPoints, double amortizedCoef) {
        this.maxDataPoints = maxDataPoints;
        this.datePoints = new ArrayList<>(maxDataPoints);
        this.amortizedCoef = amortizedCoef;
        this.valueEMAStatsAccumulator = new EMAStatsAccumulator();
        this.dateDelayEMAStatsAccumulator = new EMAStatsAccumulator();
    }

    //---------------------------------------------------------------------------------------------

    public void addPoint(LocalDate date, double value) {
        DatePoint newPoint = new DatePoint(date, value);
        int newPointPos;
        // dataPoints.add(newPoint);
        if (datePoints.isEmpty()) {
            datePoints.add(newPoint);
            newPointPos = 0;
        } else {
            // Check if the new data point should be inserted at the end
            int datePointCountBefore = datePoints.size();
            DatePoint dataPointBefore = datePoints.get(datePointCountBefore - 1);
            if (newPoint.getDate().isAfter(dataPointBefore.getDate())) {
                datePoints.add(newPoint);
                newPointPos = datePointCountBefore;
            } else {
                // Use binary search to find the correct insertion position
                int foundPos = Collections.binarySearch(datePoints, newPoint, Comparator.comparing(DatePoint::getDate));
                if (foundPos >= 0) {
                    // overwrite existing point
                    datePoints.set(foundPos, newPoint);
                    newPointPos = foundPos;
                } else {
                    // insert point
                    foundPos = -(foundPos + 1);
                    datePoints.add(foundPos, newPoint);
                    newPointPos = foundPos;
                }
                dataPointBefore = (newPointPos > 0)? datePoints.get(newPointPos-1) : null;
            }
            // Keep the list size within the maximum limit
            if (datePoints.size() >= maxDataPoints) {
                if (newPointPos == datePoints.size()-1) {
                    datePoints.remove(0); // Remove the oldest data point
                } else {
                    // replaying dates? => remove all dates after inserted, assuming they will be re-added in order
                    for(int i = datePoints.size() - 1; i > newPointPos; i--) {
                        datePoints.remove(i);
                    }
                }
            }

            val prevValueStats = this.valueStats;
            val prevDateDelayStatsOnAdd = this.dateDelayStats;

            // Calculate mean and standard deviation for value and date delay
            this.valueStats = calculateValueStatistics();
            this.dateDelayStats = calculateDateDelayStatistics();

            valueEMAStatsAccumulator.add(amortizedCoef, value);

            if (datePoints.size() > 2 && prevValueStats != null) {
                // Calculate ZScores
                double valueZScore = prevValueStats.zscoreFor(newPoint.getValue());
                newPoint.valueZScore = valueZScore;
                double dateZScore = 0.0;
                if (dataPointBefore != null) {
                    int dateDelay = (int) ChronoUnit.DAYS.between(dataPointBefore.getDate(), newPoint.getDate());
                    dateZScore = prevDateDelayStatsOnAdd.zscoreFor(dateDelay);

                    dateDelayEMAStatsAccumulator.add(amortizedCoef, dateDelay);
                }
                newPoint.dateDelayZScore = dateZScore;

                // Classify as normal or abnormal
                classify(newPoint, newPointPos);

//                System.out.println("Date: " + date + ", Value: " + value + " Z-Score: " + valueZScore
//                        + " delay Z-Score: " + dateZScore
//                        + " => classification: " + classification
//                );
            }
        }
    }

    public WeightedMeanAndStddev getValueEMAMeanAndStddev() {
        return valueEMAStatsAccumulator.toMeanAndStddev();
    }

    public WeightedMeanAndStddev getDateDelayEMAMeanAndStddev() {
        return dateDelayEMAStatsAccumulator.toMeanAndStddev();
    }


    private void classify(DatePoint newPoint, int newPointPos) {
        val valueZScore = newPoint.valueZScore;
        if (-3.0 <= valueZScore && valueZScore <= +3.0) {
            // ok
            newPoint.valueCategory = null;
        } else if (valueZScore < -3.0) {
            newPoint.valueCategory = CATEGORY_value_too_low;
        } else if (valueZScore > 3.0) {
            newPoint.valueCategory = CATEGORY_value_too_high;
        }

        val delayZScore = newPoint.dateDelayZScore;
        if (-3.0 <= delayZScore && delayZScore <= +3.0) {
            // ok
            newPoint.dateDelayCategory = null;
        } else if (delayZScore < -3.0) {
            newPoint.dateDelayCategory = CATEGORY_date_too_old;
        } else if (delayZScore > 3.0) {
            newPoint.dateDelayCategory = CATEGORY_date_too_recent;
        }

    }


    // Calculate mean and standard deviation for values
    private MeanAndStddev calculateValueStatistics() {
        int count = 0;
        double sum = 0.0;
        double sumSquares = 0.0;
        for (DatePoint point : datePoints) {
            count++;
            double value = point.getValue();
            sum += value;
            sumSquares += value * value;
        }
        return MeanAndStddev.fromSum(count, sum, sumSquares);
    }

    // idem calculateValueStatistics()
    private MeanAndStddev calculateValueStatistics2() {
        val acc = new BaseStatsAccumulator();
        for (DatePoint point : datePoints) {
            acc.add(point.getValue());
        }
        return acc.toMeanAndStddev();
    }

    // Calculate mean and standard deviation for date delays
    private MeanAndStddev calculateDateDelayStatistics() {
        int count = 0;
        long sum = 0;
        long sumSquares = 0;
        final int dataPointCount = datePoints.size();;
        for (int i = 1; i < dataPointCount; i++) {
            count++;
            int dateDelay = (int) ChronoUnit.DAYS.between(datePoints.get(i - 1).getDate(), datePoints.get(i).getDate());
            sum += dateDelay;
            sumSquares += dateDelay * dateDelay;
        }
        return MeanAndStddev.fromSum(count, sum, sumSquares);
    }

}
