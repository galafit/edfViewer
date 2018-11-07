package com.biorecorder.data.grouping.function;


public class LongGroupHigh extends LongGroupFunction {
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
    protected long groupValue1() {
        return max;
    }
}
