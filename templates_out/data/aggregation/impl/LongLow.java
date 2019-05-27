package java.com.biorecorder.data.aggregation.impl;

import java.com.biorecorder.data.sequence.LongSequence;


public class LongLow extends LongAggregateFunction {
    private long min;

    @Override
    protected void addToGroup1(LongSequence sequence, long from, long length) {
        if(count == 0) {
            min = sequence.get(from);
        }
        long till = from + length;
        for (long i = from; i < till; i++) {
            min = (long) Math.min(min, sequence.get(i));
        }
        count += length;
    }


    @Override
    protected long groupValue1() {
        return min;
    }
}
