package com.biorecorder.basechart.grouping;


public class LongGroupingMax extends LongGroupingFunction {
    private long max;

    @Override
    protected void add1(long value) {
        if(count == 0) {
            max = value;
        } else {
            max = (long) Math.max(max, value);
        }
        count++;
    }

    @Override
    protected long[] groupedValue1() {
        long[] groupedValues = {max};
        return groupedValues;
    }
}
