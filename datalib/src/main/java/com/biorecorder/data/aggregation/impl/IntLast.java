package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.aggregation.IntAggFunction;
import com.biorecorder.data.sequence.IntSequence;

/**
 * Created by galafit on 5/4/19.
 */
public class IntLast implements IntAggFunction {
    protected int count;
    private int last;

    @Override
    public int add(IntSequence sequence, int from, int length) {
        last = sequence.get(from + length);
        count +=length;
        return count;
    }

    @Override
    public int getValue() {
        checkIfEmpty();
        return last;
    }

    @Override
    public int getN() {
        return count;
    }

    @Override
    public void reset() {
        count = 0;
    }

    private void checkIfEmpty() {
        if(count == 0) {
            String errMsg = "No elements was added to group. Grouping function can not be calculated.";
            throw new IllegalStateException(errMsg);
        }
    }
}
