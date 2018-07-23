package com.biorecorder.basechart.data.grouping;

/**
 * Created by galafit on 6/7/18.
 */
public class IntGroupingMax implements IntGroupingFunction {
    private int[] max = new int[0];
    private int count;

    @Override
    public void add(int value) {
        if(max.length == 0) {
            max = new int[1];
            max[0] = value;
        } else if(max[0] < value) {
            max[0] = value;
        }
        count++;
    }

    @Override
    public int elementCount() {
        return count;
    }

    @Override
    public int[] getGrouped() {
        return max;
    }

    @Override
    public void reset() {
        max = new int[0];
        count = 0;
    }
}
