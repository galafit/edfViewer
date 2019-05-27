package java.com.biorecorder.data.aggregation.impl;

import java.com.biorecorder.data.sequence.ShortSequence;

/**
 * Created by galafit on 22/10/18.
 */
public abstract class ShortAggregateFunction {
    protected long count;

    public final short addToGroup(ShortSequence sequence, long from, long length) {
        addToGroup1(sequence, from, length);
        return groupValue();
    }

    public final short groupValue() {
       checkIfEmpty();
       return groupValue1();
    }

    public void reset() {
        count = 0;
    }

    protected abstract short groupValue1();
    
    protected void addToGroup1(ShortSequence sequence, long from, long length) {
        count += length;
    }

    private void checkIfEmpty() {
        if(count == 0) {
            String errMsg = "No elements was added to groupByEqualIntervals. Grouping function can not be calculated.";
            throw new IllegalStateException(errMsg);
        }
    }
}
