package com.biorecorder.basechart.grouping;

import com.biorecorder.util.series.FloatSeries;

/**
 * Created by galafit on 22/10/18.
 */
public abstract class FloatGroupingFunction {
    protected long count;

    public float[] addToGroup(FloatSeries series, long from, long length) {
        for (int i = 0; i < length; i++) {
           add1(series.get(from + i));
        }
        return groupedValue();
    }

    public void reset() {
        count = 0;
    }

    public float[] groupedValue() {
        if(count == 0) {
            String errMsg = "No elements was added to group. Grouping function can not be calculated.";
            throw new IllegalStateException(errMsg);
        }
        return groupedValue1();
    }

    protected void add1(float value) {
       count++;
    }

    protected abstract float[] groupedValue1();
}
