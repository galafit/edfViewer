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
    public static Column createColumn(long start, long step) {
        return new LongRegularColumn(start, step);
    }
    public static Column createColumn(long start, long step, int size) {
        return new LongRegularColumn(start, step, size);
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

    public Column concat(Column column1, int column1Length, Column column2) {
        if(column1.dataType() == column2.dataType()) {
            switch (column1.dataType()) {
                case Integer:
                    return concat((IntColumn) column1, column1Length, (IntColumn) column1);
            }
        }
        DoubleSequence resultantSequence = new DoubleSequence() {
            @Override
            public int size() {
                return column1Length + column2.size();
            }

            @Override
            public double get(int index) {
                if(index < column1Length) {
                    return column1.value(index);
                } else {
                    return column2.value(index - column1Length);
                }
            }
        };
        return new DoubleColumn(resultantSequence);
    }

    private Column concat(IntColumn column1, int column1Length, IntColumn column2) {
        IntSequence resultantSequence = new IntSequence() {
            @Override
            public int size() {
                return column1Length + column2.size();
            }

            @Override
            public int get(int index) {
                if(index < column1Length) {
                    return column1.intValue(index);
                } else {
                    return column2.intValue(index - column1Length);
                }
            }
        };
        return new IntColumn(resultantSequence);
    }

}
