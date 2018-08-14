package com.biorecorder.basechart.grouping.functions;

import com.biorecorder.basechart.series.IntSeries;

/**
 * Created by galafit on 4/7/18.
 */
public interface IntGroupingFunction {
    int[] group(IntSeries series, long from, long length);
    void reset();
}
