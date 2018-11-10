package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.sequence.IntSequence;


public class IntLow extends IntAggregateFunction {
    private int min;

    @Override
    protected void addToGroup1(IntSequence sequence, long from, long length) {
        if(count == 0) {
            min = sequence.get(from);
        }
        long till = from + length;
        for (long i = from; i < till; i++) {
            min = (int) Math.min(min, sequence.get(i));
        }
        count += length;
    }


    @Override
    protected int groupValue1() {
        return min;
    }
}
