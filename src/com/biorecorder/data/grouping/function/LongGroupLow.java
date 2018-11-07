package com.biorecorder.data.grouping.function;


public class LongGroupLow extends LongGroupFunction {
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
    protected long groupValue1() {
        return min;
    }
}
