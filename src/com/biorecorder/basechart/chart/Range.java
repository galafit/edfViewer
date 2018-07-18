package com.biorecorder.basechart.chart;

import java.text.MessageFormat;

/**
 * Created by galafit on 11/7/17.
 */
public class Range {
    private double min;
    private double max;

    public Range(double min, double max) {
        if (min > max){
            String errorMessage = "Range Error. Expected Min <= Max. Min = {0}, Max = {1}.";
            String formattedError = MessageFormat.format(errorMessage, min, max);
            throw new IllegalArgumentException(formattedError);
        }
        this.min = min;
        this.max = max;
    }

    public boolean contains(double value) {
        if(value >= min && value <= max) {
            return true;
        }
        return false;
    }

    public  double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double length() {
        return max - min;
    }

    public static Range max(Range range1, Range range2) {
        if(range1 == null && range2 == null) {
            return null;
        }
        if(range1 != null && range2 == null) {
           return range1;
        }
        if(range2 != null && range1 == null) {
            return range2;
        }
        return new Range(Math.min(range1.min, range2.min), Math.max(range1.max, range2.max));
    }

    @Override
    public String toString() {
        return "Range{" +
                "min=" + min +
                ", max=" + max +
                '}';
    }
}