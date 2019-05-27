package java.com.biorecorder.data.aggregation.impl;

import java.com.biorecorder.data.sequence.ShortSequence;


public class ShortLow extends ShortAggregateFunction {
    private short min;

    @Override
    protected void addToGroup1(ShortSequence sequence, long from, long length) {
        if(count == 0) {
            min = sequence.get(from);
        }
        long till = from + length;
        for (long i = from; i < till; i++) {
            min = (short) Math.min(min, sequence.get(i));
        }
        count += length;
    }


    @Override
    protected short groupValue1() {
        return min;
    }
}
