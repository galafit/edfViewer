package com.biorecorder.basechart.data.grouping;

import com.biorecorder.basechart.data.DoubleSeries;

/**
 * Created by galafit on 7/8/18.
 */
public interface DoubleGroupingFunction {
    double[] group(DoubleSeries series, long from, long length);
    void reset();
}
