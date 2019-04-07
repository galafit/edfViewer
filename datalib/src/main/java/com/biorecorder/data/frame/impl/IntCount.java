package com.biorecorder.data.frame.impl;

import com.biorecorder.data.sequence.IntSequence;

/**
 * Created by galafit on 6/4/19.
 */
class IntCount extends IntAggFunction {

    @Override
    public int add(IntSequence sequence, int from, int length) {
        count +=length;
        return count;
    }

    @Override
    protected int getValue1() {
        return count;
    }
}
