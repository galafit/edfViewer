package com.biorecorder.basechart.grouping;


public class LongGroupingFirst extends LongGroupingFunction {
    private long first;

    @Override
    protected void add1(long value) {
        if(count == 0) {
            first = value;
        }
        count++;
    }

    @Override
    protected long[] groupedValue1() {
        long[] groupedValues = {first};
        return groupedValues;
    }
}
