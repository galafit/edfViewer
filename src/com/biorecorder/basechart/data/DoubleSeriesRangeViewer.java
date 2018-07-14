package com.biorecorder.basechart.data;

/**
 * Created by galafit on 13/7/18.
 */
public interface DoubleSeriesRangeViewer extends DoubleSeries {
    void setViewRange(long startIndex, long length);

}
