package com.biorecorder.data.list;

import com.biorecorder.data.sequence.DoubleSequence;

/**
 * A resizable, array-backed list of double primitives.
 *<p>
 * Based on openjdk ArrayList.java -
 * http://hg.openjdk.java.net/jdk7/jdk7/jdk/file/00cd9dc3c2b5/src/share/classes/java/util/ArrayList.java
 * <br> and trove ArrayListTemplate -
 * https://bitbucket.org/trove4j/trove/src/24dd57f48bf385fa41a878f8fad7ac44d8b1d53a/core/src/main/templates/gnu/trove/list/array/_E_ArrayList.template?at=master&fileviewer=file-view-default
 */
public class DoubleArrayList implements DoubleSequence {
    private double[] data;
    private int size;
    /**
     * The maximum rowCount of array to allocate.
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array rowCount exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    public DoubleArrayList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: "+ initialCapacity);
        }
        data = new double[initialCapacity];
    }

    public DoubleArrayList() {
        this(10);
    }

    public DoubleArrayList(double[] source) {
        size = source.length;
        data = new double[size];
        System.arraycopy(source, 0, data, 0, size);
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public double get(long index) {
        rangeCheck(index);
        return data[(int)index];
    }

    /**
     * Remove an element from the specified index
     */
    public double remove(int index) {
        double old = data[index];
        remove(index, 1);
        return old;
    }

    public void remove(int from, int length ) {
        if ( length == 0 ) return;
        rangeCheck(from);
        if ( from == 0 ) {
            // data at the front
            System.arraycopy( data, length, data, 0, size - length );
        }
        else if ( size - length == from ) {
            // no copy to make, decrementing pos "deletes" values at
            // the end
        }
        else {
            // data in the middle
            System.arraycopy( data, from + length, data, from,
                    size - ( from + length ) );
        }
        size -= length;
    }

    public void clear() {
        size = 0;
    }

    public void set( int index, double value) {
        rangeCheck(index);
        data[index] = value;
    }

    /**
     * Adds a new element to the to the end of the list
     */
    public void add(double value) {
        ensureCapacity(size + 1);  // Increments modCount!!
        data[size] = value;
        size++;
    }

     /**
     * Adds the values from the array <tt>values</tt> to the end of the
     * list, in order.
     */
    public void add(double[] values) {
        int numNew = values.length;
        ensureCapacity(size + numNew);  // Increments modCount
        System.arraycopy(values, 0, data, size, numNew);
        size += numNew;
    }

    /**
     * Inserts all of the elements from the given array into the
     * list, starting at the given position.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).
     *
     * @param index index at which to insert the first element from the
     *              given array
     * @param values array containing elements to be added to the list
     * @throws IndexOutOfBoundsException
     * @throws NullPointerException if the given array is null
     */

    public void add(int index, double[] values) {
        rangeCheckForAdd(index);

        int numNew = values.length;
        ensureCapacity(size + numNew);  // Increments modCount
        int numMoved = size - index;
        if (numMoved > 0)
            System.arraycopy(data, index, data, index + numNew,
                    numMoved);
        System.arraycopy(values, 0, data, index, numNew);
        size += numNew;
    }

    /**
     * Trims the capacity of this array list instance to be the
     * list's current rowCount.
     */
    public void trimToSize() {
        if ( data.length > size ) {
            double[] tmp = new double[size];
            System.arraycopy( data, 0, tmp, 0, size );
            data = tmp;
        }
    }

    public void ensureCapacity(int minCapacity) {
        int oldCapacity = data.length;
        if (minCapacity > oldCapacity) {
            int newCapacity = oldCapacity + (oldCapacity >> 1); // oldCapacity + 1/2 * oldCapacity

            if (newCapacity - minCapacity < 0)
                newCapacity = minCapacity;

            if (newCapacity - MAX_ARRAY_SIZE > 0)

                newCapacity = hugeCapacity(minCapacity);

            // minCapacity is usually close to rowCount, so this is a win:
            double[] tmp = new double[newCapacity];
            System.arraycopy( data, 0, tmp, 0, data.length );
            data = tmp;
        }
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }

    private void rangeCheck(long index) {
        if (index >= size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private void rangeCheckForAdd(int index) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private String outOfBoundsMsg(long index) {
        return "Index: "+index+", Size: "+size;
    }
}
