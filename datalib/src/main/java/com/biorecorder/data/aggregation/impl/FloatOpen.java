package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.sequence.FloatSequence;


public class FloatOpen extends FloatAggregateFunction {
    private float first;


    @Override
    protected void addToGroup1(FloatSequence sequence, long from, long length) {
        if(count == 0) {
            first = sequence.get(from);
        }
        count +=length;
    }


    @Override
    protected float groupValue1() {
        return first;
    }
}
