package com.biorecorder.data.aggregation.impl;


public class DoubleCount extends DoubleAggregateFunction {

    @Override
    protected double groupValue1() {
        return (double) count;
    }
}
