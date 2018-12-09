package com.biorecorder.data.aggregation.impl;


public class LongCount extends LongAggregateFunction {

    @Override
    protected long groupValue1() {
        return (long) count;
    }
}
