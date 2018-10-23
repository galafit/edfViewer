package com.biorecorder.basechart.grouping;

public class FloatGroupingMinMax extends FloatGroupingFunction {
    private float max;
    private float min;

    @Override
    protected void add1(float value) {
        if(count == 0) {
            max = value;
            min = value;
        } else {
            max = (float)Math.max(max, value);
            max = (float)Math.min(min, value);
        }
        count++;
    }

    @Override
    protected float[] groupedValue1() {
        float[] groupedValues = {min, max};
        return groupedValues;
    }
}
