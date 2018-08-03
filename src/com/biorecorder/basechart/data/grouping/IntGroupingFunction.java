package com.biorecorder.basechart.data.grouping;

import com.biorecorder.basechart.data.IntSeries;

/**
 * Created by galafit on 4/7/18.
 */
public interface IntGroupingFunction {
    int[] group(IntSeries series, long from, long length);
    void reset();
}
