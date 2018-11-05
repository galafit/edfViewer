package com.biorecorder.basechart.data;

import com.biorecorder.util.lists.SeriesUtil;
import com.biorecorder.basechart.Range;
import com.biorecorder.util.lists.LongArrayList;
import com.biorecorder.basechart.grouping.GroupApproximation;
import com.biorecorder.basechart.grouping.LongGroupFunction;
import com.biorecorder.util.series.LongSeries;
import com.biorecorder.util.series.LongSeries;

import java.util.List;

/**
 * Created by galafit on 27/9/17.
 */
class LongColumn extends NumberColumn {
    protected LongSeries series;

    public LongColumn(LongSeries series) {
        this.series = series;
    }

    public LongColumn(long[] data) {
        this(new LongSeries() {
            @Override
            public long size() {
                return data.length;
            }

            @Override
            public long get(long index) {
                return data[(int) index];
            }
        });
    }

    public LongColumn(List<Long> data) {
        this(new LongSeries() {
            @Override
            public long size() {
                return data.size();
            }

            @Override
            public long get(long index) {
                return data.get((int) index);
            }
        });
    }

    @Override
    public void add(double value) throws UnsupportedOperationException {
        if(series instanceof LongArrayList) {
            ((LongArrayList) series).add((long) value);
        } else {
            throw  new UnsupportedOperationException("Value can be added to the column only if that column wraps ArrayList");
        }
    }

    @Override
    public void remove(int index) {
        if(series instanceof LongArrayList) {
            ((LongArrayList) series).remove(index);
        } else {
            throw  new UnsupportedOperationException("Value can be removed from the column only if that column wraps ArrayList");
        }
    }

    @Override
    public void add(double[] values) throws UnsupportedOperationException {
        if(series instanceof LongArrayList) {
            long[] castedValues = new long[values.length];
            for (int i = 0; i < values.length; i++) {
               castedValues[i] = (long) values[i];
            }
            ((LongArrayList)series).add(castedValues);
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
        LongSeries subSeries = new LongSeries() {
            @Override
            public long size() {
                if(length < 0) {
                    return series.size() - fromIndex;
                }
                return length;
            }

            @Override
            public long get(long index) {
                return series.get(index + fromIndex);
            }
        };
        return new LongColumn(subSeries);
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
        long dataItem = series.get(0); //
        long min = dataItem;
        long max = dataItem;
        for (long i = 1; i < size ; i++) {
            dataItem = series.get(i);
            min = (long)Math.min(min, dataItem);
            max = (long)Math.max(max, dataItem);
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
        return SeriesUtil.upperBound(series, (long) value, 0, (int) size);

    }

    @Override
    public long upperBound(double value) {
        long size = series.size();
        if (size > Integer.MAX_VALUE) {
            String errorMessage = "Upper bound binary search can not be done if size > Integer.MAX_VALUE. Size = " + size;
            throw new IllegalArgumentException(errorMessage);
        }
        return SeriesUtil.upperBound(series, (long) value, 0, (int) size);
    }

    @Override
    public long lowerBound(double value) {
        long size = series.size();
        if (size > Integer.MAX_VALUE) {
            String errorMessage = "Lower bound binary search can not be done if size > Integer.MAX_VALUE. Size = " + size;
            throw new IllegalArgumentException(errorMessage);
        }
        return SeriesUtil.lowerBound(series, (long) value, 0, (int) size);
    }

    @Override
    public NumberColumn copy() {
        LongColumn copyColumn = new LongColumn(series);
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
        LongArrayList list = new LongArrayList((int) size);
        for (int i = 0; i < size; i++) {
            list.add(series.get(i));
        }
        LongColumn cacheColumn = new LongColumn(list);
        cacheColumn.name = name;
        cacheColumn.groupApproximations = groupApproximations;
        return cacheColumn;
    }

    @Override
    public NumberColumn[] group(LongSeries groupIndexes) {
        NumberColumn[] resultantColumns = new NumberColumn[groupApproximations.length];

        for (int i = 0; i < groupApproximations.length; i++) {
            resultantColumns[i] = new LongColumn(groupSeries(groupApproximations[i], groupIndexes));
            String resultantName = name;
            if(groupApproximations.length > 1) {
                resultantName = name + " "+groupApproximations[i].name();
            }
            resultantColumns[i].setName(resultantName);
            resultantColumns[i].setGroupApproximations(groupApproximations[i]);
        }
        return resultantColumns;
    }

    protected LongSeries groupSeries(GroupApproximation groupApproximation, LongSeries groupStartIndexes) {
        return new GroupedSeries(groupApproximation, groupStartIndexes);
    }

    class GroupedSeries implements LongSeries {
        private LongSeries groupStartIndexes;
        private long lastGroupValueStart = -1;
        private long lastGroupValueLength;

        private final LongGroupFunction groupFunction;

        public GroupedSeries(GroupApproximation groupApproximation, LongSeries groupStartIndexes) {
            this.groupStartIndexes = groupStartIndexes;
            groupFunction = (LongGroupFunction) groupApproximation.getGroupingFunction("long");
        }

        @Override
        public long size() {
            return groupStartIndexes.size() - 1;
        }

        @Override
        public long get(long index) {
            if(lastGroupValueStart != groupStartIndexes.get(index)) {
                groupFunction.reset();
                lastGroupValueLength = 0;
            }
            long groupEnd = Math.min(groupStartIndexes.get(index + 1), size());
            long groupValue = groupFunction.addToGroup(series, groupStartIndexes.get(index) + lastGroupValueLength, groupEnd - groupStartIndexes.get(index) - lastGroupValueLength);

            lastGroupValueStart = groupStartIndexes.get(index);
            lastGroupValueLength = groupStartIndexes.get(index + 1) - groupStartIndexes.get(index);
            return groupValue;
        }
    }
}
