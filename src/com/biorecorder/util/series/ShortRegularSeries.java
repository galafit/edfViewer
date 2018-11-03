package com.biorecorder.util.series;

public class ShortRegularSeries implements ShortSeries {
    protected short dataInterval;
    protected short startValue;
    protected long size = Long.MAX_VALUE;

    public ShortRegularSeries(short dataInterval, short startValue, long size) {
        this.dataInterval = dataInterval;
        this.startValue = startValue;
        this.size = size;
    }

    public ShortRegularSeries(short dataInterval, short startValue) {
        this(dataInterval, startValue, Long.MAX_VALUE);
    }

    public void size(long size) {
        this.size = size;
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public short get(long index) {
        rangeCheck(index);
        return (short)(startValue + dataInterval * index);
    }

    public short getDataInterval() {
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
