package com.biorecorder.data.grouping.function;


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
    protected short groupValue1() {
        return first;
    }
}
