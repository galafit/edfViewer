package com.biorecorder.data.aggregation.impl;


public class ShortCount extends ShortAggregateFunction {

    @Override
    protected short groupValue1() {
        return (short) count;
    }
}
