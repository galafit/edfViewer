package com.biorecorder.basechart.data;

import com.biorecorder.basechart.Range;
import com.biorecorder.basechart.grouping.GroupingApproximation;
import com.biorecorder.util.series.LongSeries;

/**
 * Created by galafit on 17/9/17.
 */
public abstract class NumberColumn {
    protected String name;
    protected GroupingApproximation groupingType = GroupingApproximation.AVG;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setGroupingType(GroupingApproximation groupingType) {
        this.groupingType = groupingType;
    }

    public abstract long size();
    public abstract double value(long index);

    // if length == -1 real size will be used
    public abstract void setViewRange(long rangeStart, long rangeLength);

    public abstract Range extremes(long from, long length);
    public abstract long upperBound(double value, long from, long length);
    public abstract long lowerBound(double value, long from, long length);
    public abstract long binarySearch(double value, long from, int length);


    public abstract void enableCaching(boolean isLastElementCached);
    public abstract void enableCaching(boolean isLastElementCached, NumberColumn column);
    public abstract void disableCaching();
    public abstract void clear();

    public abstract NumberColumn[] group(LongSeries groupIndexes);

    public abstract NumberColumn copy();

}

