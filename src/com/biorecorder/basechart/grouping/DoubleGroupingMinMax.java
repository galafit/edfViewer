package com.biorecorder.basechart.grouping;

public class DoubleGroupingMinMax extends DoubleGroupingFunction {
    private double max;
    private double min;

    @Override
    protected void add1(double value) {
        if(count == 0) {
            max = value;
            min = value;
        } else {
            max = (double)Math.max(max, value);
            max = (double)Math.min(min, value);
        }
        count++;
    }

    @Override
    protected double[] groupedValue1() {
        double[] groupedValues = {min, max};
        return groupedValues;
    }
}
