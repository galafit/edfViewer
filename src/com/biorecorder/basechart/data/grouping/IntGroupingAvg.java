package com.biorecorder.basechart.data.grouping;


import com.biorecorder.basechart.data.IntSeries;

public class IntGroupingAvg implements IntGroupingFunction {
    long lastFrom = -1;
    long lastLength;
    long sum;
    int[] avg = new int[1];

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
            sum = series.get(from);
            start = from + 1;
            end = from + length;
        }
        for (long i = start; i < end; i++) {
            sum += series.get(i);
        }
        lastFrom = from;
        lastLength = length;
        avg[0] = (int) (sum / length);
        return avg;
    }

    @Override
    public void reset() {
        lastFrom = -1;
    }
}
