package com.biorecorder.data.aggregation.impl;


public class FloatAverage extends FloatSum {

    @Override
    protected float groupValue1() {
        return (float)(sum / count);
    }
}
