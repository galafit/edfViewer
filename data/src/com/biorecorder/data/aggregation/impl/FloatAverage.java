package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.sequence.FloatSequence;


public class FloatAverage extends FloatSum {

    @Override
    protected float groupValue1() {
        return (float)(sum / count);
    }
}
