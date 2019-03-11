package com.biorecorder.data.frame;


import com.biorecorder.data.sequence.IntSequence;

/**
 * Created by galafit on 11/3/19.
 */
public class FunctionColumn extends IntColumn {
    private Column argColumn;
    private Function function;
    public FunctionColumn(Function function, Column argColumn) {
        super(new IntSequence() {
            @Override
            public int size() {
                return argColumn.size();
            }

            @Override
            public int get(int index) {
                return function.apply(argColumn.value(index));
            }
        });
        this.function = function;
        this.argColumn = argColumn;
    }

    public Column getArgColumn() {
        return argColumn;
    }

    public Function getFunction() {
        return function;
    }
}
