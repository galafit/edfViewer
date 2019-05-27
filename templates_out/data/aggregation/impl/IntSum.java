package java.com.biorecorder.data.aggregation.impl;

import java.com.biorecorder.data.sequence.IntSequence;


public class IntSum extends IntAggregateFunction {
    protected int sum;

    @Override
    public void reset() {
        super.reset();
        sum = 0;
    }

    @Override
    protected void addToGroup1(IntSequence sequence, long from, long length) {
        super.addToGroup1(sequence, from, length);
        long till = from + length;
        for (long i = from; i < till; i++) {
            sum += sequence.get(i);
        }
    }

    @Override
    protected int groupValue1() {
        return (int) sum;
    }
}
