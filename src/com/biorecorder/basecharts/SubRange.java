package com.biorecorder.basecharts;


/**
 * Created by galafit on 13/7/18.
 */
public class SubRange {
    private long startIndex;
    private long size;

    public SubRange(long startIndex, long size) {
        this.startIndex = startIndex;
        if(this.startIndex < 0) {
            this.startIndex = 0;
        }
        this.size = size;
    }

    public long getStartIndex() {
        return startIndex;
    }

    public long getSize() {
        return size;
    }
}
