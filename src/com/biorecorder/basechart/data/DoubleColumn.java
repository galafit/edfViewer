package com.biorecorder.basechart.data;

import com.biorecorder.util.lists.SeriesUtil;
import com.biorecorder.basechart.Range;
import com.biorecorder.util.lists.DoubleArrayList;
import com.biorecorder.basechart.grouping.GroupApproximation;
import com.biorecorder.basechart.grouping.DoubleGroupFunction;
import com.biorecorder.util.series.DoubleSeries;
import com.biorecorder.util.series.LongSeries;

import java.util.List;

/**
 * Created by galafit on 27/9/17.
 */
class DoubleColumn extends NumberColumn {
    protected DoubleSeries series;

    public DoubleColumn(DoubleSeries series) {
        this.series = series;
    }

    public DoubleColumn(double[] data) {
        this(new DoubleSeries() {
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
        this(new DoubleSeries() {
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
        if(series instanceof DoubleArrayList) {
            ((DoubleArrayList) series).add((double) value);
        } else {
            throw  new UnsupportedOperationException("Value can be added to the column only if that column wraps ArrayList");
        }
    }

    @Override
    public void remove(int index) {
        if(series instanceof DoubleArrayList) {
            ((DoubleArrayList) series).remove(index);
        } else {
            throw  new UnsupportedOperationException("Value can be removed from the column only if that column wraps ArrayList");
        }
    }

    @Override
    public void add(double[] values) throws UnsupportedOperationException {
        if(series instanceof DoubleArrayList) {
            double[] castedValues = new double[values.length];
            for (int i = 0; i < values.length; i++) {
               castedValues[i] = (double) values[i];
            }
            ((DoubleArrayList)series).add(castedValues);
        } else {
            throw  new UnsupportedOperationException("Values can be added to the column only if that column wraps ArrayList");
        }
    }

    @Override
    public NumberColumn[] group(LongSeries groupIndexes) {
        return new GroupingManager(groupApproximation, groupIndexes).groupedColumns();
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
        DoubleSeries subSeries = new DoubleSeries() {
            @Override
            public long size() {
                if(length < 0) {
                    return series.size() - fromIndex;
                }
                return length;
            }

            @Override
            public double get(long index) {
                return series.get(index + fromIndex);
            }
        };
        return new DoubleColumn(subSeries);
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
        double dataItem = series.get(0); //
        double min = dataItem;
        double max = dataItem;
        for (long i = 1; i < size ; i++) {
            dataItem = series.get(i);
            min = (double)Math.min(min, dataItem);
            max = (double)Math.max(max, dataItem);
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
        return SeriesUtil.upperBound(series, (double) value, 0, (int) size);

    }

    @Override
    public long upperBound(double value) {
        long size = series.size();
        if (size > Integer.MAX_VALUE) {
            String errorMessage = "Upper bound binary search can not be done if size > Integer.MAX_VALUE. Size = " + size;
            throw new IllegalArgumentException(errorMessage);
        }
        return SeriesUtil.upperBound(series, (double) value, 0, (int) size);
    }

    @Override
    public long lowerBound(double value) {
        long size = series.size();
        if (size > Integer.MAX_VALUE) {
            String errorMessage = "Lower bound binary search can not be done if size > Integer.MAX_VALUE. Size = " + size;
            throw new IllegalArgumentException(errorMessage);
        }
        return SeriesUtil.lowerBound(series, (double) value, 0, (int) size);
    }

    @Override
    public NumberColumn copy() {
        DoubleColumn newColumn = new DoubleColumn(series);
        newColumn.name = name;
        newColumn.groupApproximation = groupApproximation;
        return newColumn;
    }

    @Override
    public NumberColumn cache() {
        long size = series.size();
        if (size > Integer.MAX_VALUE) {
            String errorMessage = "Column can not be cached if its size > Integer.MAX_VALUE. Size = " + size;
            throw new IllegalArgumentException(errorMessage);
        }
        DoubleArrayList list = new DoubleArrayList((int) size);
        for (int i = 0; i < size; i++) {
            list.add(series.get(i));
        }
        return new DoubleColumn(list);
    }


    class GroupingManager {
        private GroupApproximation groupingApproximation;
        private LongSeries groupStartIndexes;
        private long lastGroupValueStart = -1;
        private long lastGroupValueLength;

        private final DoubleGroupFunction groupingFunction;

        public GroupingManager(GroupApproximation groupingApproximation, LongSeries groupStartIndexes) {
            this.groupingApproximation = groupingApproximation;
            groupingFunction = (DoubleGroupFunction) groupingApproximation.getGroupingFunction("double");
            this.groupStartIndexes = groupStartIndexes;
        }

        private long groupsCount() {
            return groupStartIndexes.size() - 1;
        }

        private double[] getGroupValues(long groupIndex) {
            if(lastGroupValueStart != groupStartIndexes.get(groupIndex)) {
               groupingFunction.reset();
               lastGroupValueLength = 0;
            }
            long groupEnd = Math.min(groupStartIndexes.get(groupIndex + 1), size());
            double[] groupedValues = groupingFunction.addToGroup(series, groupStartIndexes.get(groupIndex) + lastGroupValueLength, groupEnd - groupStartIndexes.get(groupIndex) - lastGroupValueLength);

            lastGroupValueStart = groupStartIndexes.get(groupIndex);
            lastGroupValueLength = groupStartIndexes.get(groupIndex + 1) - groupStartIndexes.get(groupIndex);
            return groupedValues;
        }

        public NumberColumn[] groupedColumns() {
            NumberColumn[] resultantColumns = new NumberColumn[groupingApproximation.getDimension()];
            for (int i = 0; i < resultantColumns.length; i++) {
                final int seriesNumber = i;
                DoubleSeries groupedSeries = new DoubleSeries() {
                    @Override
                    public long size() {
                        return groupsCount();
                    }

                    @Override
                    public double get(long index) {
                        return getGroupValues(index)[seriesNumber];
                    }
                };
                resultantColumns[i] = new DoubleColumn(groupedSeries);
                resultantColumns[i].setName(name);
                resultantColumns[i].setGroupApproximation(groupingApproximation);
            }
            if (resultantColumns.length == 2) { // LowHigh
                resultantColumns[0].setGroupApproximation(GroupApproximation.LOW);
                resultantColumns[1].setGroupApproximation(GroupApproximation.HIGH);
            }
           if (resultantColumns.length == 4) { // OHLC
                resultantColumns[0].setGroupApproximation(GroupApproximation.OPEN);
                resultantColumns[1].setGroupApproximation(GroupApproximation.HIGH);
                resultantColumns[2].setGroupApproximation(GroupApproximation.OPEN);
                resultantColumns[3].setGroupApproximation(GroupApproximation.OPEN);
           }

            return resultantColumns;
        }
    }
}
