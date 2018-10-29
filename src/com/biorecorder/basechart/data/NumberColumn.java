package com.biorecorder.basechart.data;

import com.biorecorder.basechart.Range;
import com.biorecorder.basechart.grouping.GroupingApproximation;
import com.biorecorder.util.series.LongSeries;

/**
 * Wrapper class that permits work with series of different types
 * (ShortSeries, IntSeries, LongSeries, FloatSeries, DoubleSeries)
 * in unified manner.
 * Similar to Column in Table - it is the base building block to
 * create DataSeries
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

    /**
     * Appends the specified <tt>value</tt> to the end of this column (optional operation)
     * @param value to be added
     * @throws UnsupportedOperationException if the add operation is not supported by this column
     */
    public abstract void add(double value) throws UnsupportedOperationException;

    /**
     * Appends all values from the array to the end of this column, in order (optional operation)
     * @param values array with values to be added
     * @throws UnsupportedOperationException if the add operation is not supported by this column
     */
    public abstract void add(double[] values) throws UnsupportedOperationException;

    /**
     * Removes the value at the specified position in this column (optional operation).
     * Shifts any subsequent values to the left (subtracts one from their indices)
     * @param index - the index of the value to be removed
     */
    public abstract void remove(int index);


    public void setGroupingType(GroupingApproximation groupingType) {
        this.groupingType = groupingType;
    }

    public abstract long size();
    public abstract double value(long index);

    // if length == -1 real size will be used
    public abstract NumberColumn subColumn(long fromIndex, long length);


    public abstract Range extremes();
    public abstract long upperBound(double value);
    public abstract long lowerBound(double value);
    public abstract long binarySearch(double value);

    public abstract NumberColumn[] group(LongSeries groupIndexes);

    public abstract NumberColumn copy();
    public abstract NumberColumn cache();

}

