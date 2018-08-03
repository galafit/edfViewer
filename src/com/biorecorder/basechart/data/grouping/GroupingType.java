package com.biorecorder.basechart.data.grouping;

/**
 * Created by galafit on 25/11/17.
 */
public enum GroupingType {
    AVG(1),
    MIN(1),
    MAX(1),
    FIRST(1),
    MIN_MAX(2);

    private int dimension;

    GroupingType(int dimension) {
        this.dimension = dimension;
    }

    public int getDimension() {
        return dimension;
    }
}
