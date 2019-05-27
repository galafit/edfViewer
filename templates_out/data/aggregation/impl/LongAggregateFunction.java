package java.com.biorecorder.data.aggregation.impl;

import java.com.biorecorder.data.sequence.LongSequence;

/**
 * Created by galafit on 22/10/18.
 */
public abstract class LongAggregateFunction {
    protected long count;

    public final long addToGroup(LongSequence sequence, long from, long length) {
        addToGroup1(sequence, from, length);
        return groupValue();
    }

    public final long groupValue() {
       checkIfEmpty();
       return groupValue1();
    }

    public void reset() {
        count = 0;
    }

    protected abstract long groupValue1();
    
    protected void addToGroup1(LongSequence sequence, long from, long length) {
        count += length;
    }

    private void checkIfEmpty() {
        if(count == 0) {
            String errMsg = "No elements was added to groupByEqualIntervals. Grouping function can not be calculated.";
            throw new IllegalStateException(errMsg);
        }
    }
}
