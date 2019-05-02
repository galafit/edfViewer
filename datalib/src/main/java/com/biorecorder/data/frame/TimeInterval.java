package com.biorecorder.data.frame;

/**
 * Created by galafit on 1/5/19.
 */
public class TimeInterval implements Interval {
    private final TimeUnit timeUnit;
    private final int unitMultiplier;
    private final long length;

    private final long start;
    private final long nextIntervalStart;

    public TimeInterval(TimeUnit timeUnit, int unitMultiplier, long start, long nextIntervalStart) {
        this.timeUnit = timeUnit;
        this.unitMultiplier = unitMultiplier;
        this.start = start;
        this.nextIntervalStart = nextIntervalStart;
        length = nextIntervalStart - start;
    }


    public TimeInterval(TimeUnit timeUnit, int unitMultiplier) {
        this(timeUnit, unitMultiplier, 0, timeUnit.getMilliseconds() * unitMultiplier);
    }



    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public int getUnitMultiplier() {
        return unitMultiplier;
    }

    @Override
    public double length() {
        return length;
    }

    /*
             * As we will use methods contains only on INCREASING data
             * we do only one check (value < nextIntervalStart) instead of both
             */
    @Override
    public boolean contains(byte value) {
        // return value >= start && value < nextIntervalStart;
        return value < nextIntervalStart;
    }

    @Override
    public boolean contains(short value) {
        return value < nextIntervalStart;
    }

    @Override
    public boolean contains(int value) {
        return value < nextIntervalStart;
    }

    @Override
    public boolean contains(long value) {
        return value < nextIntervalStart;
    }

    @Override
    public boolean contains(float value) {
        return value < nextIntervalStart;
    }

    @Override
    public boolean contains(double value) {
        return value < nextIntervalStart;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof TimeInterval)) {
            return false;
        }

        TimeInterval timeInterval = (TimeInterval) obj;

        return timeInterval.timeUnit == timeUnit &&
                timeInterval.unitMultiplier == unitMultiplier &&
                timeInterval.start == start &&
                timeInterval.nextIntervalStart == nextIntervalStart;

    }
}
