package java.com.biorecorder.data.aggregation.impl;

import java.com.biorecorder.data.sequence.IntSequence;


public class IntAverage extends IntSum {

    @Override
    protected int groupValue1() {
        return (int)(sum / count);
    }
}
