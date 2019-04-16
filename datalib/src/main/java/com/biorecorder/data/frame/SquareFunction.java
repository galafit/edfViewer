package com.biorecorder.data.frame;

import com.biorecorder.data.utils.PrimitiveUtils;

/**
 * Created by galafit on 11/3/19.
 */
public class SquareFunction implements Function {
    @Override
    public int apply(double value) {
        return PrimitiveUtils.doubleToInt(value * value);

    }
}
