package com.biorecorder.basechart.data;

/**
 * Created by galafit on 2/8/18.
 */
public interface LongSeriesRangeViewer extends LongSeries {
    void setViewRange(long startIndex, long length);
}
