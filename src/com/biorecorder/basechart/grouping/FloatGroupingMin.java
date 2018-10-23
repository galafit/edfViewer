package com.biorecorder.basechart.grouping;


public class FloatGroupingMin extends FloatGroupingFunction {
    private float min;

    @Override
    protected void add1(float value) {
        if(count == 0) {
            min = value;
        } else {
            min = (float)Math.min(min, value);
        }
        count++;
    }

    @Override
    protected float[] groupedValue1() {
        float[] groupedValues = {min};
        return groupedValues;
    }
}
