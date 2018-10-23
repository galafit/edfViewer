package com.biorecorder.basechart.grouping;


public class DoubleGroupingMin extends DoubleGroupingFunction {
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
    protected double[] groupedValue1() {
        double[] groupedValues = {min};
        return groupedValues;
    }
}
