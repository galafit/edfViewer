package com.biorecorder.data.frame;

/**
 * Created by galafit on 11/3/19.
 */
public class SquareFunction implements Function {
    @Override
    public int apply(double value) {
        return (int)(value * value);
    }
}
