package com.biorecorder.data.grouping.function;


public class DoubleGroupLow extends DoubleGroupFunction {
    private double min;

    @Override
    protected void add1(double value) {
        if(count == 0) {
            min = value;
        } else {
            min = (double)Math.min(min, value);
        }
        count++;
    }

    @Override
    protected double groupValue1() {
        return min;
    }
}
