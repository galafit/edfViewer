package com.biorecorder.data.frame.impl;

import com.biorecorder.data.sequence.IntSequence;

/**
 * Created by galafit on 16/1/19.
 */
class IntFirst extends IntAggFunction {
    private int first;

    @Override
    public int add(IntSequence sequence, int from, int length) {
        if(count == 0) {
            first = sequence.get(from);
        }
        count +=length;
        return count;
    }

    @Override
    protected int getValue1() {
        return first;
    }
}
