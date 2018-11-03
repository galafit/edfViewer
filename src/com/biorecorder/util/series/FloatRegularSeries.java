package com.biorecorder.util.series;

public class FloatRegularSeries implements FloatSeries {
    protected float dataInterval;
    protected float startValue;
    protected long size = Long.MAX_VALUE;

    public FloatRegularSeries(float dataInterval, float startValue, long size) {
        this.dataInterval = dataInterval;
        this.startValue = startValue;
        this.size = size;
    }

    public FloatRegularSeries(float dataInterval, float startValue) {
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
    public float get(long index) {
        rangeCheck(index);
        return (float)(startValue + dataInterval * index);
    }

    public float getDataInterval() {
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
