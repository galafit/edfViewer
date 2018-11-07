package com.biorecorder.data.grouping.function;


public class LongGroupOpen extends LongGroupFunction {
    private long first;

    @Override
    protected void add1(long value) {
        if(count == 0) {
            first = value;
        }
        count++;
    }

    @Override
    protected long groupValue1() {
        return first;
    }
}
