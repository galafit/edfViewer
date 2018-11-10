package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.sequence.LongSequence;


public class LongSum extends LongAggregateFunction {
    protected long sum;

    @Override
    public void reset() {
        super.reset();
        sum = 0;
    }

    @Override
    protected void addToGroup1(LongSequence sequence, long from, long length) {
        super.addToGroup1(sequence, from, length);
        long till = from + length;
        for (long i = from; i < till; i++) {
            sum += sequence.get(i);
        }
    }

    @Override
    protected long groupValue1() {
        return (long) sum;
    }
}
