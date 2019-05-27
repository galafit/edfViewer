package java.com.biorecorder.data.aggregation.impl;

import java.com.biorecorder.data.sequence.FloatSequence;


public class FloatLow extends FloatAggregateFunction {
    private float min;

    @Override
    protected void addToGroup1(FloatSequence sequence, long from, long length) {
        if(count == 0) {
            min = sequence.get(from);
        }
        long till = from + length;
        for (long i = from; i < till; i++) {
            min = (float) Math.min(min, sequence.get(i));
        }
        count += length;
    }


    @Override
    protected float groupValue1() {
        return min;
    }
}
