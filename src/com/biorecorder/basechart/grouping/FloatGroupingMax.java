package com.biorecorder.basechart.grouping;

/**
 * Created by galafit on 22/10/18.
 */
public class FloatGroupingMax extends  FloatGroupingFunction {
    private float max;

    @Override
    protected void add1(float value) {
        if(count == 0) {
            max = value;
        } else {
            max = Math.max(max, value);
        }
        count++;
    }

    @Override
    protected float[] groupedValue1() {
        float[] groupedValues = {max};
        return groupedValues;
    }
}
