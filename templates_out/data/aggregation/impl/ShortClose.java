package java.com.biorecorder.data.aggregation.impl;

import java.com.biorecorder.data.sequence.ShortSequence;


public class ShortClose extends ShortAggregateFunction {
    private short last;

    @Override
    protected void addToGroup1(ShortSequence sequence, long from, long length) {
        super.addToGroup1(sequence, from, length);
        last = sequence.get(from + length - 1);
    }

    @Override
    protected short groupValue1() {
        return last;
    }
}
