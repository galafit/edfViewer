package com.biorecorder.basechart.grouping;


public class FloatGroupClose extends FloatGroupFunction {
    private float last;

    @Override
    protected void add1(float value) {
        last = value;
    }

    @Override
    protected float[] groupedValue1() {
        float[] groupedValues = {last};
        return groupedValues;
    }
}
