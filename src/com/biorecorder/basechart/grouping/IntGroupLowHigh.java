package com.biorecorder.basechart.grouping;

public class IntGroupLowHigh extends IntGroupFunction {
    private int max;
    private int min;

    @Override
    protected void add1(int value) {
        if(count == 0) {
            max = value;
            min = value;
        } else {
            max = (int)Math.max(max, value);
            max = (int)Math.min(min, value);
        }
        count++;
    }

    @Override
    protected int[] groupedValue1() {
        int[] groupedValues = {min, max};
        return groupedValues;
    }
}
