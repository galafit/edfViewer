package com.biorecorder.basechart.data;

/**
 * Created by galafit on 13/7/18.
 */
public interface FloatSeriesRangeViewer extends FloatSeries {
    void setViewRange(long startIndex, long length);
}
