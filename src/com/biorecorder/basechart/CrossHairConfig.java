package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.BColor;

/**
 * Created by galafit on 19/8/17.
 */
public class CrossHairConfig {
    private BColor lineColor = BColor.RED;
    private int lineWidth = 1;

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
