package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.sequence.IntSequence;


public class IntCount extends IntAggregateFunction {

    @Override
    protected int groupValue1() {
        return (int) count;
    }
}
