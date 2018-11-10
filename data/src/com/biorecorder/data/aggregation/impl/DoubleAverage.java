package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.sequence.DoubleSequence;


public class DoubleAverage extends DoubleSum {

    @Override
    protected double groupValue1() {
        return (double)(sum / count);
    }
}
