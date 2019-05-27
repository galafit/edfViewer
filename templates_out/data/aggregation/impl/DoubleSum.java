package java.com.biorecorder.data.aggregation.impl;

import java.com.biorecorder.data.sequence.DoubleSequence;


public class DoubleSum extends DoubleAggregateFunction {
    protected double sum;

    @Override
    public void reset() {
        super.reset();
        sum = 0;
    }

    @Override
    protected void addToGroup1(DoubleSequence sequence, long from, long length) {
        super.addToGroup1(sequence, from, length);
        long till = from + length;
        for (long i = from; i < till; i++) {
            sum += sequence.get(i);
        }
    }

    @Override
    protected double groupValue1() {
        return (double) sum;
    }
}
