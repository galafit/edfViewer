package com.biorecorder.data.grouping.function;


public class FloatGroupHigh extends FloatGroupFunction {
    private float max;

    @Override
    protected void add1(float value) {
        if(count == 0) {
            max = value;
        } else {
            max = (float) Math.max(max, value);
        }
        count++;
    }

    @Override
    protected float groupValue1() {
        return max;
    }
}
