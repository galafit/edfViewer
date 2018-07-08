package com.biorecorder.basechart.data.grouping;

/**
 * Created by galafit on 6/7/18.
 */
public class IntGroupingAvg implements IntGroupingFunction {
    private long sum;
    protected int count;

    @Override
    public void add(int value) {
        sum += value;
        count++;
    }

    @Override
    public int[] getGrouped() {
        int[] avg = new int[1];
        avg[0] = (int) (sum / count);
        return avg;
    }

    @Override
    public void reset() {
        sum = 0;
        count = 0;
    }
}
