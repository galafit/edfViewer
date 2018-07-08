package com.biorecorder.basechart.data;

import com.biorecorder.basechart.chart.Range;
import com.biorecorder.basechart.data.grouping.GroupingType;

/**
 * Created by galafit on 17/9/17.
 */
public interface NumberColumn {
    public long size();
    public double value(long index);

    public Range extremes(long from, int length);
    public long upperBound(double value, long from, int length);
    public long lowerBound(double value, long from, int length);
    public NumberColumn copy();

    public NumberColumn[] group(GroupingType groupingType, LongSeries groupIndexes);

}

