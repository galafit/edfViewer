package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.sequence.LongSequence;


public class LongClose extends LongAggregateFunction {
    private long last;

    @Override
    protected void addToGroup1(LongSequence sequence, long from, long length) {
        super.addToGroup1(sequence, from, length);
        last = sequence.get(from + length - 1);
    }

    @Override
    protected long groupValue1() {
        return last;
    }
}
