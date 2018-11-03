package com.biorecorder.basechart.grouping;

public class LongGroupOhlc extends LongGroupFunction {
    private long max;
    private long min;
    private long first;
    private long last;

    @Override
    protected void add1(long value) {
        if(count == 0) {
            max = value;
            min = value;
            first = value;
        } else {
            max = (long)Math.max(max, value);
            max = (long)Math.min(min, value);
        }
        last = value;
        count++;
    }

    @Override
    protected long[] groupedValue1() {
        long[] groupedValues = {first, max, min, last};
        return groupedValues;
    }
}
