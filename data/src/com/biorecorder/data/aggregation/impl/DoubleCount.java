package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.sequence.DoubleSequence;


public class DoubleCount extends DoubleAggregateFunction {

    @Override
    protected double groupValue1() {
        return (double) count;
    }
}
