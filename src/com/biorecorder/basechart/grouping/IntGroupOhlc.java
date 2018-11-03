package com.biorecorder.basechart.grouping;

public class IntGroupOhlc extends IntGroupFunction {
    private int max;
    private int min;
    private int first;
    private int last;

    @Override
    protected void add1(int value) {
        if(count == 0) {
            max = value;
            min = value;
            first = value;
        } else {
            max = (int)Math.max(max, value);
            max = (int)Math.min(min, value);
        }
        last = value;
        count++;
    }

    @Override
    protected int[] groupedValue1() {
        int[] groupedValues = {first, max, min, last};
        return groupedValues;
    }
}
