package java.com.biorecorder.data.aggregation.impl;

import java.com.biorecorder.data.sequence.DoubleSequence;


public class DoubleOpen extends DoubleAggregateFunction {
    private double first;


    @Override
    protected void addToGroup1(DoubleSequence sequence, long from, long length) {
        if(count == 0) {
            first = sequence.get(from);
        }
        count +=length;
    }


    @Override
    protected double groupValue1() {
        return first;
    }
}
