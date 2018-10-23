package com.biorecorder.basechart.grouping;

public class ShortGroupingMinMax extends ShortGroupingFunction {
    private short max;
    private short min;

    @Override
    protected void add1(short value) {
        if(count == 0) {
            max = value;
            min = value;
        } else {
            max = (short)Math.max(max, value);
            max = (short)Math.min(min, value);
        }
        count++;
    }

    @Override
    protected short[] groupedValue1() {
        short[] groupedValues = {min, max};
        return groupedValues;
    }
}
