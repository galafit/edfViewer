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
    int[] sort(int from, int length, boolean isParallel);

    Column slice(int from, int length);
    Column view(int from, int length);
    Column view(int[] order);

    void cache(int nLastExcluded);
    void disableCaching();

    /**
     * Binary search algorithm. The sequence must be sorted! Find some occurrence
     * (if there are multiples, it returns some arbitrary one)
     * or the insertion point for value in data sequence to maintain sorted order.
     * If the sequence is not sorted, the results are undefined.
     * @return returned index i satisfies a[i-1] < v <= a[i]. If there is no suitable index, return <b>from</b>
     */
    int bisect(double value, int from, int length);

    /**
     * Lower bound binary search algorithm. The sequence must be sorted! Find the FIRST occurrence
     * or the insertion point for value in data sequence to maintain sorted order.
     * If the sequence is not sorted, the results are undefined.
     * @return returned index i satisfies a[i-1] < v <= a[i]. If there is no suitable index, return <b>from</b>
     */
    int bisectLeft(double value, int from, int length);

    /**
     * Upper bound search algorithm. The sequence must be sorted. Find the LAST occurrence
     * or the insertion point for value in data sequence to maintain sorted order.
     * If the sequence is not sorted, the results are undefined.
     * @return returned index i satisfies a[i-1] <= v < a[i]. If there is no suitable index, return <b>from + length</b>
     */
    int bisectRight(double value, int from, int length);

    /**
     * Equal Width Binning. This method divides the entire range of column data (max - min)
     * into intervals of equal size, searches the start indexes for every such
     * interval-bin-group and returns a sequence of that start indexes.
     * The column data must be sorted!!!
     */
    IntSequence group(double interval);

    Column aggregate(AggregateFunction aggregateFunction, IntSequence groupIndexes);
}
