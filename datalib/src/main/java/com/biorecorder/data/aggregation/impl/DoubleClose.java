package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.sequence.DoubleSequence;


public class DoubleClose extends DoubleAggregateFunction {
    private double last;

    @Override
    protected void addToGroup1(DoubleSequence sequence, long from, long length) {
        super.addToGroup1(sequence, from, length);
        last = sequence.get(from + length - 1);
    }

    @Override
    protected double groupValue1() {
        return last;
    }
}
