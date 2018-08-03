package com.biorecorder.basechart.data;

import com.biorecorder.basechart.Range;
import com.biorecorder.basechart.data.grouping.GroupingType;

/**
 * Created by galafit on 17/9/17.
 */
public abstract class NumberColumn {
    protected String name;
    protected GroupingType groupingType = GroupingType.AVG;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GroupingType getGroupingType() {
        return groupingType;
    }

    public void setGroupingType(GroupingType groupingType) {
        this.groupingType = groupingType;
    }

    public abstract long size();
    public abstract double value(long index);

    // if length == -1 real size will be used
    public abstract void setViewRange(long from, long length);

    public abstract Range extremes(long length);
    public abstract long upperBound(double value, long length);
    public abstract long lowerBound(double value, long length);
    public abstract void enableCaching(boolean isLastElementCacheable);
    public abstract void disableCaching();
    public abstract void clearCache();

    public abstract ColumnGroupingManager groupingManager();

    public abstract NumberColumn copy();

}

