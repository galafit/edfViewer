package com.biorecorder.basechart.grouping;


public class IntGroupLow extends IntGroupFunction {
    private int min;

    @Override
    protected void add1(int value) {
        if(count == 0) {
            min = value;
        } else {
            min = (int)Math.min(min, value);
        }
        count++;
    }

    @Override
    protected int[] groupedValue1() {
        int[] groupedValues = {min};
        return groupedValues;
    }
}
