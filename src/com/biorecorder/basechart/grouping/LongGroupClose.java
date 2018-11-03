package com.biorecorder.basechart.grouping;


public class LongGroupClose extends LongGroupFunction {
    private long last;

    @Override
    protected void add1(long value) {
        last = value;
    }

    @Override
    protected long[] groupedValue1() {
        long[] groupedValues = {last};
        return groupedValues;
    }
}
