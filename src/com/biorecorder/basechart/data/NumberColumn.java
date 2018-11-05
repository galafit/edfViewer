package com.biorecorder.basechart.data;

import com.biorecorder.basechart.Range;
import com.biorecorder.basechart.grouping.GroupApproximation;
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
    protected GroupApproximation[] groupApproximations = {GroupApproximation.AVERAGE};

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

    /**
     * Set approximations that will be used when data are grouped
     * In most cases only one group approximation is required
     * But there are some exceptions: low-high, or ohls (open, high, low, close)
     *
     * @param groupApproximations approximations used when data are grouped
     */
    public void setGroupApproximations(GroupApproximation... groupApproximations) {
        this.groupApproximations = groupApproximations;
    }

    /**
     * Get array of approximations used during data grouping
     * In most cases only one group approximation is used
     * But there are some exceptions: low-high, or ohls (open, high, low, close)
     *
     * @return array of approximations used during data grouping
     */
    public GroupApproximation[] getGroupApproximations() {
        return groupApproximations;
    }

    public abstract long size();
    public abstract double value(long index);

    // if length == -1 real size will be used
    public abstract NumberColumn subColumn(long fromIndex, long length);


    public abstract Range extremes();

    /**
     * <ul>
     *     <li>If there are several elements equal to the searched value
     *     function returns the <b>CLOSE</b> occurrence</li>
     *     <li>If there is no element equal to the searched value function returns
     *      index of the first element which is bigger than the searched value</li>
     *     <li>if all elements are less then the searched value
     *     then <b>size</b> will be returned</li>
     * </ul>
     */
    public abstract long upperBound(double value);

    /**
     * <ul>
     *     <li>If there are several elements equal to the searched value
     *     function returns the <b>OPEN</b> occurrence</li>
     *     <li>If there is no element equal to the searched value function returns
     *      index of the first element which is less than the searched value</li>
     *     <li>if all elements are bigger then the searched value
     *     then <b>-1</b> will be returned</li>
     *
     * </ul>
     */
    public abstract long lowerBound(double value);

    /**
     * @return index of the search value, if it is contained in the array
     *         within the specified range;
     *         otherwise, <tt>(-(<i>insertion point</i>) - 1)</tt>. The
     *         <i>insertion point</i> is defined as the point at which the
     *         key would be inserted into the array: the index of the first
     *         element in the range greater than the key,
     *         or <tt>size</tt> if all
     *         elements in the range are less than the specified key. Note
     *         that this guarantees that the return value will be &gt;= 0 if
     *         and only if the key is found.
     */
    public abstract long binarySearch(double value);

    public abstract NumberColumn[] group(LongSeries groupIndexes);

    public abstract NumberColumn copy();
    public abstract NumberColumn cache();

}

