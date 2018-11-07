package com.biorecorder.data.grouping.function;


public class ShortGroupLow extends ShortGroupFunction {
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
    protected short groupValue1() {
        return min;
    }
}
