package java.com.biorecorder.data.aggregation.impl;

import java.com.biorecorder.data.sequence.IntSequence;


public class IntHigh extends IntAggregateFunction {
    private int max;

    @Override
    protected void addToGroup1(IntSequence sequence, long from, long length) {
        if(count == 0) {
            max = sequence.get(from);
        }
        long till = from + length;
        for (long i = from; i < till; i++) {
            max = (int) Math.max(max, sequence.get(i));
        }
        count += length;
    }

    @Override
    protected int groupValue1() {
        return max;
    }
}
