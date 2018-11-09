package com.biorecorder.basecharts;

import java.text.MessageFormat;

/**
 * Created by galafit on 8/1/18.
 */
public class RangeInt {
    private int start;
    private int end;

    public RangeInt(int start, int end) {
        if (start > end){
            String errorMessage = "Range error. Expected Start <= End. Start = {0}, End = {1}.";
            String formattedError = MessageFormat.format(errorMessage, start, end);
            throw new IllegalArgumentException(formattedError);
        }
        this.start = start;
        this.end = end;
    }

    public boolean contains(int value) {
        if(value >= start && value <= end) {
            return true;
        }
        return false;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public int length() {
        return end - start;
    }

    public static RangeInt max(RangeInt range1, RangeInt range2) {
        if(range1 == null) {
            return range2;
        }
        if(range2 == null) {
            return range1;
        }
        return new RangeInt(Math.min(range1.start, range2.start), Math.max(range1.end, range2.end));
    }

    @Override
    public String toString() {
        return "Range {" +
                "min =" + start +
                ", max =" + end +
                '}';
    }
}
