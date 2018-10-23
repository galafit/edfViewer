package com.biorecorder.basechart.grouping;

/**
 * Created by galafit on 23/10/18.
 */
public class FloatGroupingMinMax extends FloatGroupingFunction {
    private float max;
    private float min;

    @Override
    protected void add1(float value) {
        if(count == 0) {
            max = value;
            min = value;
        } else {
            max = Math.max(max, value);
            max = Math.min(min, value);
        }
        count++;
    }

    @Override
    protected float[] groupedValue1() {
        float[] groupedValues = {min, max};
        return groupedValues;
    }
}
