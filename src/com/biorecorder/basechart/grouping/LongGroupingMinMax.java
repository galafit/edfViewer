package com.biorecorder.basechart.grouping;

public class LongGroupingMinMax extends LongGroupingFunction {
    private long max;
    private long min;

    @Override
    protected void add1(long value) {
        if(count == 0) {
            max = value;
            min = value;
        } else {
            max = (long)Math.max(max, value);
            max = (long)Math.min(min, value);
        }
        count++;
    }

    @Override
    protected long[] groupedValue1() {
        long[] groupedValues = {min, max};
        return groupedValues;
    }
}
