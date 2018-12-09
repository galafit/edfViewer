package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.sequence.FloatSequence;


public class FloatClose extends FloatAggregateFunction {
    private float last;

    @Override
    protected void addToGroup1(FloatSequence sequence, long from, long length) {
        super.addToGroup1(sequence, from, length);
        last = sequence.get(from + length - 1);
    }

    @Override
    protected float groupValue1() {
        return last;
    }
}
