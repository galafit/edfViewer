package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.sequence.ShortSequence;


public class ShortCount extends ShortAggregateFunction {

    @Override
    protected short groupValue1() {
        return (short) count;
    }
}
