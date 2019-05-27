package java.com.biorecorder.data.aggregation.impl;

import java.com.biorecorder.data.sequence.IntSequence;


public class IntCount extends IntAggregateFunction {

    @Override
    protected int groupValue1() {
        return (int) count;
    }
}
