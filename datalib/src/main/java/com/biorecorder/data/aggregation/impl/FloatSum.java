package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.sequence.FloatSequence;


public class FloatSum extends FloatAggregateFunction {
    protected float sum;

    @Override
    public void reset() {
        super.reset();
        sum = 0;
    }

    @Override
    protected void addToGroup1(FloatSequence sequence, long from, long length) {
        super.addToGroup1(sequence, from, length);
        long till = from + length;
        for (long i = from; i < till; i++) {
            sum += sequence.get(i);
        }
    }

    @Override
    protected float groupValue1() {
        return (float) sum;
    }
}
