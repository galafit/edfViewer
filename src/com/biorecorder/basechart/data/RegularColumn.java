package com.biorecorder.basechart.data;

import com.biorecorder.basechart.Range;
import com.biorecorder.basechart.grouping.DoubleGroupFunction;
import com.biorecorder.basechart.grouping.GroupApproximation;
import com.biorecorder.basechart.grouping.IntGroupFunction;
import com.biorecorder.util.series.DoubleRegularSeries;
import com.biorecorder.util.series.DoubleSeries;
import com.biorecorder.util.series.LongRegularSeries;
import com.biorecorder.util.series.LongSeries;

/**
 * Created by galafit on 1/11/17.
 */
public class RegularColumn extends DoubleColumn {

    public RegularColumn(DoubleRegularSeries regularSeries) {
        super(regularSeries);
    }

    public RegularColumn(double startValue, double dataInterval, long size) {
        this(new DoubleRegularSeries(startValue, dataInterval, size));
    }

    public RegularColumn(double startValue, double dataInterval) {
        this(startValue, dataInterval, Long.MAX_VALUE);
    }

    public RegularColumn() {
        this(0, 1);
    }

    public double getDataInterval() {
        return ((DoubleRegularSeries) series).getDataInterval();
    }

    @Override
    public void add(double value) throws UnsupportedOperationException {
        if(size() < Long.MAX_VALUE) {
            setSize(size() + 1);
        }
    }

    @Override
    public void add(double[] values) throws UnsupportedOperationException {
        if(size() < Long.MAX_VALUE - values.length) {
            setSize(size() + values.length);
        }
        setSize(Long.MAX_VALUE);
    }

    @Override
    public void remove(int index) {
        if(size() < Long.MAX_VALUE) {
            setSize(size() - 1);
        }
    }

    public void setSize(long size) {
        ((DoubleRegularSeries) series).size(size);
    }

    @Override
    public Range extremes() {
        return new Range(value(0), value(size() - 1));
    }

    @Override
    public long upperBound(double value) {
        return lowerBound(value) + 1;
    }

    @Override
    public long lowerBound(double value) {
        long lowerBoundIndex = (long) ((value - value(0)) / getDataInterval());
        return lowerBoundIndex;
    }

    @Override
    public long binarySearch(double value) {
        long lowerBoundIndex = lowerBound(value);
        if(Double.doubleToLongBits(value) == Double.doubleToLongBits(value(lowerBoundIndex))) {
            return lowerBoundIndex;
        }
        long insertionPoint = lowerBoundIndex + 1;

        return  -insertionPoint - 1;

    }

    @Override
    protected DoubleSeries groupSeries(GroupApproximation groupApproximation, LongSeries groupStartIndexes) {
        if(groupStartIndexes instanceof LongRegularSeries) {
            long numberOfGroupedIntervals = ((LongRegularSeries) groupStartIndexes).getDataInterval();
            double resultantInterval = getDataInterval() * numberOfGroupedIntervals;

            DoubleGroupFunction groupFunction = (DoubleGroupFunction) groupApproximation.getGroupingFunction("double");
            double groupedStartValue = groupFunction.addToGroup(series, 0, numberOfGroupedIntervals);

            return new DoubleRegularSeries(groupedStartValue, resultantInterval);

        } else {
            return super.groupSeries(groupApproximation, groupStartIndexes);
        }
    }

    @Override
    public NumberColumn subColumn(long fromIndex, long length) {
        return new RegularColumn(value(fromIndex), getDataInterval(), length);
    }

    @Override
    public NumberColumn cache() {
        return copy();
    }

    @Override
    public NumberColumn copy() {
        return new RegularColumn(value(0), getDataInterval(), size());
    }
}
