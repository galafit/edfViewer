package com.biorecorder.data.aggregation;

import com.biorecorder.data.sequence.IntSequence;

/**
 * Created by galafit on 16/1/19.
 */
public class IntFirst implements IntAggFunction {
    protected long count;
    private int first;

    @Override
    public long add(IntSequence sequence, long from, long length) {
        if(count == 0) {
            first = sequence.get(from);
        }
        count +=length;
        return count;
    }

    @Override
    public int getValue() {
        checkIfEmpty();
        return first;
    }

    @Override
    public long getN() {
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
