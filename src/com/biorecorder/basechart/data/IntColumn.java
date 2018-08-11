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
    private SeriesRangeViewer series;

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
    public void clear() {
        series.clear();
    }

    @Override
    public NumberColumn[] group(LongSeries groupIndexes) {
        return new GroupingManager(groupingType, groupIndexes).groupedColumns();
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

    @Override
    public void cache(NumberColumn column) {
        series.cache(column);
    }

    class GroupingManager {
        private GroupingType groupingType;
        private LongSeries groupStartIndexes;

        private final IntGroupingFunction groupingFunction;

        public GroupingManager(GroupingType groupingType, LongSeries groupStartIndexes) {
            this.groupingType = groupingType;
            groupingFunction = new IntGroupingAvg();
            this.groupStartIndexes = groupStartIndexes;
        }

        private long groupsCount() {
            return  groupStartIndexes.size() - 1;
        }

        private int[] getGroupValues(long groupIndex) {
int[] value = groupingFunction.group(series, groupStartIndexes.get(groupIndex), groupStartIndexes.get(groupIndex+1) - groupStartIndexes.get(groupIndex));
           if(groupIndex == groupsCount() - 1)
            System.out.println(groupIndex +" index  size " +groupsCount() + "   " +groupStartIndexes.get(groupIndex)+" group bounds " + groupStartIndexes.get(groupIndex+1)+ " value "+value[0]);

return value;
            // return groupingFunction.group(series, groupStartIndexes.get(groupIndex), groupStartIndexes.get(groupIndex+1) - groupStartIndexes.get(groupIndex));
        }

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
                resultantColumns[i] = new IntColumn(groupedSeries) {
                    @Override
                    public void clear() {
                        super.clear();
                        groupingFunction.reset();
                    }

                    @Override
                    public void cache(NumberColumn column) {
                        super.cache(column);
                        groupingFunction.reset();
                    }
                };
                resultantColumns[i].setName(name);
                resultantColumns[i].setGroupingType(groupingType);
            }
            if(resultantColumns.length == 2) { // intersect-join
                resultantColumns[0].setGroupingType(GroupingType.MIN);
                resultantColumns[1].setGroupingType(GroupingType.MAX);
            }

            return resultantColumns;
        }
    }

    public interface SeriesRangeViewer extends IntSeries {
        void setViewRange(long startIndex, long length);
        void clear();
        void cache(NumberColumn column);

    }

    class CachedSeries implements SeriesRangeViewer {
        private SeriesRangeViewer series;
        private IntArrayList cache;
        private boolean isLastElementCacheable;

        public CachedSeries(SeriesRangeViewer series, boolean isLastElementCacheable) {
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
            if(!isLastElementCacheable && index == series.size() - 1) {
                return series.get(index);
            }

            if(index >= cache.size()) {
                for (long i = cache.size(); i <= index; i++) {
                   cache.add(series.get(i));
                }
            }
            return cache.get(index);
        }

        public SeriesRangeViewer getOriginalSeries() {
            return series;
        }

        @Override
        public void setViewRange(long startIndex, long length) {
            series.setViewRange(startIndex, length);
            cache.clear();
        }

        @Override
        public void clear() {
            cache.clear();
        }

        @Override
        public void cache(NumberColumn column) {
            cache.clear();
            for (int i = 0; i < column.size() - 1; i++) {
                cache.add((int)column.value(i));
            }
            if(isLastElementCacheable) {
                cache.add((int)column.value(column.size() - 1));
            }
        }
    }

    class SeriesViewer implements SeriesRangeViewer {
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

        @Override
        public void clear() {
            // do nothing
        }

        @Override
        public void cache(NumberColumn column) {
            // do nothing;
        }
    }

}
