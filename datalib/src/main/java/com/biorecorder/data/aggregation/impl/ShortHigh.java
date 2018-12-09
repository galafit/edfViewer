package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.sequence.ShortSequence;


public class ShortHigh extends ShortAggregateFunction {
    private short max;

    @Override
    protected void addToGroup1(ShortSequence sequence, long from, long length) {
        if(count == 0) {
            max = sequence.get(from);
        }
        long till = from + length;
        for (long i = from; i < till; i++) {
            max = (short) Math.max(max, sequence.get(i));
        }
        count += length;
    }

    @Override
    protected short groupValue1() {
        return max;
    }
}
