package com.biorecorder.basechart.data;

import java.text.MessageFormat;
import java.util.Arrays;

/**
 * Created by galafit on 12/10/17.
 */
public class IntArrayList implements IntSeries {
    private int[] data;
    private int size;

    public IntArrayList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: "+ initialCapacity);
        }
        data = new int[initialCapacity];
    }

    public IntArrayList() {
        this(10);
    }

    public IntArrayList(int[] source) {
        size = source.length;
        data = new int[size];
        System.arraycopy(source, 0, data, 0, size);
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public int get(long index) {
        if(index > Integer.MAX_VALUE) {
            String errorMessage = "Error. Expected: index is integer. Index = {0}, Integer.MAX_VALUE = {1}.";
            String formattedError = MessageFormat.format(errorMessage, index, Integer.MAX_VALUE);
            throw new IllegalArgumentException(formattedError);
        }
        if (index >= size) {
            throw new ArrayIndexOutOfBoundsException((int)index);
        }
        return data[(int)index];
    }

    /**
     * Remove an element from the specified index
     */
    public int remove(int index) {
        if (index < 0 || index >= size) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        int entry = data[index];
//    int[] outgoing = new int[count - 1];
//    System.arraycopy(com.biorecorder.basechart.data, 0, outgoing, 0, index);
//    count--;
//    System.arraycopy(com.biorecorder.basechart.data, index + 1, outgoing, 0, count - index);
//    com.biorecorder.basechart.data = outgoing;
        // For most cases, this actually appears to be faster
        // than arraycopy() on an array copying into itself.
        for (int i = index; i < size-1; i++) {
            data[i] = data[i+1];
        }
        size--;
        return entry;
    }

    /**
     * Add a new element to the list.
     */
    public void add(int value) {
        ensureCapacity(size + 1);  // Increments modCount!!
        data[size] = value;
        size++;
    }


    public void add(int... values) {
        int numNew = values.length;
        ensureCapacity(size + numNew);  // Increments modCount
        System.arraycopy(values, 0, data, size, numNew);
        size += numNew;
    }

    public void ensureCapacity(int minCapacity) {
        int oldCapacity = data.length;
        if (minCapacity > oldCapacity) {
           // int[] oldData = com.biorecorder.basechart.data;
            int newCapacity = (oldCapacity * 3)/2 + 1;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            // minCapacity is usually close to size, so this is a win:
            data = Arrays.copyOf(data, newCapacity);
        }
    }
}
