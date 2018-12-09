package com.biorecorder.data.aggregation.impl;


public class FloatCount extends FloatAggregateFunction {

    @Override
    protected float groupValue1() {
        return (float) count;
    }
}
