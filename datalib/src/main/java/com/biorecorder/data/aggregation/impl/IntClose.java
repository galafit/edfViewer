package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.sequence.IntSequence;


public class IntClose extends IntAggregateFunction {
    private int last;

    @Override
    protected void addToGroup1(IntSequence sequence, long from, long length) {
        super.addToGroup1(sequence, from, length);
        last = sequence.get(from + length - 1);
    }

    @Override
    protected int groupValue1() {
        return last;
    }
}
