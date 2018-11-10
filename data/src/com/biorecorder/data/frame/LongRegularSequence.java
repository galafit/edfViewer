package com.biorecorder.data.frame;

import com.biorecorder.data.sequence.LongSequence;

public class LongRegularSequence implements LongSequence {
    protected long dataInterval;
    protected long startValue;
    protected long size = Long.MAX_VALUE;

    public LongRegularSequence(long startValue, long dataInterval, long size) {
        this.dataInterval = dataInterval;
        this.startValue = startValue;
        this.size = size;
    }

    public LongRegularSequence(long startValue, long dataInterval) {
        this(startValue, dataInterval, Long.MAX_VALUE);
    }

    public void size(long size) {
        this.size = size;
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public long get(long index) {
        rangeCheck(index);
        return (long)(startValue + dataInterval * index);
    }

    public long getDataInterval() {
        return dataInterval;
    }

    private void rangeCheck(long index) {
        if (index >= size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    private String outOfBoundsMsg(long index) {
        return "Index: "+index+", Size: "+size;
    }
}
