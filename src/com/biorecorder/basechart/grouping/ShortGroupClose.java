package com.biorecorder.basechart.grouping;


public class ShortGroupClose extends ShortGroupFunction {
    private short last;

    @Override
    protected void add1(short value) {
        last = value;
    }

    @Override
    protected short[] groupedValue1() {
        short[] groupedValues = {last};
        return groupedValues;
    }
}
