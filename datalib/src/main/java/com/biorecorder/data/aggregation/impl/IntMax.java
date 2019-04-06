package com.biorecorder.data.aggregation.impl;

import com.biorecorder.data.aggregation.IntAggFunction;
import com.biorecorder.data.sequence.IntSequence;

public class IntMax extends IntAggFunction {
    private int max;

    @Override
    public int add(IntSequence sequence, int from, int length) {
        if(count == 0) {
            max = sequence.get(from);
        }
        for (int i = 0; i < length; i++) {
            max = Math.max(max, sequence.get(from + i));
        }
        count +=length;
        return count;
    }

    @Override
    protected int getValue1() {
        return max;
    }
}
