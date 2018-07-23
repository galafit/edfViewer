package com.biorecorder.basechart.data.grouping;

/**
 * Created by galafit on 4/7/18.
 */
public interface IntGroupingFunction {
    void add(int value);
    int elementCount();
    int[] getGrouped();
    void reset();
}
