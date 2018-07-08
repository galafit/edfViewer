package com.biorecorder.basechart.chart;

import java.text.MessageFormat;

/**
 * Created by galafit on 8/1/18.
 */
public class RangeInt {
    private int min;
    private int max;

    public RangeInt(int min, int max) {
        this.min = min;
        this.max = max;
        if (min > max){
            String errorMessage = "Error during creating Range. Expected Start <= End. Start = {0}, End = {1}.";
            String formattedError = MessageFormat.format(errorMessage, min, max);
            throw new IllegalArgumentException(formattedError);
        }
    }

    public boolean contains(int value) {
        if(value >= min && value <= max) {
            return true;
        }
        return false;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int length() {
        return max - min;
    }

    public static RangeInt max(RangeInt range1, RangeInt range2) {
        if(range1 == null) {
            return range2;
        }
        if(range2 == null) {
            return range1;
        }
        return new RangeInt(Math.min(range1.min, range2.min), Math.max(range1.max, range2.max));
    }
}
