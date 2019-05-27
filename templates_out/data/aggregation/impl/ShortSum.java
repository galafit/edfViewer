package java.com.biorecorder.data.aggregation.impl;

import java.com.biorecorder.data.sequence.ShortSequence;


public class ShortSum extends ShortAggregateFunction {
    protected short sum;

    @Override
    public void reset() {
        super.reset();
        sum = 0;
    }

    @Override
    protected void addToGroup1(ShortSequence sequence, long from, long length) {
        super.addToGroup1(sequence, from, length);
        long till = from + length;
        for (long i = from; i < till; i++) {
            sum += sequence.get(i);
        }
    }

    @Override
    protected short groupValue1() {
        return (short) sum;
    }
}
