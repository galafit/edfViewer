package com.biorecorder.basechart.chart.config.traces;

import com.biorecorder.basechart.chart.BColor;

/**
 * Created by galafit on 28/1/18.
 */
public class BooleanTraceConfig implements TraceConfig {
    private BColor color;

    @Override
    public BColor getColor() {
        return color;
    }

    @Override
    public void setColor(BColor color) {
        this.color = color;
    }
}
