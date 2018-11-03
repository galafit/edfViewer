package com.biorecorder.basechart.grouping;


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
    protected float[] groupedValue1() {
        float[] groupedValues = {max};
        return groupedValues;
    }
}
