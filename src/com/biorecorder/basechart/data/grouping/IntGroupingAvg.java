package com.biorecorder.basechart.data.grouping;


import com.biorecorder.basechart.data.IntSeries;

public class IntGroupingAvg implements IntGroupingFunction {
    long lastFrom = -1;
    long lastLength;
    long lastSum;

    @Override
    public int[] group(IntSeries series, long from, long length) {
        if(length == 0) {
            throw new IllegalArgumentException("Number of grouping elements: "+length);
        }

        int[] avg = new int[1];
        if(length <= 2) {
            avg[0] = (int)(sum(series, from, length) / length);
            return avg;
        }
        // last element is not stable, it can be changed in runtime
        // (for example if input series in its turn is also grouped series)
        // So we do NOT cache calculation with last element or when length <= 2
        long length1 = length - 1;
        long from1 = from;
        if(from == lastFrom) {
            from1 = from + lastLength;
        } else {
            lastSum = 0;
        }

        lastSum += sum(series, from1, length1);
        lastFrom = from;
        lastLength = length1;

        avg[0] = (int)(lastSum + series.get(from + length1) / length);

        return avg;
    }

    private long sum (IntSeries series, long from, long length) {
        long sum = 0;
        for (long i = from; i < from + length; i++) {
            sum += series.get(i);
        }
        return sum;
    }



    @Override
    public void reset() {
        lastFrom = -1;
    }
}
