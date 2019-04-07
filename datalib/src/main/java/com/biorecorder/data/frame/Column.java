package com.biorecorder.data.frame;

import com.biorecorder.data.sequence.IntSequence;

/**
 * Created by galafit on 14/1/19.
 */
public interface Column {
    int size();

    double value(int index);

    String label(int index);

    DataType dataType();

    /**
     * Returns a sorted version of the column without modifying the order
     * of the underlying data.
     *
     * @return array of indexes representing sorted view of the underlying data
     */
    int[] sort(int from, int length, boolean isParallel);

    Column slice(int from, int length);
    Column view(int from, int length);
    Column view(int[] order);

    int bisect(double value, int from, int length);

    Stats stats(int length, boolean isLastChangeable);

    void cache(boolean isLastChangeable);
    void disableCaching();

    /**
     * Equal Width Binning. This method divides the entire range of column data (max - min)
     * into intervals of equal size, searches the start indexes for every such
     * interval-bin-group and returns a sequence of that start indexes.
     * The column data must be sorted!!!
     */
    IntSequence group(double interval, IntWrapper length);

    Column aggregate(Aggregation aggregation, IntSequence groupIndexes);
    Column aggregate(Aggregation aggregation, int points);

}
