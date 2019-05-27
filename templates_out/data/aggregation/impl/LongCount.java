package java.com.biorecorder.data.aggregation.impl;

import java.com.biorecorder.data.sequence.LongSequence;


public class LongCount extends LongAggregateFunction {

    @Override
    protected long groupValue1() {
        return (long) count;
    }
}
