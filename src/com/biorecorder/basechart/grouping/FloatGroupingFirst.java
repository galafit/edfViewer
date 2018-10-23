package com.biorecorder.basechart.grouping;

/**
 * Created by galafit on 23/10/18.
 */
public class FloatGroupingFirst extends FloatGroupingFunction {
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
