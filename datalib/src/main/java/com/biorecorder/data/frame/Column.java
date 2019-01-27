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

    BRange minMax(int length);

    public boolean isIncreasing(int length);

    public boolean isDecreasing(int length);

    /**
     * Returns a sorted version of the column without modifying the order
     * of the underlying data.
     *
     * @return array of indexes representing sorted view of the underlying data
     */
    int[] sort(int length);

    Column slice(int from, int length);
    Column view(int from, int length);
    Column view(int[] elementsOrder);

    void cache(int nLastExcluded);
    void disableCaching();

    int nearest(double value, int from, int length);

    IntSequence group(double interval);
    Column aggregate(AggregateFunction aggregateFunction, IntSequence groupIndexes);
}
