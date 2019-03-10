package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.aggregation.IntAggFunction;
import com.biorecorder.data.sequence.IntSequence;

/**
 * Created by galafit on 9/3/19.
 */
public class IntAverage implements IntAggFunction {
    protected int count;
    private int sum;

    @Override
    public int add(IntSequence sequence, int from, int length) {
        for (int i = 0; i < length; i++) {
           sum += sequence.get(from + i);
        }
        count +=length;
        return count;
    }

    @Override
    public int getValue() {
        checkIfEmpty();
        return sum / count;
    }

    @Override
    public int getN() {
        return count;
    }

    @Override
    public void reset() {
        count = 0;
        sum = 0;
    }

    private void checkIfEmpty() {
        if(count == 0) {
            String errMsg = "No elements was added to group. Grouping function can not be calculated.";
            throw new IllegalStateException(errMsg);
        }
    }

}
