package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.sequence.IntSequence;


public class IntAverage extends IntSum {

    @Override
    protected int groupValue1() {
        return (int)(sum / count);
    }
}
