package java.com.biorecorder.data.sequence;

public class DoubleRegularSequence implements DoubleSequence {
    protected double dataInterval;
    protected double startValue;
    protected long size = Long.MAX_VALUE;

    public DoubleRegularSequence(double startValue, double dataInterval, long size) {
        this.dataInterval = dataInterval;
        this.startValue = startValue;
        this.size = size;
    }

    public DoubleRegularSequence(double startValue, double dataInterval) {
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
    public double get(long index) {
        rangeCheck(index);
        return (double)(startValue + dataInterval * index);
    }

    public double getDataInterval() {
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
