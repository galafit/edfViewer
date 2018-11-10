package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.sequence.FloatSequence;


public class FloatCount extends FloatAggregateFunction {

    @Override
    protected float groupValue1() {
        return (float) count;
    }
}
