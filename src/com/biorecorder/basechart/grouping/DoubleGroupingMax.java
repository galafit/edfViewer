package com.biorecorder.basechart.grouping;


public class DoubleGroupingMax extends DoubleGroupingFunction {
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
    protected double[] groupedValue1() {
        double[] groupedValues = {max};
        return groupedValues;
    }
}
