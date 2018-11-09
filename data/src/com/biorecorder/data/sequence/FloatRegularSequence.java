package com.biorecorder.data.sequence;

public class FloatRegularSequence implements FloatSequence {
    protected float dataInterval;
    protected float startValue;
    protected long size = Long.MAX_VALUE;

    public FloatRegularSequence(float startValue, float dataInterval, long size) {
        this.dataInterval = dataInterval;
        this.startValue = startValue;
        this.size = size;
    }

    public FloatRegularSequence(float startValue, float dataInterval) {
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
