package com.biorecorder.data.frame;

import java.util.Arrays;
import java.util.List;

/**
 * Created by galafit on 2/5/19.
 */
public enum TimeInterval {
    MILLISECOND_1(TimeUnit.MILLISECOND, 1),
    MILLISECOND_2(TimeUnit.MILLISECOND, 2),
    MILLISECOND_5(TimeUnit.MILLISECOND, 5),
    MILLISECOND_10(TimeUnit.MILLISECOND, 10),
    MILLISECOND_20(TimeUnit.MILLISECOND, 20),
    MILLISECOND_50(TimeUnit.MILLISECOND, 50),
    MILLISECOND_100(TimeUnit.MILLISECOND, 100),
    MILLISECOND_200(TimeUnit.MILLISECOND, 200),
    MILLISECOND_500(TimeUnit.MILLISECOND, 500),
    SECOND_1(TimeUnit.SECOND, 1),
    SECOND_2(TimeUnit.SECOND, 2),
    SECOND_5(TimeUnit.SECOND, 5),
    SECOND_10(TimeUnit.SECOND, 10),
    SECOND_30(TimeUnit.SECOND, 30),
    MINUTE_1(TimeUnit.MINUTE, 1),
    MINUTE_2(TimeUnit.MINUTE, 2),
    MINUTE_5(TimeUnit.MINUTE, 5),
    MINUTE_10(TimeUnit.MINUTE, 10),
    MINUTE_30(TimeUnit.MINUTE, 30),
    HOUR_1(TimeUnit.HOUR, 1),
    HOUR_2(TimeUnit.HOUR, 2),
    HOUR_6(TimeUnit.HOUR, 6),
    HOUR_12(TimeUnit.HOUR, 12),
    DAY(TimeUnit.DAY, 1),
    WEEK(TimeUnit.WEEK, 1),
    MONTH_1(TimeUnit.MONTH, 1),
    MONTH_3(TimeUnit.MONTH, 3),
    MONTH_6(TimeUnit.MONTH, 6),
    YEAR(TimeUnit.YEAR, 1);

    TimeUnit timeUnit;
    int timeUnitMultiplier;

    TimeInterval(TimeUnit timeUnit, int timeUnitMultiplier) {
        this.timeUnit = timeUnit;
        this.timeUnitMultiplier = timeUnitMultiplier;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public int getTimeUnitMultiplier() {
        return timeUnitMultiplier;
    }

    public long toMilliseconds() {
        return timeUnitMultiplier * timeUnit.toMilliseconds();
    }

    public static TimeInterval getClosest(long interval, boolean isWeekIncluded) {
        List<TimeInterval> timeIntervals = Arrays.asList(values());
        if(!isWeekIncluded) {
            timeIntervals.remove(WEEK);
        }
        long diffPrev = 0;
        for (int i = 0; i < timeIntervals.size(); i++) {
            TimeInterval timeInterval_i = timeIntervals.get(i);
            long diff = timeInterval_i.toMilliseconds() - interval;
            if(diff >= 0) {
                if(i > 0 && diffPrev < diff) {
                    return timeIntervals.get(i - 1);

                }
                return timeInterval_i;
            }
            diffPrev = Math.abs(diff);
        }
        return YEAR;
    }
}
