package com.biorecorder.basechart.data.grouping;

/**
 * Created by galafit on 6/7/18.
 */
public class IntGroupingMinMax implements IntGroupingFunction{
    private int[] minMax = new int[0];
    private int count;

    @Override
    public void add(int value) {
        if(minMax.length == 0) {
            minMax = new int[2];
            minMax[0] = value;
            minMax[1] = value;
        } else {
            if(minMax[0] > value) {
                minMax[0] = value;
            }
            if(minMax[1] < value) {
                minMax[1] = value;
            }
        }
        count++;
    }

    @Override
    public int elementCount() {
        return count;
    }


    @Override
    public int[] getGrouped() {
        return minMax;
    }

    @Override
    public void reset() {
        minMax = new int[0];
        count = 0;
    }
}
