package com.biorecorder.basechart.grouping;


public class FloatGroupOpen extends FloatGroupFunction {
    private float first;

    @Override
    protected void add1(float value) {
        if(count == 0) {
            first = value;
        }
        count++;
    }

    @Override
    protected float[] groupedValue1() {
        float[] groupedValues = {first};
        return groupedValues;
    }
}
