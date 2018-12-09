package com.biorecorder.basechart.axis;

/**
 * Created by galafit on 25/12/17.
 */
public enum Unit {
    AXIS_UNIT(1), // domain units
    SECOND(1000),
    MINUTE(1000 * 60),
    HOUR(1000 * 60 * 60),
    DAY(1000 * 60 * 60 * 24);

    private int multiplier;

    Unit(int multiplier) {
        this.multiplier = multiplier;
    }

    int getMultiplier() {
        return multiplier;
    }
}
