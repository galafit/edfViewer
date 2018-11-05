package com.biorecorder.basechart.grouping;


public class ShortGroupClose extends ShortGroupFunction {
    private short last;

    @Override
    protected void add1(short value) {
        last = value;
    }

    @Override
    protected short groupValue1() {
        return last;
    }
}
