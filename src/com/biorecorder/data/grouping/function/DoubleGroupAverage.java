package com.biorecorder.data.grouping.function;


public class DoubleGroupAverage extends DoubleGroupFunction {
    private double sum;

    @Override
    public void reset() {
        super.reset();
        sum = 0;
    }

    @Override
    protected void add1(double value) {
        super.add1(value);
        sum += value;
    }

    @Override
    protected double groupValue1() {
        return (double)(sum / count);
    }
}
