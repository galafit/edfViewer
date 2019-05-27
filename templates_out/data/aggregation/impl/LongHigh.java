package java.com.biorecorder.data.aggregation.impl;

import java.com.biorecorder.data.sequence.LongSequence;


public class LongHigh extends LongAggregateFunction {
    private long max;

    @Override
    protected void addToGroup1(LongSequence sequence, long from, long length) {
        if(count == 0) {
            max = sequence.get(from);
        }
        long till = from + length;
        for (long i = from; i < till; i++) {
            max = (long) Math.max(max, sequence.get(i));
        }
        count += length;
    }

    @Override
    protected long groupValue1() {
        return max;
    }
}
