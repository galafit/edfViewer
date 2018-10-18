package com.biorecorder.basechart.grouping.functions;

import com.biorecorder.util.series.IntSeries;

/**
 * Created by galafit on 6/7/18.
 */
public class IntGroupingMinMax implements IntGroupingFunction {
    long lastFrom = -1;
    long lastLength;
    int[] minMax = new int[2];

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
            minMax[0] = series.get(from);
            minMax[1] = series.get(from);
            start = from + 1;
            end = from + length;
        }
        for (long i = start; i < end; i++) {
            minMax[0] = Math.min(series.get(i), minMax[0]);
            minMax[1] = Math.max(series.get(i), minMax[1]);
        }
        lastFrom = from;
        lastLength = length;
        return minMax;
    }

    @Override
    public void reset() {
        lastFrom = -1;
    }
}

