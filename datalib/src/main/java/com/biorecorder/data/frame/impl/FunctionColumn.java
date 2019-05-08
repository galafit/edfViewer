package com.biorecorder.data.frame.impl;


import com.biorecorder.data.frame.Column;
import com.biorecorder.data.frame.Function;
import com.biorecorder.data.sequence.DoubleSequence;


/**
 * Created by galafit on 11/3/19.
 */
public class FunctionColumn extends DoubleColumn {
    private Column argColumn;
    private Function function;
    public FunctionColumn(Function function, Column argColumn) {
        super(new DoubleSequence() {
            @Override
            public int size() {
                return argColumn.size();
            }

            @Override
            public double get(int index) {
                return function.apply(argColumn.value(index));
            }
        });
        this.function = function;
        this.argColumn = argColumn;
    }

    @Override
    public Column view(int from, int length) {
        return new FunctionColumn(function, argColumn.view(from, length));
    }

    @Override
    public Column view(int[] order) {
        return new FunctionColumn(function, argColumn.view(order));
    }
}
