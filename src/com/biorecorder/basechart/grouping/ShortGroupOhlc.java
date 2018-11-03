package com.biorecorder.basechart.grouping;

public class ShortGroupOhlc extends ShortGroupFunction {
    private short max;
    private short min;
    private short first;
    private short last;

    @Override
    protected void add1(short value) {
        if(count == 0) {
            max = value;
            min = value;
            first = value;
        } else {
            max = (short)Math.max(max, value);
            max = (short)Math.min(min, value);
        }
        last = value;
        count++;
    }

    @Override
    protected short[] groupedValue1() {
        short[] groupedValues = {first, max, min, last};
        return groupedValues;
    }
}
