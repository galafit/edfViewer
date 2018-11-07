package com.biorecorder.data;

import com.biorecorder.data.list.SeriesUtil;
import com.biorecorder.basechart.Range;
import com.biorecorder.data.list.ShortArrayList;
import com.biorecorder.data.grouping.GroupApproximation;
import com.biorecorder.data.grouping.function.ShortGroupFunction;
import com.biorecorder.data.series.ShortSeries;
import com.biorecorder.data.series.LongSeries;

import java.util.List;

/**
 * Created by galafit on 27/9/17.
 */
class ShortColumn extends NumberColumn {
    protected ShortSeries series;

    public ShortColumn(ShortSeries series) {
        this.series = series;
    }

    public ShortColumn(short[] data) {
        this(new ShortSeries() {
            @Override
            public long size() {
                return data.length;
            }

            @Override
            public short get(long index) {
                return data[(int) index];
            }
        });
    }

    public ShortColumn(List<Short> data) {
        this(new ShortSeries() {
            @Override
            public long size() {
                return data.size();
            }

            @Override
            public short get(long index) {
                return data.get((int) index);
            }
        });
    }

    @Override
    public void add(double value) throws UnsupportedOperationException {
        if(series instanceof ShortArrayList) {
            ((ShortArrayList) series).add((short) value);
        } else {
            throw  new UnsupportedOperationException("Value can be added to the column only if that column wraps ArrayList");
        }
    }

    @Override
    public void remove(int index) {
        if(series instanceof ShortArrayList) {
            ((ShortArrayList) series).remove(index);
        } else {
            throw  new UnsupportedOperationException("Value can be removed from the column only if that column wraps ArrayList");
        }
    }

    @Override
    public void add(double[] values) throws UnsupportedOperationException {
        if(series instanceof ShortArrayList) {
            short[] castedValues = new short[values.length];
            for (int i = 0; i < values.length; i++) {
               castedValues[i] = (short) values[i];
            }
            ((ShortArrayList)series).add(castedValues);
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
        ShortSeries subSeries = new ShortSeries() {
            @Override
            public long size() {
                if(length < 0) {
                    return series.size() - fromIndex;
                }
                return length;
            }

            @Override
            public short get(long index) {
                return series.get(index + fromIndex);
            }
        };
        return new ShortColumn(subSeries);
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
        short dataItem = series.get(0); //
        short min = dataItem;
        short max = dataItem;
        for (long i = 1; i < size ; i++) {
            dataItem = series.get(i);
            min = (short)Math.min(min, dataItem);
            max = (short)Math.max(max, dataItem);
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
        return SeriesUtil.upperBound(series, (short) value, 0, (int) size);

    }

    @Override
    public long upperBound(double value) {
        long size = series.size();
        if (size > Integer.MAX_VALUE) {
            String errorMessage = "Upper bound binary search can not be done if size > Integer.MAX_VALUE. Size = " + size;
            throw new IllegalArgumentException(errorMessage);
        }
        return SeriesUtil.upperBound(series, (short) value, 0, (int) size);
    }

    @Override
    public long lowerBound(double value) {
        long size = series.size();
        if (size > Integer.MAX_VALUE) {
            String errorMessage = "Lower bound binary search can not be done if size > Integer.MAX_VALUE. Size = " + size;
            throw new IllegalArgumentException(errorMessage);
        }
        return SeriesUtil.lowerBound(series, (short) value, 0, (int) size);
    }

    @Override
    public NumberColumn copy() {
        ShortColumn copyColumn = new ShortColumn(series);
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
        ShortArrayList list = new ShortArrayList((int) size);
        for (int i = 0; i < size; i++) {
            list.add(series.get(i));
        }
        ShortColumn cacheColumn = new ShortColumn(list);
        cacheColumn.name = name;
        cacheColumn.groupApproximations = groupApproximations;
        return cacheColumn;
    }

    @Override
    public NumberColumn[] group(LongSeries groupStartIndexes) {
        NumberColumn[] resultantColumns = new NumberColumn[groupApproximations.length];

        for (int i = 0; i < groupApproximations.length; i++) {
            resultantColumns[i] = new ShortColumn(new GroupedSeries(groupApproximations[i], groupStartIndexes));
            String resultantName = name;
            if(groupApproximations.length > 1) {
                resultantName = name + " "+groupApproximations[i].name();
            }
            resultantColumns[i].setName(resultantName);
            resultantColumns[i].setGroupApproximations(groupApproximations[i]);
        }
        return resultantColumns;
    }

    class GroupedSeries implements ShortSeries {
        private LongSeries groupStartIndexes;
        private long lastGroupValueStart = -1;
        private long lastGroupValueLength;

        private final ShortGroupFunction groupFunction;

        public GroupedSeries(GroupApproximation groupApproximation, LongSeries groupStartIndexes) {
            this.groupStartIndexes = groupStartIndexes;
            groupFunction = (ShortGroupFunction) groupApproximation.getGroupingFunction("short");
        }

        @Override
        public long size() {
            return groupStartIndexes.size() - 1;
        }

        @Override
        public short get(long index) {
            if(lastGroupValueStart != groupStartIndexes.get(index)) {
                groupFunction.reset();
                lastGroupValueLength = 0;
            }
            long groupEnd = Math.min(groupStartIndexes.get(index + 1), size());
            short groupValue = groupFunction.addToGroup(series, groupStartIndexes.get(index) + lastGroupValueLength, groupEnd - groupStartIndexes.get(index) - lastGroupValueLength);

            lastGroupValueStart = groupStartIndexes.get(index);
            lastGroupValueLength = groupStartIndexes.get(index + 1) - groupStartIndexes.get(index);
            return groupValue;
        }
    }
}
