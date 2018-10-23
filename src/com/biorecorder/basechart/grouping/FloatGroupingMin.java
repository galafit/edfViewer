package com.biorecorder.basechart.grouping;

/**
 * Created by galafit on 23/10/18.
 */
public class FloatGroupingMin extends FloatGroupingFunction {
    private float min;

    @Override
    protected void add1(float value) {
        if(count == 0) {
            min = value;
        } else {
            min = Math.min(min, value);
        }
        count++;
    }

    @Override
    protected float[] groupedValue1() {
        float[] groupedValues = {min};
        return groupedValues;
    }

}
