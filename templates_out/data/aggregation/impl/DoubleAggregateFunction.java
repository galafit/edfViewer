package java.com.biorecorder.data.aggregation.impl;

import java.com.biorecorder.data.sequence.DoubleSequence;

/**
 * Created by galafit on 22/10/18.
 */
public abstract class DoubleAggregateFunction {
    protected long count;

    public final double addToGroup(DoubleSequence sequence, long from, long length) {
        addToGroup1(sequence, from, length);
        return groupValue();
    }

    public final double groupValue() {
       checkIfEmpty();
       return groupValue1();
    }

    public void reset() {
        count = 0;
    }

    protected abstract double groupValue1();
    
    protected void addToGroup1(DoubleSequence sequence, long from, long length) {
        count += length;
    }

    private void checkIfEmpty() {
        if(count == 0) {
            String errMsg = "No elements was added to groupByEqualIntervals. Grouping function can not be calculated.";
            throw new IllegalStateException(errMsg);
        }
    }
}
