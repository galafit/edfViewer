package com.biorecorder.basechart.grouping;


public class FloatGroupAverage extends FloatGroupFunction {
    private float sum;

    @Override
    public void reset() {
        super.reset();
        sum = 0;
    }

    @Override
    protected void add1(float value) {
        super.add1(value);
        sum += value;
    }

    @Override
    protected float groupValue1() {
        return (float)(sum / count);
    }
}
