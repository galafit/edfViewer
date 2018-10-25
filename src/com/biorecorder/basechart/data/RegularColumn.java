package com.biorecorder.basechart.data;

import com.biorecorder.basechart.Range;
import com.biorecorder.util.series.LongSeries;

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

    public double getStartValue() {
        return startValue;
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
    public Range extremes(long from, long length) {
        return new Range(value(0), value(length - 1));
    }

    @Override
    public long upperBound(double value, long from, long length) {
        long lowerBoundIndex = lowerBound(value, from, length);
        if(value == value(lowerBoundIndex)) {
            return lowerBoundIndex;
        }
        return lowerBoundIndex + 1;
    }

    @Override
    public long lowerBound(double value, long from, long length) {
        long lowerBoundIndex = (long) ((value - value(from)) / dataInterval);
        if(lowerBoundIndex < 0) {
            lowerBoundIndex = 0;
        }
        return lowerBoundIndex;
    }

    @Override
    public long binarySearch(double value, long from, int length) {
        return 0;
    }

    @Override
    public void clear() {
        // do nothing
    }

    @Override
    public void enableCaching(boolean isLastElementCached) {
        // do nothing
    }

    @Override
    public void enableCaching(boolean isLastElementCached, NumberColumn column) {
        // do nothing
    }

    @Override
    public void disableCaching() {
       // do nothing
    }

    @Override
    public void setViewRange(long rangeStart, long rangeLength) {
        startIndex = rangeStart;
        if(startIndex < 0) {
            startIndex = 0;
        }
        size = rangeLength;
    }

    @Override
    public NumberColumn copy() {
        return new RegularColumn(startValue, dataInterval);
    }


    @Override
    public NumberColumn[] group(LongSeries groupIndexes) {
        return new NumberColumn[0];
    }



}
