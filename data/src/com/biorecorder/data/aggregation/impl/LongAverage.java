package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.sequence.LongSequence;


public class LongAverage extends LongSum {

    @Override
    protected long groupValue1() {
        return (long)(sum / count);
    }
}
