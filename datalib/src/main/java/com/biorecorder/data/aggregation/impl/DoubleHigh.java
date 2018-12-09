package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.sequence.DoubleSequence;


public class DoubleHigh extends DoubleAggregateFunction {
    private double max;

    @Override
    protected void addToGroup1(DoubleSequence sequence, long from, long length) {
        if(count == 0) {
            max = sequence.get(from);
        }
        long till = from + length;
        for (long i = from; i < till; i++) {
            max = (double) Math.max(max, sequence.get(i));
        }
        count += length;
    }

    @Override
    protected double groupValue1() {
        return max;
    }
}
