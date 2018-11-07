package com.biorecorder.data;

import com.biorecorder.data.list.SeriesUtil;
import com.biorecorder.basechart.Range;
import com.biorecorder.data.list.IntArrayList;
import com.biorecorder.data.grouping.GroupApproximation;
import com.biorecorder.data.grouping.IntGroupFunction;
import com.biorecorder.data.series.IntSeries;
import com.biorecorder.data.series.LongSeries;

import java.util.List;

/**
 * Created by galafit on 27/9/17.
 */
class IntColumn extends NumberColumn {
    protected IntSeries series;

    public IntColumn(IntSeries series) {
        this.series = series;
    }

    public IntColumn(int[] data) {
        this(new IntSeries() {
            @Override
            public long size() {
                return data.length;
            }

            @Override
            public int get(long index) {
                return data[(int) index];
            }
        });
    }

    public IntColumn(List<Integer> data) {
        this(new IntSeries() {
            @Override
            public long size() {
                return data.size();
            }

            @Override
            public int get(long index) {
                return data.get((int) index);
            }
        });
    }

    @Override
    public void add(double value) throws UnsupportedOperationException {
        if(series instanceof IntArrayList) {
            ((IntArrayList) series).add((int) value);
        } else {
            throw  new UnsupportedOperationException("Value can be added to the column only if that column wraps ArrayList");
        }
    }

    @Override
    public void remove(int index) {
        if(series instanceof IntArrayList) {
            ((IntArrayList) series).remove(index);
        } else {
            throw  new UnsupportedOperationException("Value can be removed from the column only if that column wraps ArrayList");
        }
    }

    @Override
    public void add(double[] values) throws UnsupportedOperationException {
        if(series instanceof IntArrayList) {
            int[] castedValues = new int[values.length];
            for (int i = 0; i < values.length; i++) {
               castedValues[i] = (int) values[i];
            }
            ((IntArrayList)series).add(castedValues);
        } else {
            throw  new UnsupportedOperationException("Values can be added to the column only if that column wraps ArrayList");
        }
    }

    @Override
    public long size() {
        return series.size();
    }

    @Override
    public double value(long index) {
        return series.get(index);
    }

    @Override
    public NumberColumn subColumn(long fromIndex, long length) {
        IntSeries subSeries = new IntSeries() {
            @Override
            public long size() {
                if(length < 0) {
                    return series.size() - fromIndex;
                }
                return length;
            }

            @Override
            public int get(long index) {
                return series.get(index + fromIndex);
            }
        };
        return new IntColumn(subSeries);
    }

    @Override
    public Range extremes() {
        long size = series.size();
        if(size == 0){
            return null;
        }
        if (size > Integer.MAX_VALUE) {
            String errorMessage = "Extremes can not be find if size > Integer.MAX_VALUE. Size = " + size();
            throw new IllegalArgumentException(errorMessage);
        }

        // invoke data.get(i) can be expensive in the case data is grouped data
        int dataItem = series.get(0); //
        int min = dataItem;
        int max = dataItem;
        for (long i = 1; i < size ; i++) {
            dataItem = series.get(i);
            min = (int)Math.min(min, dataItem);
            max = (int)Math.max(max, dataItem);
        }
        return new Range(min, max);
    }

    @Override
    public long binarySearch(double value) {
        long size = series.size();
        if (size > Integer.MAX_VALUE) {
            String errorMessage = "Binary search can not be done if size > Integer.MAX_VALUE. Size = " + size;
            throw new IllegalArgumentException(errorMessage);
        }
        return SeriesUtil.upperBound(series, (int) value, 0, (int) size);

    }

    @Override
    public long upperBound(double value) {
        long size = series.size();
        if (size > Integer.MAX_VALUE) {
            String errorMessage = "Upper bound binary search can not be done if size > Integer.MAX_VALUE. Size = " + size;
            throw new IllegalArgumentException(errorMessage);
        }
        return SeriesUtil.upperBound(series, (int) value, 0, (int) size);
    }

    @Override
    public long lowerBound(double value) {
        long size = series.size();
        if (size > Integer.MAX_VALUE) {
            String errorMessage = "Lower bound binary search can not be done if size > Integer.MAX_VALUE. Size = " + size;
            throw new IllegalArgumentException(errorMessage);
        }
        return SeriesUtil.lowerBound(series, (int) value, 0, (int) size);
    }

    @Override
    public NumberColumn copy() {
        IntColumn copyColumn = new IntColumn(series);
        copyColumn.name = name;
        copyColumn.groupApproximations = groupApproximations;
        return copyColumn;
    }


    @Override
    public NumberColumn cache() {
        long size = series.size();
        if (size > Integer.MAX_VALUE) {
            String errorMessage = "Column can not be cached if its size > Integer.MAX_VALUE. Size = " + size;
            throw new IllegalArgumentException(errorMessage);
        }
        IntArrayList list = new IntArrayList((int) size);
        for (int i = 0; i < size; i++) {
            list.add(series.get(i));
        }
        IntColumn cacheColumn = new IntColumn(list);
        cacheColumn.name = name;
        cacheColumn.groupApproximations = groupApproximations;
        return cacheColumn;
    }

    @Override
    public NumberColumn[] group(LongSeries groupStartIndexes) {
        NumberColumn[] resultantColumns = new NumberColumn[groupApproximations.length];

        for (int i = 0; i < groupApproximations.length; i++) {
            resultantColumns[i] = new IntColumn(new GroupedSeries(groupApproximations[i], groupStartIndexes));
            String resultantName = name;
            if(groupApproximations.length > 1) {
                resultantName = name + " "+groupApproximations[i].name();
            }
            resultantColumns[i].setName(resultantName);
            resultantColumns[i].setGroupApproximations(groupApproximations[i]);
        }
        return resultantColumns;
    }

    class GroupedSeries implements IntSeries {
        private LongSeries groupStartIndexes;
        private long lastGroupValueStart = -1;
        private long lastGroupValueLength;

        private final IntGroupFunction groupFunction;

        public GroupedSeries(GroupApproximation groupApproximation, LongSeries groupStartIndexes) {
            this.groupStartIndexes = groupStartIndexes;
            groupFunction = (IntGroupFunction) groupApproximation.getGroupingFunction("int");
        }

        @Override
        public long size() {
            return groupStartIndexes.size() - 1;
        }

        @Override
        public int get(long index) {
            if(lastGroupValueStart != groupStartIndexes.get(index)) {
                groupFunction.reset();
                lastGroupValueLength = 0;
            }
            long groupEnd = Math.min(groupStartIndexes.get(index + 1), size());
            int groupValue = groupFunction.addToGroup(series, groupStartIndexes.get(index) + lastGroupValueLength, groupEnd - groupStartIndexes.get(index) - lastGroupValueLength);

            lastGroupValueStart = groupStartIndexes.get(index);
            lastGroupValueLength = groupStartIndexes.get(index + 1) - groupStartIndexes.get(index);
            return groupValue;
        }
    }
}
