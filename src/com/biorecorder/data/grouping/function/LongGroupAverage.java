package com.biorecorder.data.grouping.function;


public class LongGroupAverage extends LongGroupFunction {
    private long sum;

    @Override
    public void reset() {
        super.reset();
        sum = 0;
    }

    @Override
    protected void add1(long value) {
        super.add1(value);
        sum += value;
    }

    @Override
    protected long groupValue1() {
        return (long)(sum / count);
    }
}
