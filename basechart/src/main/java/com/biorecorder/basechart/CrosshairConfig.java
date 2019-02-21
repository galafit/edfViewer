package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.BColor;

/**
 * Created by galafit on 19/8/17.
 */
public class CrosshairConfig {
    private BColor lineColor = BColor.GRAY;
    private int lineWidth = 1;

    public CrosshairConfig() {
    }

    public CrosshairConfig(CrosshairConfig config) {
        lineColor = config.lineColor;
        lineWidth = config.lineWidth;
    }

    public BColor getLineColor() {
        return lineColor;
    }

    public void setLineColor(BColor lineColor) {
        this.lineColor = lineColor;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }
}
