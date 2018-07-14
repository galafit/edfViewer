package com.biorecorder.basechart.chart;

import java.text.MessageFormat;

/**
 * Created by galafit on 11/7/17.
 */
public class Range {
    private double start;
    private double end;

    public Range(double start, double end) {
        if (start > end){
            String errorMessage = "Range Error. Expected Start <= End. Start = {0}, End = {1}.";
            String formattedError = MessageFormat.format(errorMessage, start, end);
            throw new IllegalArgumentException(formattedError);
        }
        this.start = start;
        this.end = end;
    }

    public boolean contains(double value) {
        if(value >= start && value <= end) {
            return true;
        }
        return false;
    }

    public  double getStart() {
        return start;
    }

    public double getEnd() {
        return end;
    }

    public double length() {
        return end - start;
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
        return new Range(Math.min(range1.start, range2.start), Math.max(range1.end, range2.end));
    }

    @Override
    public String toString() {
        return "Range{" +
                "getStart=" + start +
                ", end=" + end +
                '}';
    }
}