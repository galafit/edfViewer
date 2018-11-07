package com.biorecorder.data.transformation;

/**
 * Created by galafit on 5/11/18.
 */
public class GroupInterval {
    private final double interval;
    private final IntervalUnit unit; // The unit of measurement for the interval

    public GroupInterval(double interval, IntervalUnit unit) {
        this.interval = interval;
        this.unit = unit;
    }

    public GroupInterval(double interval) {
        this(interval, IntervalUnit.NUMBER);
    }

    public double getIntervalInUnits() {
        return interval;
    }

    public double getIntervalAsNumber() {
        return interval * unit.getUnitFactor();
    }

    public IntervalUnit getUnit() {
        return unit;
    }
}
