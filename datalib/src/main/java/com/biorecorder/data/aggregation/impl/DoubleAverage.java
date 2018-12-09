package com.biorecorder.data.aggregation.impl;


public class DoubleAverage extends DoubleSum {

    @Override
    protected double groupValue1() {
        return (double)(sum / count);
    }
}
