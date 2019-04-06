package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.aggregation.IntAggFunction;
import com.biorecorder.data.sequence.IntSequence;

/**
 * Created by galafit on 5/4/19.
 */
public class IntLast extends IntAggFunction {
    private int last;

    @Override
    public int add(IntSequence sequence, int from, int length) {
        last = sequence.get(from + length - 1);
        count +=length;
        return count;
    }

    @Override
    protected int getValue1() {
        return last;
    }
}
