package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.sequence.FloatSequence;

/**
 * Created by galafit on 22/10/18.
 */
public abstract class FloatAggregateFunction {
    protected long count;

    public final float addToGroup(FloatSequence sequence, long from, long length) {
        addToGroup1(sequence, from, length);
        return groupValue();
    }

    public final float groupValue() {
       checkIfEmpty();
       return groupValue1();
    }

    public void reset() {
        count = 0;
    }

    protected abstract float groupValue1();
    
    protected void addToGroup1(FloatSequence sequence, long from, long length) {
        count += length;
    }

    private void checkIfEmpty() {
        if(count == 0) {
            String errMsg = "No elements was added to group. Grouping function can not be calculated.";
            throw new IllegalStateException(errMsg);
        }
    }
}
