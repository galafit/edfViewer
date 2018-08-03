package com.biorecorder.basechart.data;

/**
 * Created by galafit on 3/8/18.
 */
public interface ColumnGroupingManager {
    void setGroupIndexes(LongSeries groupIndexes);
    void reset();
    NumberColumn[] groupedColumns();
}
