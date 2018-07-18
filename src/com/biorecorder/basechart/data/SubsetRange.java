package com.biorecorder.basechart.data;


/**
 * Created by galafit on 13/7/18.
 */
public class SubsetRange {
    private long startIndex;
    private long length;

    public SubsetRange(long startIndex, long length) {
        this.startIndex = startIndex;
        this.length = length;
    }

    public long getStartIndex() {
        return startIndex;
    }

    public long getLength() {
        return length;
    }
}
