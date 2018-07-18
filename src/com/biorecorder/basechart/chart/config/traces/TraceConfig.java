package com.biorecorder.basechart.chart.config.traces;

import com.biorecorder.basechart.chart.BColor;

/**
 * Created by galafit on 13/9/17.
 */
public interface TraceConfig {
    public BColor getColor();
    public void setColor(BColor color);
    public int getMarkSize();
}
