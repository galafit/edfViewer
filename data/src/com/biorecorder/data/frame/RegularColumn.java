package com.biorecorder.data.frame;

import com.biorecorder.basecharts.Range;
import com.biorecorder.data.aggregation.impl.DoubleAggregateFunction;
import com.biorecorder.data.aggregation.AggregateFunction;
import com.biorecorder.data.sequence.DoubleRegularSequence;
import com.biorecorder.data.sequence.LongRegularSequence;
import com.biorecorder.data.sequence.LongSequence;

/**
 * Created by galafit on 1/11/17.
 */
public class RegularColumn extends DoubleColumn {

    public RegularColumn(DoubleRegularSequence regularSeries) {
        super(regularSeries);
        setAggregateFunctions(AggregateFunction.OPEN);
    }

    public RegularColumn(double startValue, double dataInterval, long size) {
        this(new DoubleRegularSequence(startValue, dataInterval, size));
    }

    public RegularColumn(double startValue, double dataInterval) {
        this(startValue, dataInterval, Long.MAX_VALUE);
    }

    public RegularColumn() {
        this(0, 1);
    }

    public double getDataInterval() {
        return ((DoubleRegularSequence) sequence).getDataInterval();
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
        ((DoubleRegularSequence) sequence).size(size);
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
    public NumberColumn[] group(LongSequence groupStartIndexes) {
        if(groupStartIndexes instanceof LongRegularSequence) {
            long numberOfGroupedIntervals = ((LongRegularSequence) groupStartIndexes).getDataInterval();
            double resultantGroupInterval = getDataInterval() * numberOfGroupedIntervals;

            NumberColumn[] resultantColumns = new NumberColumn[aggregateFunctions.length];

            for (int i = 0; i < aggregateFunctions.length; i++) {
                DoubleAggregateFunction groupFunction = (DoubleAggregateFunction) aggregateFunctions[i].getFunctionImpl("double");
                double groupedStartValue = groupFunction.addToGroup(sequence, 0, numberOfGroupedIntervals);
                resultantColumns[i] = new RegularColumn(groupedStartValue, resultantGroupInterval);
                String resultantName = name;
                if(aggregateFunctions.length > 1) {
                    resultantName = name + " "+ aggregateFunctions[i].name();
                }
                resultantColumns[i].setName(resultantName);
                resultantColumns[i].setAggregateFunctions(aggregateFunctions[i]);
            }
            return resultantColumns;
        } else {
            return super.group(groupStartIndexes);
        }
    }

    @Override
    public NumberColumn subColumn(long fromIndex, long length) {
        NumberColumn subColumn = new RegularColumn(value(fromIndex), getDataInterval(), length);
        subColumn.name = name;
        subColumn.aggregateFunctions = aggregateFunctions;
        return subColumn;
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
