package com.biorecorder.basechart.grouping;


public class LongGroupingMin extends LongGroupingFunction {
    private long min;

    @Override
    protected void add1(long value) {
        if(count == 0) {
            min = value;
        } else {
            min = (long)Math.min(min, value);
        }
        count++;
    }

    @Override
    protected long[] groupedValue1() {
        long[] groupedValues = {min};
        return groupedValues;
    }
}
