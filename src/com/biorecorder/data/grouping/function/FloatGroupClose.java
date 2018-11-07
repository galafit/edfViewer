package com.biorecorder.data.grouping.function;


public class FloatGroupClose extends FloatGroupFunction {
    private float last;

    @Override
    protected void add1(float value) {
        last = value;
    }

    @Override
    protected float groupValue1() {
        return last;
    }
}
