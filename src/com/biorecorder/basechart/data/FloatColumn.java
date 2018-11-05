package com.biorecorder.basechart.data;

import com.biorecorder.util.lists.SeriesUtil;
import com.biorecorder.basechart.Range;
import com.biorecorder.util.lists.FloatArrayList;
import com.biorecorder.basechart.grouping.GroupApproximation;
import com.biorecorder.basechart.grouping.FloatGroupFunction;
import com.biorecorder.util.series.FloatSeries;
import com.biorecorder.util.series.LongSeries;

import java.util.List;

/**
 * Created by galafit on 27/9/17.
 */
class FloatColumn extends NumberColumn {
    protected FloatSeries series;

    public FloatColumn(FloatSeries series) {
        this.series = series;
    }

    public FloatColumn(float[] data) {
        this(new FloatSeries() {
            @Override
            public long size() {
                return data.length;
            }

            @Override
            public float get(long index) {
                return data[(int) index];
            }
        });
    }

    public FloatColumn(List<Float> data) {
        this(new FloatSeries() {
            @Override
            public long size() {
                return data.size();
            }

            @Override
            public float get(long index) {
                return data.get((int) index);
            }
        });
    }

    @Override
    public void add(double value) throws UnsupportedOperationException {
        if(series instanceof FloatArrayList) {
            ((FloatArrayList) series).add((float) value);
        } else {
            throw  new UnsupportedOperationException("Value can be added to the column only if that column wraps ArrayList");
        }
    }

    @Override
    public void remove(int index) {
        if(series instanceof FloatArrayList) {
            ((FloatArrayList) series).remove(index);
        } else {
            throw  new UnsupportedOperationException("Value can be removed from the column only if that column wraps ArrayList");
        }
    }

    @Override
    public void add(double[] values) throws UnsupportedOperationException {
        if(series instanceof FloatArrayList) {
            float[] castedValues = new float[values.length];
            for (int i = 0; i < values.length; i++) {
               castedValues[i] = (float) values[i];
            }
            ((FloatArrayList)series).add(castedValues);
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
        FloatSeries subSeries = new FloatSeries() {
            @Override
            public long size() {
                if(length < 0) {
                    return series.size() - fromIndex;
                }
                return length;
            }

            @Override
            public float get(long index) {
                return series.get(index + fromIndex);
            }
        };
        return new FloatColumn(subSeries);
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
        float dataItem = series.get(0); //
        float min = dataItem;
        float max = dataItem;
        for (long i = 1; i < size ; i++) {
            dataItem = series.get(i);
            min = (float)Math.min(min, dataItem);
            max = (float)Math.max(max, dataItem);
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
        return SeriesUtil.upperBound(series, (float) value, 0, (int) size);

    }

    @Override
    public long upperBound(double value) {
        long size = series.size();
        if (size > Integer.MAX_VALUE) {
            String errorMessage = "Upper bound binary search can not be done if size > Integer.MAX_VALUE. Size = " + size;
            throw new IllegalArgumentException(errorMessage);
        }
        return SeriesUtil.upperBound(series, (float) value, 0, (int) size);
    }

    @Override
    public long lowerBound(double value) {
        long size = series.size();
        if (size > Integer.MAX_VALUE) {
            String errorMessage = "Lower bound binary search can not be done if size > Integer.MAX_VALUE. Size = " + size;
            throw new IllegalArgumentException(errorMessage);
        }
        return SeriesUtil.lowerBound(series, (float) value, 0, (int) size);
    }

    @Override
    public NumberColumn copy() {
        FloatColumn copyColumn = new FloatColumn(series);
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
        FloatArrayList list = new FloatArrayList((int) size);
        for (int i = 0; i < size; i++) {
            list.add(series.get(i));
        }
        FloatColumn cacheColumn = new FloatColumn(list);
        cacheColumn.name = name;
        cacheColumn.groupApproximations = groupApproximations;
        return cacheColumn;
    }

    @Override
    public NumberColumn[] group(LongSeries groupStartIndexes) {
        NumberColumn[] resultantColumns = new NumberColumn[groupApproximations.length];

        for (int i = 0; i < groupApproximations.length; i++) {
            resultantColumns[i] = new FloatColumn(new GroupedSeries(groupApproximations[i], groupStartIndexes));
            String resultantName = name;
            if(groupApproximations.length > 1) {
                resultantName = name + " "+groupApproximations[i].name();
            }
            resultantColumns[i].setName(resultantName);
            resultantColumns[i].setGroupApproximations(groupApproximations[i]);
        }
        return resultantColumns;
    }

    class GroupedSeries implements FloatSeries {
        private LongSeries groupStartIndexes;
        private long lastGroupValueStart = -1;
        private long lastGroupValueLength;

        private final FloatGroupFunction groupFunction;

        public GroupedSeries(GroupApproximation groupApproximation, LongSeries groupStartIndexes) {
            this.groupStartIndexes = groupStartIndexes;
            groupFunction = (FloatGroupFunction) groupApproximation.getGroupingFunction("float");
        }

        @Override
        public long size() {
            return groupStartIndexes.size() - 1;
        }

        @Override
        public float get(long index) {
            if(lastGroupValueStart != groupStartIndexes.get(index)) {
                groupFunction.reset();
                lastGroupValueLength = 0;
            }
            long groupEnd = Math.min(groupStartIndexes.get(index + 1), size());
            float groupValue = groupFunction.addToGroup(series, groupStartIndexes.get(index) + lastGroupValueLength, groupEnd - groupStartIndexes.get(index) - lastGroupValueLength);

            lastGroupValueStart = groupStartIndexes.get(index);
            lastGroupValueLength = groupStartIndexes.get(index + 1) - groupStartIndexes.get(index);
            return groupValue;
        }
    }
}
