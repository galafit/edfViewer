package com.biorecorder.basechart.data;

import com.biorecorder.basechart.Range;
import com.biorecorder.basechart.data.grouping.GroupingType;
import com.biorecorder.basechart.data.grouping.IntGroupingAvg;
import com.biorecorder.basechart.data.grouping.IntGroupingFunction;

import java.util.List;

/**
 * Created by galafit on 27/9/17.
 */
class IntColumn extends NumberColumn {
    private IntSeriesRangeViewer series;

    public IntColumn(IntSeries series) {
        this.series = new SeriesViewer(series);
    }

    public IntColumn(int[] data) {
        this(new IntSeries() {
            @Override
            public long size() {
                return data.length;
            }

            @Override
            public int get(long index) {
                return data[(int)index];
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
                return data.get((int)index);
            }
        });
    }

    @Override
    public void enableCaching(boolean isLastElementCacheable) {
        disableCaching();
        series = new CachedSeries(series, isLastElementCacheable);
    }

    @Override
    public void disableCaching() {
        if( series instanceof CachedSeries) {
            series = ((CachedSeries) series).getOriginalSeries();
        }
    }

    @Override
    public void clearCache() {
        if(series instanceof CachedSeries) {
            ((CachedSeries) series).clearCache();
        }
    }

    @Override
    public ColumnGroupingManager groupingManager() {
        return new GroupingManager(groupingType);
    }

    @Override
    public void setViewRange(long from, long length) {
        series.setViewRange(from, length);
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
    public Range extremes(long length) {
        if (length > Integer.MAX_VALUE) {
            String errorMessage = "Extremes can not be find if data size > Integer.MAX_VALUE. Size = " + length;
            throw new IllegalArgumentException(errorMessage);
        }
        return DataManipulator.minMaxRange(series, 0, (int)length);
    }

    @Override
    public long upperBound(double value, long length) {
        if (length > Integer.MAX_VALUE) {
            String errorMessage = "Binary search can not be done if data size > Integer.MAX_VALUE. Size = " + length;
            throw new IllegalArgumentException(errorMessage);
        }
        return DataManipulator.upperBound(series, value, 0, (int)length);
    }

    @Override
    public long lowerBound(double value,  long length) {
        if (length > Integer.MAX_VALUE) {
            String errorMessage = "Binary search can not be done if data size > Integer.MAX_VALUE. Size = " + length;
            throw new IllegalArgumentException(errorMessage);
        }
        return DataManipulator.lowerBound(series, value, 0, (int)length);
    }

    @Override
    public NumberColumn copy() {
        IntColumn newColumn = new IntColumn(series);
        newColumn.name = name;
        newColumn.groupingType = groupingType;
        return newColumn;
    }

    class GroupingManager implements ColumnGroupingManager{
        private GroupingType groupingType;
        private LongSeries groupIndexes;

        private IntGroupingFunction groupingFunction;

        public GroupingManager(GroupingType groupingType) {
            this.groupingType = groupingType;
            groupingFunction = new IntGroupingAvg();
        }

        private long groupsCount() {
            return  groupIndexes.size() - 1;
        }

        private int[] getGroupValues(long groupIndex) {
            return groupingFunction.group(series, groupIndexes.get(groupIndex), groupIndexes.get(groupIndex+1) - groupIndexes.get(groupIndex));
        }

        @Override
        public NumberColumn[] groupedColumns() {
            NumberColumn[] resultantColumns = new NumberColumn[groupingType.getDimension()];
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
                if(resultantColumns.length == 1) {
                    resultantColumns[0].setGroupingType(groupingType);
                } else if(resultantColumns.length == 2) { // min-max
                    resultantColumns[0].setGroupingType(GroupingType.MIN);
                    resultantColumns[1].setGroupingType(GroupingType.MAX);
                }
                for (int j = 0; j < resultantColumns.length; j++) {
                   resultantColumns[i].setName(name);
                }
            }
            return resultantColumns;
        }

        @Override
        public void setGroupIndexes(LongSeries groupIndexes) {
            this.groupIndexes = groupIndexes;
            groupingFunction.reset();
        }

        @Override
        public void reset() {
            groupingFunction.reset();
        }
    }

    class CachedSeries implements IntSeriesRangeViewer {
        private IntSeriesRangeViewer series;
        private IntArrayList cache;
        private boolean isLastElementCacheable;

        public CachedSeries(IntSeriesRangeViewer series, boolean isLastElementCacheable) {
            this.series = series;
            this.isLastElementCacheable = isLastElementCacheable;
            cache = new IntArrayList((int)series.size());
        }

        @Override
        public long size() {
            return series.size();
        }

        @Override
        public int get(long index) {
            if(index == series.size() - 1 && !isLastElementCacheable) {
                return series.get(index);
            }

            if(index >= cache.size()) {
                for (long i = cache.size(); i <= index; i++) {
                   cache.add(series.get(i));
                }
            }
            return cache.get(index);
        }

        public IntSeriesRangeViewer getOriginalSeries() {
            return series;
        }


        @Override
        public void setViewRange(long startIndex, long length) {
            series.setViewRange(startIndex, length);
            cache.clear();
        }

        public void clearCache() {
            cache.clear();
        }
    }

    class SeriesViewer implements IntSeriesRangeViewer{
        private IntSeries series;
        private long startIndex = 0;
        private long length = -1;

        public SeriesViewer(IntSeries series) {
            this.series = series;
        }

        @Override
        public void setViewRange(long startIndex1, long length1) {
            startIndex = startIndex1;
            length = length1;
            if(startIndex < 0) {
                startIndex = 0;
            }
            if(startIndex >= series.size()) {
                startIndex = 0;
                length = 0;
            }
            if(length > series.size() - startIndex) {
                length = series.size() - startIndex;
            }
        }

        @Override
        public long size() {
            if(length < 0) {
                return series.size();
            }
            return length;
        }

        @Override
        public int get(long index) {
            return series.get(index + startIndex);
        }

    }

}
