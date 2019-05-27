package java.com.biorecorder.data.aggregation.impl;

import java.com.biorecorder.data.sequence.DoubleSequence;


public class DoubleAverage extends DoubleSum {

    @Override
    protected double groupValue1() {
        return (double)(sum / count);
    }
}
