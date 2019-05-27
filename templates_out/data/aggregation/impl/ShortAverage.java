package java.com.biorecorder.data.aggregation.impl;

import java.com.biorecorder.data.sequence.ShortSequence;


public class ShortAverage extends ShortSum {

    @Override
    protected short groupValue1() {
        return (short)(sum / count);
    }
}
