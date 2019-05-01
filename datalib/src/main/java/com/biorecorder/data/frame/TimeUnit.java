package com.biorecorder.data.frame;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by galafit on 28/4/19.
 */
public enum TimeUnit {
    MILLISECOND(1,    1, 2, 5, 10, 20, 50, 100, 200, 500), // dividers of 1000
    SECOND(1000,    1, 2, 5, 10, 30), // dividers of 60
    MINUTE(1000 * 60,   1, 2, 5, 10, 30), // dividers of 60
    HOUR(1000 * 60 * 60,   1, 2, 6, 12), // dividers of 24
    DAY(1000 * 60 * 60 * 24,   1),
    WEEK(1000 * 60 * 60 * 24 * 7,    1),
    MONTH(1000 * 60 * 60 * 24 * 7 * 30,   1, 3, 6), // dividers of 12
    YEAR(1000 * 60 * 60 * 24 * 7 * 30 * 365);

    int[] allowedMultiples;
    long milliseconds;

    TimeUnit(int milliseconds, int... allowedMultiples) {
        this.allowedMultiples = allowedMultiples;
        this.milliseconds = milliseconds;
    }

    public int[] getAllowedMultiples() {
        return allowedMultiples;
    }

    public long getMilliseconds() {
        return milliseconds;
    }

    public static List<TimeInterval> getClosestIntervals(boolean isWeekIncluded, double... intervals) {
        List<TimeInterval> allowedIntervals = createAllowedIntervals(isWeekIncluded);
        List<TimeInterval> resultantIntervals = new ArrayList<>(intervals.length);
        for (double interval : intervals) {
            TimeInterval timeInterval = getClosestInterval(allowedIntervals, (long) interval);
            if(resultantIntervals.size() == 0 || !resultantIntervals.get(resultantIntervals.size() - 1).equals(timeInterval)) {
                resultantIntervals.add(timeInterval);
            }
        }
        return resultantIntervals;
    }


    private static TimeInterval getClosestInterval(List<TimeInterval> allowedIntervals,  long interval) {
        long diffPrev = 0;
        for (int i = 0; i < allowedIntervals.size(); i++) {
            TimeInterval allowedInterval = allowedIntervals.get(i);
            long diff = allowedInterval.getLengthInMilliseconds() - interval;
            if(diff >= 0) {
                if(i > 0 && diffPrev < diff) {
                    TimeInterval allowedIntervalPrev = allowedIntervals.get(i - 1);
                    return new TimeInterval(allowedIntervalPrev.getTimeUnit(), allowedIntervalPrev.getUnitMultiplier());
                }
                return new TimeInterval(allowedInterval.getTimeUnit(), allowedInterval.getUnitMultiplier());
            }
            diffPrev = Math.abs(diff);
        }

        return new TimeInterval(YEAR,  Math.round(interval / YEAR.milliseconds));
    }

    private static List<TimeInterval> createAllowedIntervals(boolean isWeekIncluded) {
        ArrayList<TimeInterval> allowedIntervals = new ArrayList<>();
        for (TimeUnit unit : values()) {
            if (isWeekIncluded || unit != WEEK) {
                int[] multiples = unit.getAllowedMultiples();
                for (int multiple : multiples) {
                    allowedIntervals.add(new TimeInterval(unit, multiple));
                }
            }
        }
        return allowedIntervals;
    }
}
