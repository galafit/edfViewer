package com.biorecorder.data.grouping.function;

import com.biorecorder.data.series.DoubleSeries;

/**
 * Created by galafit on 22/10/18.
 */
public abstract class DoubleGroupFunction {
    protected long count;

    public double addToGroup(DoubleSeries series, long from, long length) {
        for (int i = 0; i < length; i++) {
           add1(series.get(from + i));
        }
        return groupValue();
    }

    public void reset() {
        count = 0;
    }

    public double groupValue() {
        if(count == 0) {
            String errMsg = "No elements was added to group. Grouping function can not be calculated.";
            throw new IllegalStateException(errMsg);
        }
        return groupValue1();
    }

    protected void add1(double value) {
       count++;
    }

    protected abstract double groupValue1();
}
