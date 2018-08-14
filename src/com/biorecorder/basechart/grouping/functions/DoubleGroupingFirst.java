package com.biorecorder.basechart.grouping.functions;

import com.biorecorder.basechart.series.DoubleSeries;

/**
 * Created by galafit on 7/8/18.
 */
public class DoubleGroupingFirst implements DoubleGroupingFunction {
    long lastFrom = -1;
    double[] lastFirst = new double[1];

    @Override
    public double[] group(DoubleSeries series, long from, long length) {
        if(length == 0) {
            throw new IllegalArgumentException("Number of grouping elements: "+length);
        }

        double first[] = new double[1];
        if(lastFirst == first) {
            first[0] = lastFirst[0];
            return first;
        }

        first[0] = series.get(from);

        // last element is not stable, it can be changed in runtime
        // (for example if input series in its turn is also grouped series)
        // So we do NOT cache calculation with when length <= 2
        if(length >= 2) {
            lastFrom = from;
            lastFirst[0] = series.get(from);
        }
        return first;
    }

    @Override
    public void reset() {
       lastFrom = -1;
    }
}
