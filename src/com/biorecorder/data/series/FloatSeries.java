package com.biorecorder.data.series;

/**
 * Interface that represents a set of indexed data of type float (like array)
 * that can be accessed but can not be modified
 */
public interface FloatSeries {
    public long size();
    public float get(long index);
}
