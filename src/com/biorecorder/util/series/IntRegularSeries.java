package com.biorecorder.util.series;

public class IntRegularSeries implements IntSeries {
    protected int dataInterval;
    protected int startValue;
    protected long size = Long.MAX_VALUE;

    public IntRegularSeries(int dataInterval, int startValue, long size) {
        this.dataInterval = dataInterval;
        this.startValue = startValue;
        this.size = size;
    }

    public IntRegularSeries(int dataInterval, int startValue) {
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
    public int get(long index) {
        rangeCheck(index);
        return (int)(startValue + dataInterval * index);
    }

    public int getDataInterval() {
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
