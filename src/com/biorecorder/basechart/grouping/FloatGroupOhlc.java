package com.biorecorder.basechart.grouping;

public class FloatGroupOhlc extends FloatGroupFunction {
    private float max;
    private float min;
    private float first;
    private float last;

    @Override
    protected void add1(float value) {
        if(count == 0) {
            max = value;
            min = value;
            first = value;
        } else {
            max = (float)Math.max(max, value);
            max = (float)Math.min(min, value);
        }
        last = value;
        count++;
    }

    @Override
    protected float[] groupedValue1() {
        float[] groupedValues = {first, max, min, last};
        return groupedValues;
    }
}
