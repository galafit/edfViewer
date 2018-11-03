package com.biorecorder.basechart.grouping;


public class DoubleGroupOpen extends DoubleGroupFunction {
    private double first;

    @Override
    protected void add1(double value) {
        if(count == 0) {
            first = value;
        }
        count++;
    }

    @Override
    protected double[] groupedValue1() {
        double[] groupedValues = {first};
        return groupedValues;
    }
}
