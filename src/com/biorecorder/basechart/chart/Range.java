package com.biorecorder.basechart.chart;

import java.text.MessageFormat;

/**
 * Created by galafit on 11/7/17.
 */
public class Range {
    private Double min;
    private Double max;

    public Range(Double min, Double max) {
        this.min = min;
        this.max = max;
        if (max != null && min != null & min > max){
            String errorMessage = "Error during creating Range. Expected Start <= End. Start = {0}, End = {1}.";
            String formattedError = MessageFormat.format(errorMessage, min, max);
            throw new IllegalArgumentException(formattedError);
        }
    }

    public boolean contains(double value) {
        if(max == null || min == null) {
            return false;
        }
        if(value >= min && value <= max) {
            return true;
        }
        return false;
    }

    public  Double getMin() {
        return min;
    }

    public Double getMax() {
        return max;
    }

    public double length() {
        if(max != null && min != null) {
            return max - min;
        }
        return 0;
    }

    public static Range max(Range range1, Range range2) {
        if(range1 == null && range2 == null) {
            return null;
        }
        Double min = null;
        Double max = null;
        if(range1 != null && range1.min != null) {
            min = range1.min;
        }
        if(range2 != null && range2.min != null) {
            min = (min == null) ? range2.min : Math.min(min, range2.min);
        }
        if(range1 != null && range1.max != null) {
            max = range1.max;
        }
        if(range2 != null && range2.max != null) {
            max = (max == null) ? range2.max : Math.max(max, range2.max);
        }
        return new Range(min, max);
    }

    @Override
    public String toString() {
        return "Range{" +
                "getMin=" + min +
                ", max=" + max +
                '}';
    }
}