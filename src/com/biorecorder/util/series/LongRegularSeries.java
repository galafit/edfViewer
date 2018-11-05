package com.biorecorder.util.series;

import com.biorecorder.basechart.data.RegularColumn;

public class LongRegularSeries implements LongSeries{
    protected long dataInterval;
    protected long startValue;
    protected long size = Long.MAX_VALUE;

    public LongRegularSeries(long startValue, long dataInterval, long size) {
        if(dataInterval <= 0) {
            throw new IllegalArgumentException("Data interval = "+dataInterval + " Expected > 0");
        }
        this.dataInterval = dataInterval;
        this.startValue = startValue;
        this.size = size;
    }

    public LongRegularSeries(long startValue, long dataInterval) {
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
        return startValue + dataInterval * index;
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
