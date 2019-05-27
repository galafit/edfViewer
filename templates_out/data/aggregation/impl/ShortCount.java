package java.com.biorecorder.data.aggregation.impl;

import java.com.biorecorder.data.sequence.ShortSequence;


public class ShortCount extends ShortAggregateFunction {

    @Override
    protected short groupValue1() {
        return (short) count;
    }
}
