package com.biorecorder.data.grouping.function;


public class IntGroupSum extends IntGroupFunction {
    private int sum;

    @Override
    public void reset() {
        super.reset();
        sum = 0;
    }

    @Override
    protected void add1(int value) {
        super.add1(value);
        sum += value;
    }

    @Override
    protected int groupValue1() {
        return sum;
    }
}
