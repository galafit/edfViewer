package com.biorecorder.basecharts.traces;

import com.biorecorder.basecharts.graphics.BColor;

/**
 * Created by galafit on 13/9/17.
 */
public interface TraceConfig {
    public BColor getColor();
    public void setColor(BColor color);
    public int getMarkSize();
}
