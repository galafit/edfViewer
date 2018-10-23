package com.biorecorder.basechart.grouping;


public class IntGroupingMax extends IntGroupingFunction {
    private int max;

    @Override
    protected void add1(int value) {
        if(count == 0) {
            max = value;
        } else {
            max = (int) Math.max(max, value);
        }
        count++;
    }

    @Override
    protected int[] groupedValue1() {
        int[] groupedValues = {max};
        return groupedValues;
    }
}
