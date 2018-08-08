package com.biorecorder.basechart.data.grouping;

import com.biorecorder.basechart.data.IntSeries;

/**
 * Created by galafit on 3/8/18.
 */
public class IntGroupingMin implements IntGroupingFunction {
    long lastFrom = -1;
    long lastLength;
    int[] lastMin = new int[1];

    @Override
    public int[] group(IntSeries series, long from, long length) {
        if(length == 0) {
            throw new IllegalArgumentException("Number of grouping elements: "+length);
        }

        if(length <= 2) {
            return group1(series, from, length);
        }
        // last element is not stable, it can be changed in runtime
        // (for example if input series in its turn is also grouped series)
        // So we do NOT cache calculation with last element or when length <= 2
        long length1 = length - 1;
        long from1 = from;
        if(from == lastFrom) {
            from1 = from + lastLength;
        }
        int[] min = group1(series, from1, length1);
        min[0] = Math.min(min[0], lastMin[0]);
        lastMin[0] = min[0];
        lastFrom = from;
        lastLength = length1;
        min[0] = Math.min(min[0], series.get(from + length1));
        return min;
    }

    private int[] group1(IntSeries series, long from, long length) {
        int[] min = new int[1];
        min[0] = series.get(from);
        for (long i = from + 1; i < from + length; i++) {
            min[0] = Math.min(series.get(i), min[0]);
        }
        return min;
    }

    @Override
    public void reset() {
        lastFrom = -1;
    }
}
