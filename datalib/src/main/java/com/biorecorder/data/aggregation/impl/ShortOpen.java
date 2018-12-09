package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.sequence.ShortSequence;


public class ShortOpen extends ShortAggregateFunction {
    private short first;


    @Override
    protected void addToGroup1(ShortSequence sequence, long from, long length) {
        if(count == 0) {
            first = sequence.get(from);
        }
        count +=length;
    }


    @Override
    protected short groupValue1() {
        return first;
    }
}
