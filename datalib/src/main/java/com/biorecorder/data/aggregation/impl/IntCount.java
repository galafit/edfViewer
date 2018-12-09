package com.biorecorder.data.aggregation.impl;


public class IntCount extends IntAggregateFunction {

    @Override
    protected int groupValue1() {
        return (int) count;
    }
}
