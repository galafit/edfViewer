package com.biorecorder.basechart.grouping;

public class DoubleGroupOhlc extends DoubleGroupFunction {
    private double max;
    private double min;
    private double first;
    private double last;

    @Override
    protected void add1(double value) {
        if(count == 0) {
            max = value;
            min = value;
            first = value;
        } else {
            max = (double)Math.max(max, value);
            max = (double)Math.min(min, value);
        }
        last = value;
        count++;
    }

    @Override
    protected double[] groupedValue1() {
        double[] groupedValues = {first, max, min, last};
        return groupedValues;
    }
}
