package com.biorecorder.basechart.grouping;


public class IntGroupClose extends IntGroupFunction {
    private int last;

    @Override
    protected void add1(int value) {
        last = value;
    }

    @Override
    protected int[] groupedValue1() {
        int[] groupedValues = {last};
        return groupedValues;
    }
}
