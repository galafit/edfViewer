package com.biorecorder.basechart.data;

/**
 * Created by galafit on 13/7/18.
 */
public interface IntSeriesRangeViewer extends IntSeries {
    void setViewRange(long startIndex, long length);
}
