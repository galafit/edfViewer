package com.biorecorder.basechart.data;

import com.biorecorder.basechart.chart.Range;
import com.biorecorder.basechart.data.grouping.GroupingType;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by galafit on 27/9/17.
 */
class FloatColumn implements NumberColumn {
    FloatSeries series;
    private GroupingType groupingType = GroupingType.AVG;

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
                if(index > Integer.MAX_VALUE) {
                    String errorMessage = "Error. Expected: index is integer. Index = {0}, Integer.MAX_VALUE = {1}.";
                    String formattedError = MessageFormat.format(errorMessage, index, Integer.MAX_VALUE);
                    throw new IllegalArgumentException(formattedError);
                }
                return data[(int)index];
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
        return DataManipulator.upperBound(series,  value, from, length);
    }

    @Override
    public long lowerBound(double value, long from, int length) {
        return DataManipulator.lowerBound(series,  value, from, length);
    }

    @Override
    public NumberColumn[] group(GroupingType groupingType, LongSeries groupIndexes) {

        return null;
    }
    @Override
    public NumberColumn copy() {
        FloatColumn newColumn = new FloatColumn(series);
        newColumn.groupingType = groupingType;
        return newColumn;
    }
}
