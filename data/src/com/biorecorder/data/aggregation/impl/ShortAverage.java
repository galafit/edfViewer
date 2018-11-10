package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.sequence.ShortSequence;


public class ShortAverage extends ShortSum {

    @Override
    protected short groupValue1() {
        return (short)(sum / count);
    }
}
