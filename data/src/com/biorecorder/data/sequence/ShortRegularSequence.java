package com.biorecorder.data.sequence;

public class ShortRegularSequence implements ShortSequence {
    protected short dataInterval;
    protected short startValue;
    protected long size = Long.MAX_VALUE;

    public ShortRegularSequence(short startValue, short dataInterval, long size) {
        this.dataInterval = dataInterval;
        this.startValue = startValue;
        this.size = size;
    }

    public ShortRegularSequence(short startValue, short dataInterval) {
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
