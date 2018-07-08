package com.biorecorder.basechart.data;

import com.biorecorder.basechart.chart.Range;
import com.biorecorder.basechart.data.grouping.GroupingType;

/**
 * Created by galafit on 1/11/17.
 */
public class RegularColumn implements NumberColumn {
    private double startValue;
    private double dataInterval;

    public RegularColumn(double startValue, double dataInterval) {
        this.startValue = startValue;
        this.dataInterval = dataInterval;
    }

    public double getStartValue() {
        return startValue;
    }

    public double getDataInterval() {
        return dataInterval;
    }

    public RegularColumn() {
        this(0, 1);
    }

    @Override
    public long size() {
        return Integer.MAX_VALUE;
    }

    @Override
    public double value(long index) {
        return startValue + dataInterval * index;
    }

    @Override
    public Range extremes(long from, int length) {
        double min = startValue;
        double max = startValue + (size() - 1) * dataInterval;
        return new Range(min, max);
    }

    @Override
    public long lowerBound(double value, long from, int length) {
        return (long) ((value - startValue) / dataInterval);
    }


    @Override
    public long upperBound(double value, long from, int length) {
        long lowerBound = lowerBound(value, from, length);
        if(value == value(lowerBound)) {
            return lowerBound;
        }
        return lowerBound + 1;
    }

    @Override
    public void setGroupingType(GroupingType groupingType) {
        // DO NOTHING!!!
    }

    @Override
    public void groupByNumber(int numberOfElementsInGroup, boolean isCachingEnable) {
        dataInterval = dataInterval * numberOfElementsInGroup;
        //startValue += dataInterval * (numberOfElementsInGroup - 1) / 2;
    }

    @Override
    public NumberColumn copy() {
        return new RegularColumn(startValue, dataInterval);
    }
}
