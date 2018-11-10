package com.biorecorder.data.frame;

import com.biorecorder.data.aggregation.AggregateFunction;
import com.biorecorder.basecharts.Range;
import com.biorecorder.data.list.DoubleArrayList;
import com.biorecorder.data.aggregation.impl.DoubleAggregateFunction;;
import com.biorecorder.data.sequence.DoubleSequence;
import com.biorecorder.data.sequence.LongSequence;
import com.biorecorder.data.sequence.SequenceUtils;

import java.util.List;

/**
 * Created by galafit on 27/9/17.
 */
class DoubleColumn extends NumberColumn {
    protected DoubleSequence sequence;

    public DoubleColumn(DoubleSequence sequence) {
        this.sequence = sequence;
    }

    public DoubleColumn(double[] data) {
        this(new DoubleSequence() {
            @Override
            public long size() {
                return data.length;
            }

            @Override
            public double get(long index) {
                return data[(int) index];
            }
        });
    }

    public DoubleColumn(List<Double> data) {
        this(new DoubleSequence() {
            @Override
            public long size() {
                return data.size();
            }

            @Override
            public double get(long index) {
                return data.get((int) index);
            }
        });
    }

    @Override
    public void add(double value) throws UnsupportedOperationException {
        if(sequence instanceof DoubleArrayList) {
            ((DoubleArrayList) sequence).add((double) value);
        } else {
            throw  new UnsupportedOperationException("Value can be added to the column only if that column wraps ArrayList");
        }
    }

    @Override
    public void remove(int index) {
        if(sequence instanceof DoubleArrayList) {
            ((DoubleArrayList) sequence).remove(index);
        } else {
            throw  new UnsupportedOperationException("Value can be removed from the column only if that column wraps ArrayList");
        }
    }

    @Override
    public void add(double[] values) throws UnsupportedOperationException {
        if(sequence instanceof DoubleArrayList) {
            double[] castedValues = new double[values.length];
            for (int i = 0; i < values.length; i++) {
               castedValues[i] = (double) values[i];
            }
            ((DoubleArrayList)sequence).add(castedValues);
        } else {
            throw  new UnsupportedOperationException("Values can be added to the column only if that column wraps ArrayList");
        }
    }

    @Override
    public long size() {
        return sequence.size();
    }

    @Override
    public double value(long index) {
        return sequence.get(index);
    }

    @Override
    public NumberColumn subColumn(long fromIndex, long length) {
        DoubleSequence subSequence = new DoubleSequence() {
            @Override
            public long size() {
                if(length < 0) {
                    return sequence.size() - fromIndex;
                }
                return length;
            }

            @Override
            public double get(long index) {
                return sequence.get(index + fromIndex);
            }
        };
        DoubleColumn subColumn = new DoubleColumn(subSequence);
        subColumn.name = name;
        subColumn.aggregateFunctions = aggregateFunctions;
        return subColumn;
    }

    @Override
    public Range extremes() {
        long size = sequence.size();
        if(size == 0){
            return null;
        }
        if (size > Integer.MAX_VALUE) {
            String errorMessage = "Extremes can not be find if size > Integer.MAX_VALUE. Size = " + size();
            throw new IllegalArgumentException(errorMessage);
        }

        // invoke data.get(i) can be expensive in the case data is grouped data
        double dataItem = sequence.get(0); //
        double min = dataItem;
        double max = dataItem;
        for (long i = 1; i < size ; i++) {
            dataItem = sequence.get(i);
            min = (double)Math.min(min, dataItem);
            max = (double)Math.max(max, dataItem);
        }
        return new Range(min, max);
    }

    @Override
    public long binarySearch(double value) {
        long size = sequence.size();
        if (size > Integer.MAX_VALUE) {
            String errorMessage = "Binary search can not be done if size > Integer.MAX_VALUE. Size = " + size;
            throw new IllegalArgumentException(errorMessage);
        }
        return SequenceUtils.upperBound(sequence, (double) value, 0, (int) size);

    }

    @Override
    public long upperBound(double value) {
        long size = sequence.size();
        if (size > Integer.MAX_VALUE) {
            String errorMessage = "Upper bound binary search can not be done if size > Integer.MAX_VALUE. Size = " + size;
            throw new IllegalArgumentException(errorMessage);
        }
        return SequenceUtils.upperBound(sequence, (double) value, 0, (int) size);
    }

    @Override
    public long lowerBound(double value) {
        long size = sequence.size();
        if (size > Integer.MAX_VALUE) {
            String errorMessage = "Lower bound binary search can not be done if size > Integer.MAX_VALUE. Size = " + size;
            throw new IllegalArgumentException(errorMessage);
        }
        return SequenceUtils.lowerBound(sequence, (double) value, 0, (int) size);
    }

    @Override
    public NumberColumn copy() {
        DoubleColumn copyColumn = new DoubleColumn(sequence);
        copyColumn.name = name;
        copyColumn.aggregateFunctions = aggregateFunctions;
        return copyColumn;
    }


    @Override
    public NumberColumn cache() {
        long size = sequence.size();
        if (size > Integer.MAX_VALUE) {
            String errorMessage = "Column can not be cached if its size > Integer.MAX_VALUE. Size = " + size;
            throw new IllegalArgumentException(errorMessage);
        }
        DoubleArrayList list = new DoubleArrayList((int) size);
        for (int i = 0; i < size; i++) {
            list.add(sequence.get(i));
        }
        DoubleColumn cacheColumn = new DoubleColumn(list);
        cacheColumn.name = name;
        cacheColumn.aggregateFunctions = aggregateFunctions;
        return cacheColumn;
    }

    @Override
    public NumberColumn[] group(LongSequence groupStartIndexes) {
        NumberColumn[] resultantColumns = new NumberColumn[aggregateFunctions.length];

        for (int i = 0; i < aggregateFunctions.length; i++) {
            resultantColumns[i] = new DoubleColumn(new GroupedSequence(aggregateFunctions[i], groupStartIndexes));
            String resultantName = name;
            if(aggregateFunctions.length > 1) {
                resultantName = name + " "+aggregateFunctions[i].name();
            }
            resultantColumns[i].setName(resultantName);
            resultantColumns[i].setAggregateFunctions(aggregateFunctions[i]);
        }
        return resultantColumns;
    }

    class GroupedSequence implements DoubleSequence {
        private LongSequence groupStartIndexes;
        private long lastGroupValueStart = -1;
        private long lastGroupValueLength;

        private final DoubleAggregateFunction aggregateFunction;

        public GroupedSequence(AggregateFunction aggregateFunction, LongSequence groupStartIndexes) {
            this.groupStartIndexes = groupStartIndexes;
            this.aggregateFunction = (DoubleAggregateFunction) aggregateFunction.getFunctionImpl("double");
        }

        @Override
        public long size() {
            return groupStartIndexes.size() - 1;
        }

        @Override
        public double get(long index) {
            if(lastGroupValueStart != groupStartIndexes.get(index)) {
                aggregateFunction.reset();
                lastGroupValueLength = 0;
            }

            long groupEnd = Math.min(groupStartIndexes.get(index + 1), sequence.size());

            double groupValue = aggregateFunction.addToGroup(sequence, groupStartIndexes.get(index) + lastGroupValueLength, groupEnd - groupStartIndexes.get(index) - lastGroupValueLength);

            lastGroupValueStart = groupStartIndexes.get(index);
            lastGroupValueLength = groupEnd - groupStartIndexes.get(index);
            return groupValue;
        }
    }
}
