package com.biorecorder.basechart.data.grouping;

import com.biorecorder.basechart.data.IntSeries;

/**
 * Created by galafit on 3/8/18.
 */
public class IntGroupingMin implements IntGroupingFunction {
    long lastFrom = -1;
    long lastLength;
    int[] min = new int[1];

    @Override
    public int[] group(IntSeries series, long from, long length) {
        if(length == 0) {
            throw new IllegalArgumentException("Number of grouping elements: "+length);
        }
        long start;
        long end;
        if(from == lastFrom) {
            start = from + lastLength;
            end = from + length;
        } else {
            avg[0] = series.get(from);
            start = from + 1;
            end = from + length;
        }
        for (long i = start; i < end; i++) {
           avg[0] = Math.min(series.get(i), avg[0]);
        }
        lastFrom = from;
        lastLength = length;
        return avg;
    }

    @Override
    public void reset() {
        lastFrom = -1;
    }
}
