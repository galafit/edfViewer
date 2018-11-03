package com.biorecorder.basechart.grouping;


public class ShortGroupOpen extends ShortGroupFunction {
    private short first;

    @Override
    protected void add1(short value) {
        if(count == 0) {
            first = value;
        }
        count++;
    }

    @Override
    protected short[] groupedValue1() {
        short[] groupedValues = {first};
        return groupedValues;
    }
}
