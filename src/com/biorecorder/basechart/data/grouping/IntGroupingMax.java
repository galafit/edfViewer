package com.biorecorder.basechart.data.grouping;

import com.biorecorder.basechart.data.IntSeries;

public class IntGroupingMax implements IntGroupingFunction {
    long lastFrom = -1;
    long lastLength;
    int[] max = new int[1];

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
            max[0] = series.get(from);
            start = from + 1;
            end = from + length;
        }
        for (long i = start; i < end; i++) {
            max[0] = Math.max(series.get(i), max[0]);
        }
        lastFrom = from;
        lastLength = length;
        return max;
    }

    @Override
    public void reset() {
        lastFrom = -1;
    }
}
