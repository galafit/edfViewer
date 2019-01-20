package com.biorecorder.data.frame;

import com.biorecorder.basechart.BRange;
import com.biorecorder.data.aggregation.AggregateFunction;
import com.biorecorder.data.sequence.IntSequence;

/**
 * Created by galafit on 14/1/19.
 */
public interface Column {
    int size();

    double value(int index);

    String label(int index);

    DataType dataType();

    // if length == -1 real rowCount will be used
    Column slice(int from, int length);
    Column view(int from, int length);

    void cache(int nLastExcluded);
    void disableCaching();


    int nearest(double value, int from, int length);

    IntSequence group(double interval);
    Column aggregate(AggregateFunction aggregateFunction, IntSequence groupIndexes);

    BRange range(int from, int length);
}
