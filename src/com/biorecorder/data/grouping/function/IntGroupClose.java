package com.biorecorder.data.grouping.function;


public class IntGroupClose extends IntGroupFunction {
    private int last;

    @Override
    protected void add1(int value) {
        last = value;
    }

    @Override
    protected int groupValue1() {
        return last;
    }
}
