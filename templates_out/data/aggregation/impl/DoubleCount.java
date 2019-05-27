package java.com.biorecorder.data.aggregation.impl;

import java.com.biorecorder.data.sequence.DoubleSequence;


public class DoubleCount extends DoubleAggregateFunction {

    @Override
    protected double groupValue1() {
        return (double) count;
    }
}
