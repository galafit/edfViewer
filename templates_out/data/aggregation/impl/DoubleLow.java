package java.com.biorecorder.data.aggregation.impl;

import java.com.biorecorder.data.sequence.DoubleSequence;


public class DoubleLow extends DoubleAggregateFunction {
    private double min;

    @Override
    protected void addToGroup1(DoubleSequence sequence, long from, long length) {
        if(count == 0) {
            min = sequence.get(from);
        }
        long till = from + length;
        for (long i = from; i < till; i++) {
            min = (double) Math.min(min, sequence.get(i));
        }
        count += length;
    }


    @Override
    protected double groupValue1() {
        return min;
    }
}
