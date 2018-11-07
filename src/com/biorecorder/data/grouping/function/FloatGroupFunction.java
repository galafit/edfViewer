package com.biorecorder.data.grouping.function;

import com.biorecorder.data.series.FloatSeries;

/**
 * Created by galafit on 22/10/18.
 */
public abstract class FloatGroupFunction {
    protected long count;

    public float addToGroup(FloatSeries series, long from, long length) {
        for (int i = 0; i < length; i++) {
           add1(series.get(from + i));
        }
        return groupValue();
    }

    public void reset() {
        count = 0;
    }

    public float groupValue() {
        if(count == 0) {
            String errMsg = "No elements was added to group. Grouping function can not be calculated.";
            throw new IllegalStateException(errMsg);
        }
        return groupValue1();
    }

    protected void add1(float value) {
       count++;
    }

    protected abstract float groupValue1();
}
