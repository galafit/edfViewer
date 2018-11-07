package com.biorecorder.data.grouping.function;


public class ShortGroupAverage extends ShortGroupFunction {
    private short sum;

    @Override
    public void reset() {
        super.reset();
        sum = 0;
    }

    @Override
    protected void add1(short value) {
        super.add1(value);
        sum += value;
    }

    @Override
    protected short groupValue1() {
        return (short)(sum / count);
    }
}
