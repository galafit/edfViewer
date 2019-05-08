package com.biorecorder.data.frame.impl;

import com.biorecorder.data.frame.Column;
import com.biorecorder.data.frame.Function;
import com.biorecorder.data.sequence.*;

/**
 * Created by galafit on 7/5/19.
 */
public class ColumnFactory {
    public static Column createColumn(ShortSequence data) {
        return new ShortColumn(data);
    }
    public static Column createColumn(IntSequence data) {
        return new IntColumn(data);
    }
    public static Column createColumn(LongSequence data) {
        return new LongColumn(data);
    }
    public static Column createColumn(FloatSequence data) {
        return new FloatColumn(data);
    }
    public static Column createColumn(DoubleSequence data) {
        return new DoubleColumn(data);
    }
    public static Column createColumn(StringSequence data) {
        return new StringColumn(data);
    }

    public static Column createColumn(double start, double step) {
        return new DoubleRegularColumn(start, step);
    }

    public static Column createColumn(double start, double step, int size) {
        return new DoubleRegularColumn(start, step, size);
    }

    public static Column createColumn(Function function, Column argColumn) {
        return new FunctionColumn(function, argColumn);
    }

}
