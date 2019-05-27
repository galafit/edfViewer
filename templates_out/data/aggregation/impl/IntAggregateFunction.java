package java.com.biorecorder.data.aggregation.impl;

import java.com.biorecorder.data.sequence.IntSequence;

/**
 * Created by galafit on 22/10/18.
 */
public abstract class IntAggregateFunction {
    protected long count;

    public final int addToGroup(IntSequence sequence, long from, long length) {
        addToGroup1(sequence, from, length);
        return groupValue();
    }

    public final int groupValue() {
       checkIfEmpty();
       return groupValue1();
    }

    public void reset() {
        count = 0;
    }

    protected abstract int groupValue1();
    
    protected void addToGroup1(IntSequence sequence, long from, long length) {
        count += length;
    }

    private void checkIfEmpty() {
        if(count == 0) {
            String errMsg = "No elements was added to groupByEqualIntervals. Grouping function can not be calculated.";
            throw new IllegalStateException(errMsg);
        }
    }
}
