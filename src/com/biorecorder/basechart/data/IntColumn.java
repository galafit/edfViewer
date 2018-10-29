package com.biorecorder.basechart.data;

import com.biorecorder.util.lists.SeriesUtil;
import com.biorecorder.basechart.Range;
import com.biorecorder.util.lists.IntArrayList;
import com.biorecorder.basechart.grouping.GroupingApproximation;
import com.biorecorder.basechart.grouping.IntGroupingFunction;
import com.biorecorder.util.series.IntSeries;
import com.biorecorder.util.series.LongSeries;

import java.util.List;

/**
 * Created by galafit on 27/9/17.
 */
class IntColumn extends NumberColumn {
    private final IntSeries series;

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
        }
        throw  new UnsupportedOperationException("Values can be added to the column only if that column wraps ArrayList");
    }

    @Override
    public void remove(int index) {
        if(series instanceof IntArrayList) {
            ((IntArrayList) series).remove(index);
        }
        throw  new UnsupportedOperationException("Value can be removed from the column only if that column wraps ArrayList");
    }

    @Override
    public void add(double[] values) throws UnsupportedOperationException {
        if(series instanceof IntArrayList) {
            int[] intValues = new int[values.length];
            for (int i = 0; i < values.length; i++) {
               intValues[i] = (int) values[i];
            }
            ((IntArrayList)series).add(intValues);
        }
        throw  new UnsupportedOperationException("Values can be added to the column only if that column wraps ArrayList");
    }

    @Override
    public NumberColumn[] group(LongSeries groupIndexes) {
        return new GroupingManager(groupingType, groupIndexes).groupedColumns();
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
        if(series.size() == 0){
            return null;
        }
        if (series.size() > Integer.MAX_VALUE) {
            String errorMessage = "Extremes can not be find if size > Integer.MAX_VALUE. Size = " + size();
            throw new IllegalArgumentException(errorMessage);
        }

        // invoke data.get(i) can be expensive in the case data is grouped data
        int dataItem = series.get(0); //
        int min = dataItem;
        int max = dataItem;
        for (long i = 1; i < series.size() ; i++) {
            dataItem = series.get(i);
            min = Math.min(min, dataItem);
            max = Math.max(max, dataItem);
        }
        return new Range(min, max);
    }

    @Override
    public long binarySearch(double value) {
        if (series.size() > Integer.MAX_VALUE) {
            String errorMessage = "Binary search can not be done if size > Integer.MAX_VALUE. Size = " + series.size();
            throw new IllegalArgumentException(errorMessage);
        }
        return SeriesUtil.upperBound(series, (int) value, 0, (int) series.size());

    }

    @Override
    public long upperBound(double value) {
        if (series.size() > Integer.MAX_VALUE) {
            String errorMessage = "Upper bound binary search not be done if size > Integer.MAX_VALUE. Size = " + series.size();
            throw new IllegalArgumentException(errorMessage);
        }
        return SeriesUtil.upperBound(series, (int) value, 0, (int) series.size());
    }

    @Override
    public long lowerBound(double value) {
        if (series.size() > Integer.MAX_VALUE) {
            String errorMessage = "Lower bound binary search not be done if size > Integer.MAX_VALUE. Size = " + series.size();
            throw new IllegalArgumentException(errorMessage);
        }
        return SeriesUtil.lowerBound(series, (int) value, 0, (int) series.size());
    }

    @Override
    public NumberColumn copy() {
        IntColumn newColumn = new IntColumn(series);
        newColumn.name = name;
        newColumn.groupingType = groupingType;
        return newColumn;
    }

    @Override
    public NumberColumn cache() {
        long size = size();
        if (size > Integer.MAX_VALUE) {
            String errorMessage = "Column can not be cached if its size > Integer.MAX_VALUE. Size = " + size;
            throw new IllegalArgumentException(errorMessage);
        }
        IntArrayList intList = new IntArrayList((int) size);
        for (int i = 0; i < size; i++) {
            intList.add(series.get(i));
        }
        return new IntColumn(intList);
    }


    class GroupingManager {
        private GroupingApproximation groupingApproximation;
        private LongSeries groupStartIndexes;
        private long lastGroupValueStart = -1;
        private long lastGroupValueLength;

        private final IntGroupingFunction groupingFunction;

        public GroupingManager(GroupingApproximation groupingApproximation, LongSeries groupStartIndexes) {
            this.groupingApproximation = groupingApproximation;
            groupingFunction = (IntGroupingFunction) groupingApproximation.getGroupingFunction("int");
            this.groupStartIndexes = groupStartIndexes;
        }

        private long groupsCount() {
            return groupStartIndexes.size() - 1;
        }

        private int[] getGroupValues(long groupIndex) {
            if(lastGroupValueStart != groupStartIndexes.get(groupIndex)) {
               groupingFunction.reset();
               lastGroupValueLength = 0;
            }
            int[] groupedValues = groupingFunction.addToGroup(series, groupStartIndexes.get(groupIndex) + lastGroupValueLength, groupStartIndexes.get(groupIndex + 1) - groupStartIndexes.get(groupIndex) - lastGroupValueLength);

            lastGroupValueStart = groupStartIndexes.get(groupIndex);
            lastGroupValueLength = groupStartIndexes.get(groupIndex + 1) - groupStartIndexes.get(groupIndex);
            return groupedValues;
        }

        public NumberColumn[] groupedColumns() {
            NumberColumn[] resultantColumns = new NumberColumn[groupingApproximation.getDimension()];
            for (int i = 0; i < resultantColumns.length; i++) {
                final int seriesNumber = i;
                IntSeries groupedSeries = new IntSeries() {
                    @Override
                    public long size() {
                        return groupsCount();
                    }

                    @Override
                    public int get(long index) {
                        return getGroupValues(index)[seriesNumber];
                    }
                };
                resultantColumns[i] = new IntColumn(groupedSeries);
                resultantColumns[i].setName(name);
                resultantColumns[i].setGroupingType(groupingApproximation);
            }
            if (resultantColumns.length == 2) { // MinMAx
                resultantColumns[0].setGroupingType(GroupingApproximation.MIN);
                resultantColumns[1].setGroupingType(GroupingApproximation.MAX);
            }

            return resultantColumns;
        }
    }
}
