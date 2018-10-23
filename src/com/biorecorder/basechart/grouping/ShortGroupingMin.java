package com.biorecorder.basechart.grouping;


public class ShortGroupingMin extends ShortGroupingFunction {
    private short min;

    @Override
    protected void add1(short value) {
        if(count == 0) {
            min = value;
        } else {
            min = (short)Math.min(min, value);
        }
        count++;
    }

    @Override
    protected short[] groupedValue1() {
        short[] groupedValues = {min};
        return groupedValues;
    }
}
