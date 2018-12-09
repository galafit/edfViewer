package com.biorecorder.data.aggregation.impl;


public class ShortAverage extends ShortSum {

    @Override
    protected short groupValue1() {
        return (short)(sum / count);
    }
}
