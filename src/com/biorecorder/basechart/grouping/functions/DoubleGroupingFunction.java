package com.biorecorder.basechart.grouping.functions;

import com.biorecorder.basechart.series.DoubleSeries;

/**
 * Created by galafit on 7/8/18.
 */
public interface DoubleGroupingFunction {
    double[] group(DoubleSeries series, long from, long length);
    void reset();
}
