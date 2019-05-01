package com.biorecorder.data.frame;

/**
 * Created by galafit on 1/5/19.
 */
public class TimeInterval {
    private final TimeUnit timeUnit;
    private final int unitMultiplier;
    private final long length;

    public TimeInterval(TimeUnit timeUnit, int unitMultiplier) {
        this.timeUnit = timeUnit;
        this.unitMultiplier = unitMultiplier;
        this.length = timeUnit.getMilliseconds() * unitMultiplier;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public int getUnitMultiplier() {
        return unitMultiplier;
    }

    public long getLengthInMilliseconds() {
        return length;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof TimeInterval)) {
            return false;
        }

        TimeInterval timeInterval = (TimeInterval) obj;

        return timeInterval.timeUnit == timeUnit &&
                timeInterval.unitMultiplier == unitMultiplier;

    }
}
