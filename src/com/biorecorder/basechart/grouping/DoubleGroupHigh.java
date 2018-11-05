package com.biorecorder.basechart.grouping;


public class DoubleGroupHigh extends DoubleGroupFunction {
    private double max;

    @Override
    protected void add1(double value) {
        if(count == 0) {
            max = value;
        } else {
            max = (double) Math.max(max, value);
        }
        count++;
    }

    @Override
    protected double groupValue1() {
        return max;
    }
}
