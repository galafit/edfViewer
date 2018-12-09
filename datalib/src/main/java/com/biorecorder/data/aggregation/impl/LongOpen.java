package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.sequence.LongSequence;


public class LongOpen extends LongAggregateFunction {
    private long first;


    @Override
    protected void addToGroup1(LongSequence sequence, long from, long length) {
        if(count == 0) {
            first = sequence.get(from);
        }
        count +=length;
    }


    @Override
    protected long groupValue1() {
        return first;
    }
}
