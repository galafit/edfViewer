package com.biorecorder.data.aggregation;

import com.biorecorder.data.sequence.IntSequence;

/**
 * Created by galafit on 16/1/19.
 */
public interface  IntAggFunction {
    int add(IntSequence sequence, int from, int length);
    int getValue();
    int getN();
    void reset();
}
