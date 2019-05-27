package java.com.biorecorder.data.aggregation.impl;

import java.com.biorecorder.data.sequence.LongSequence;


public class LongAverage extends LongSum {

    @Override
    protected long groupValue1() {
        return (long)(sum / count);
    }
}
