package com.biorecorder.basechart.grouping;


public class DoubleGroupClose extends DoubleGroupFunction {
    private double last;

    @Override
    protected void add1(double value) {
        last = value;
    }

    @Override
    protected double groupValue1() {
        return last;
    }
}
