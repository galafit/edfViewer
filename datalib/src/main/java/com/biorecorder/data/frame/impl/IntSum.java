package com.biorecorder.data.frame.impl;

import com.biorecorder.data.sequence.IntSequence;
import com.biorecorder.data.utils.PrimitiveUtils;

/**
 * Created by galafit on 5/4/19.
 */
class IntSum extends IntAggFunction {
    private long sum;

    @Override
    public int add(IntSequence sequence, int from, int length) {
        if(count == 0) {
            sum = 0;
        }
        for (int i = 0; i < length; i++) {
            sum += sequence.get(from + i);
        }
        count +=length;
        return count;
    }

    @Override
    protected int getValue1() {
        return PrimitiveUtils.longToInt(sum);
    }
}
