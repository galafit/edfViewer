package com.biorecorder.basechart.data;

import com.biorecorder.basechart.Range;

/**
 * Created by galafit on 1/11/17.
 */
public class RegularColumn extends NumberColumn {
    private double startValue;
    private double dataInterval;
    private long startIndex = 0;
    private long size = Long.MAX_VALUE;

    public RegularColumn(double startValue, double dataInterval) {
        this.startValue = startValue;
        this.dataInterval = dataInterval;
    }

    public RegularColumn() {
        this(0, 1);
    }

    public double getDataInterval() {
        return dataInterval;
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public double value(long index) {
        return startValue + dataInterval * (index + startIndex);
    }

    @Override
    public Range extremes(long length) {
        return new Range(value(0), value(length - 1));
    }

    @Override
    public long upperBound(double value, long length) {
        long lowerBoundIndex = lowerBound(value, length);
        if(value == value(lowerBoundIndex)) {
            return lowerBoundIndex;
        }
        return lowerBoundIndex + 1;
    }

    @Override
    public long lowerBound(double value, long length) {
        long lowerBoundIndex = (long) ((value - value(0)) / dataInterval);
        if(lowerBoundIndex < 0) {
            lowerBoundIndex = 0;
        }
        return lowerBoundIndex;
    }


    @Override
    public void clearCache() {
        // do nothing
    }

    @Override
    public void enableCaching(boolean isLastElementCacheable) {
        // do nothing
    }

    @Override
    public void disableCaching() {
       // do nothing
    }

    @Override
    public ColumnGroupingManager groupingManager() {
        return null;
    }

    @Override
    public void setViewRange(long from, long length) {
        startIndex = from;
        size = length;
    }

    @Override
    public NumberColumn copy() {
        return new RegularColumn(startValue, dataInterval);
    }

}
