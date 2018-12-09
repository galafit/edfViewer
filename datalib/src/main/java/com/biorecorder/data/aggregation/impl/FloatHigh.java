package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.sequence.FloatSequence;


public class FloatHigh extends FloatAggregateFunction {
    private float max;

    @Override
    protected void addToGroup1(FloatSequence sequence, long from, long length) {
        if(count == 0) {
            max = sequence.get(from);
        }
        long till = from + length;
        for (long i = from; i < till; i++) {
            max = (float) Math.max(max, sequence.get(i));
        }
        count += length;
    }

    @Override
    protected float groupValue1() {
        return max;
    }
}
