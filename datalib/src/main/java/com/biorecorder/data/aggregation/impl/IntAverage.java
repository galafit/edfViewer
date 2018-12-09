package com.biorecorder.data.aggregation.impl;


public class IntAverage extends IntSum {

    @Override
    protected int groupValue1() {
        return (int)(sum / count);
    }
}
