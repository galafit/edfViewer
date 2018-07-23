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
    public void setCachingEnabled(boolean isCachingEnabled) {
          if( series instanceof CachedSeries) {
              if(!isCachingEnabled) {
                  series = ((CachedSeries) series).getCachedSeries();
              }
          } else {
              if(isCachingEnabled) {
                  series = new CachedSeries(series);
              }
          }
    }

    @Override
    public boolean isCachingEnabled() {
        return series instanceof CachedSeries;
    }

    @Override
    public void clearCache() {
        if(series instanceof CachedSeries) {
            ((CachedSeries) series).clearCache();
        }
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
    public void getGroupIndexes(LongArrayList groupIndexes, double groupInterval, long from, long length) {
        if(from == 0) {
            groupIndexes.add(from);
        }
        int intGroupInterval = (int) groupInterval;
        if(intGroupInterval <= 0) {
            groupInterval = 1;
        }

        int intervalsCount = (series.get(from) / intGroupInterval) * intGroupInterval;
        long till = from + length;
        for (long i = from;  i < till; i++) {
            if (series.get(i) >= intervalsCount + groupInterval) {
                groupIndexes.add(i);
                intervalsCount = (series.get(i) / intGroupInterval) * intGroupInterval;
            }
        }
        // last closing bin
        groupIndexes.add(till);
    }

    @Override
    public NumberColumn[] group(LongSeries groupIndexes) {
        IntSeries[] groupedSeries = new GroupingManager(groupingType, groupIndexes).getGroupedSeries();
        NumberColumn[] columns = new NumberColumn[groupedSeries.length];
        for (int i = 0; i < columns.length; i++) {
            columns[i] = new IntColumn(groupedSeries[i]);
        }
        return columns;
    }

    @Override
    public NumberColumn copy() {
        IntColumn newColumn = new IntColumn(series);
        newColumn.name = name;
        newColumn.groupingType = groupingType;
        return newColumn;
    }

    class GroupingManager {
        private GroupingType groupingType;
        private LongSeries groupIndexes;

        private long groupIndex = -1;
        private IntGroupingFunction groupingFunction;

        public GroupingManager(GroupingType groupingType, LongSeries groupIndexes) {
            this.groupingType = groupingType;
            this.groupIndexes = groupIndexes;
            groupingFunction = new IntGroupingAvg();
        }

        public long groupsCount() {
            return  groupIndexes.size();
        }

        public int[] getGroupValues(long groupIndex) {
            if(groupIndex != this.groupIndex) {
                groupingFunction.reset();
                this.groupIndex = groupIndex;
            }

            long groupStart = groupIndexes.get(groupIndex) + groupingFunction.elementCount();
            long groupEnd = groupIndexes.get(groupIndex+1);

            for (long j = groupStart; j < groupEnd; j++) {
                groupingFunction.add(series.get(j));
            }
            return groupingFunction.getGrouped();
        }

        public IntSeries[] getGroupedSeries() {
            IntSeries[] groupedSeries = new IntSeries[groupingType.getDimension()];
            for (int i = 0; i < groupedSeries.length; i++) {
                final int seriesNumber = i;
                groupedSeries[seriesNumber] = new IntSeries() {
                    @Override
                    public long size() {
                        return groupsCount() - 1;
                    }

                    @Override
                    public int get(long index) {
                        return getGroupValues(index)[seriesNumber];
                    }
                };
            }
            return groupedSeries;
        }
    }

    class CachedSeries implements IntSeriesRangeViewer {
        private IntSeriesRangeViewer series;
        private IntArrayList cache;

        public CachedSeries(IntSeriesRangeViewer series) {
            this.series = series;
            cache = new IntArrayList((int)series.size());
        }

        @Override
        public long size() {
            return series.size();
        }

        @Override
        public int get(long index) {
            if(index >= cache.size()) {
                for (long i = cache.size(); i <= index; i++) {
                   cache.add(series.get(i));
                }
            }
            return cache.get(index);
        }

        public IntSeriesRangeViewer getCachedSeries() {
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
