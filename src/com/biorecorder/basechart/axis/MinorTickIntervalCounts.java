package com.biorecorder.basechart.axis;


/**
 * Created by galafit on 3/9/18.
 */
public enum MinorTickIntervalCounts {
    AUTO(0),
    NONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TENS(10);

    int intervalCount;

    MinorTickIntervalCounts(int intervalCount) {
        this.intervalCount = intervalCount;
    }

    public int getIntervalCount() {
        return intervalCount;
    }
}
