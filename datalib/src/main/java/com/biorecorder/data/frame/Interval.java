package com.biorecorder.data.frame;

/**
 * Created by galafit on 28/4/19.
 */
public interface Interval {
    public void goContaining(double value);
    public void goNext();
    public void goPrevious();
}
