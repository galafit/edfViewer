package com.biorecorder.data.aggregation.impl;


public class LongAverage extends LongSum {

    @Override
    protected long groupValue1() {
        return (long)(sum / count);
    }
}
