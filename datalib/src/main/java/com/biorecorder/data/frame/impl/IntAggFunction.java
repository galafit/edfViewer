package com.biorecorder.data.frame.impl;

import com.biorecorder.data.sequence.IntSequence;

/**
 * Created by galafit on 16/1/19.
 */
abstract class  IntAggFunction {
    protected int count;

    public abstract int add(IntSequence sequence, int from, int length);

    /**
     * get value without checkIfEmpty
     */
    protected abstract int getValue1();

    public int getValue() {
        checkIfEmpty();
        return getValue1();
    }

    public int getN() {
        return count;
    }

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
