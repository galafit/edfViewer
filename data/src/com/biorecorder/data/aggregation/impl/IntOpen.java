package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.sequence.IntSequence;


public class IntOpen extends IntAggregateFunction {
    private int first;


    @Override
    protected void addToGroup1(IntSequence sequence, long from, long length) {
        if(count == 0) {
            first = sequence.get(from);
        }
        count +=length;
    }


    @Override
    protected int groupValue1() {
        return first;
    }
}
