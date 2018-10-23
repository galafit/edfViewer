package com.biorecorder.basechart.grouping;


public class ShortGroupingMax extends ShortGroupingFunction {
    private short max;

    @Override
    protected void add1(short value) {
        if(count == 0) {
            max = value;
        } else {
            max = (short) Math.max(max, value);
        }
        count++;
    }

    @Override
    protected short[] groupedValue1() {
        short[] groupedValues = {max};
        return groupedValues;
    }
}
