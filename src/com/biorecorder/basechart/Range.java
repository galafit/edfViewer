package com.biorecorder.basechart;

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

    public boolean contains(Range range) {
        if(min <= range.getMin() && max >= range.getMax()) {
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

    /**
     * Create a range so that its:
     * min = min(range1.min, range2.min)
     * length = min(range1.length, range2.length) if both lengths > 0
     */
    public static Range min(Range range1, Range range2) {
        if(range1 == null && range2 == null) {
            return null;
        }
        if(range1 != null && range2 == null) {
            return range1;
        }
        if(range2 != null && range1 == null) {
            return range2;
        }
        double min = Math.min(range1.min, range2.min);
        double length;
        if(range1.length() == 0) {
            length = range2.length();
        } else if(range2.length() == 0) {
            length = range1.length();
        } else {
            length = Math.min(range1.length(), range2.length());
        }
        return new Range(min, min + length);
    }

    public static Range join(Range range1, Range range2) {
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

    public static Range intersect(Range range1, Range range2) {
        if(range1 == null && range2 == null) {
            return null;
        }
        if(range1 != null && range2 == null) {
            return range1;
        }
        if(range2 != null && range1 == null) {
            return range2;
        }
        double min = Math.max(range1.min, range2.min);
        double max =  Math.min(range1.max, range2.max);
        if(min <= max) {
            return new Range(min, max);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Range{" +
                "intersect=" + min +
                ", join=" + max +
                '}';
    }
}