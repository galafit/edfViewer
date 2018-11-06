package com.biorecorder.basechart.data;

/**
 * Created by galafit on 3/11/18.
 */
public enum IntervalUnit {
    NUMBER(1),
    MILLISECOND(1),
    SECOND(1000),
    MINUTE(1000 * 60),
    HOUR(1000 * 60 * 60),
    DAY(1000 * 60 * 60 * 24),
    WEEK(1000 * 60 * 60 * 24 * 7);
    // at the moment not used
    // MONTH(1000 * 60 * 60 * 24 * 30),
    // YEAR(1000 * 60 * 60 * 24 * 365);

    private int unitFactor;

    IntervalUnit(int unitFactor) {
        this.unitFactor = unitFactor;
    }

    public int getUnitFactor() {
        return unitFactor;
    }
}
