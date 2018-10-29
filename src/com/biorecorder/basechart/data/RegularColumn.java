package com.biorecorder.basechart.data;

import com.biorecorder.basechart.Range;
import com.biorecorder.util.series.LongSeries;

/**
 * Created by galafit on 1/11/17.
 */
public class RegularColumn extends NumberColumn {
    private double startValue;
    private double dataInterval;
    private long size = Long.MAX_VALUE;

    public RegularColumn(double startValue, double dataInterval, long size) {
        this.startValue = startValue;
        this.dataInterval = dataInterval;
        this.size = size;
    }

    public RegularColumn(double startValue, double dataInterval) {
        this.startValue = startValue;
        this.dataInterval = dataInterval;
    }

    @Override
    public void add(double value) throws UnsupportedOperationException {
        if(size < Long.MAX_VALUE) {
            size++;
        }
    }

    @Override
    public void add(double[] values) throws UnsupportedOperationException {
        if(size < Long.MAX_VALUE - values.length) {
            size += values.length;
        }
    }

    @Override
    public void remove(int index) {
        if(size < Long.MAX_VALUE) {
            size--;
        }
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

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public double value(long index) {
        return startValue + dataInterval * index;
    }

    @Override
    public Range extremes() {
        return new Range(value(0), value(size - 1));
    }

    @Override
    public long upperBound(double value) {
        long lowerBoundIndex = lowerBound(value);
        if(value == value(lowerBoundIndex)) {
            return lowerBoundIndex;
        }
        return lowerBoundIndex + 1;
    }

    @Override
    public long lowerBound(double value) {
        long lowerBoundIndex = (long) ((value - value(0)) / dataInterval);
        if(lowerBoundIndex < 0) {
            lowerBoundIndex = 0;
        }
        return lowerBoundIndex;
    }

    @Override
    public long binarySearch(double value) {
        return 0;
    }

    @Override
    public NumberColumn subColumn(long fromIndex, long length) {
        return new RegularColumn(value(fromIndex), dataInterval, length);
    }

    @Override
    public NumberColumn cache() {
        return copy();
    }

    @Override
    public NumberColumn copy() {
        return new RegularColumn(startValue, dataInterval, size);
    }


    @Override
    public NumberColumn[] group(LongSeries groupIndexes) {
        return new NumberColumn[0];
    }



}
