package com.biorecorder.basechart.data;

import com.biorecorder.basechart.chart.Range;
import com.biorecorder.basechart.data.grouping.GroupingType;
import com.biorecorder.basechart.data.grouping.IntGroupingAvg;
import com.biorecorder.basechart.data.grouping.IntGroupingFunction;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by galafit on 27/9/17.
 */
class IntColumn implements NumberColumn {
    private IntSeries series;

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
                if(index > Integer.MAX_VALUE) {
                    String errorMessage = "Error. Expected index <= {0}, index = {1}.";
                    String formattedError = MessageFormat.format(errorMessage,Integer.MAX_VALUE,index);
                    throw new IllegalArgumentException(formattedError);

                }
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
                if(index > Integer.MAX_VALUE) {
                    String errorMessage = "Error. Expected: index is integer. Index = {0}, Integer.MAX_VALUE = {1}.";
                    String formattedError = MessageFormat.format(errorMessage, index, Integer.MAX_VALUE);
                    throw new IllegalArgumentException(formattedError);
                }
                return data.get((int)index);
            }
        });
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
    public Range extremes(long from, int length) {
        return DataManipulator.minMaxRange(series, from, length);
    }


    @Override
    public long upperBound(double value, long from, int length) {
        return DataManipulator.upperBound(series, value, from, length);
    }

    @Override
    public long lowerBound(double value, long from, int length) {
        return DataManipulator.lowerBound(series, value, from, length);
    }

    @Override
    public NumberColumn[] group(GroupingType groupingType, LongSeries groupIndexes) {
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
        return newColumn;
    }

    class GroupingManager {
        private GroupingType groupingType;
        private LongSeries groupIndexes;

        private long groupIndex = -1;
        private int[] groupValues;
        private IntGroupingFunction groupingFunction;

        public GroupingManager(GroupingType groupingType, LongSeries groupIndexes) {
            this.groupingType = groupingType;
            this.groupIndexes = groupIndexes;
            groupingFunction = new IntGroupingAvg();
        }

        public long groupCount() {
            return  groupIndexes.size();
        }

        public int[] getGroupValues(long groupIndex) {
            if(groupIndex != this.groupIndex) {
                groupValues = group(groupIndex);
            }
            return groupValues;
        }

        private int[] group(long groupIndex) {
            groupingFunction.reset();
            long groupStart = groupIndexes.get(groupIndex);
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
                        return groupCount();
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

}
