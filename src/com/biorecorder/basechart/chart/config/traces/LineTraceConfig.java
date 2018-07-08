package com.biorecorder.basechart.chart.config.traces;

import com.biorecorder.basechart.chart.BColor;
import com.biorecorder.basechart.chart.BStroke;

/**
 * Created by galafit on 11/10/17.
 */
public class LineTraceConfig implements TraceConfig {
    public static final int LINEAR = 0;
    public static final int STEP = 1;
    public static final int VERTICAL_LINES = 2;

    private int markSize = 0; // point size
    private BStroke lineStroke = new BStroke();
    private BColor color;
    private int mode;
    private boolean isFilled = false;

    public LineTraceConfig() {
        this(LINEAR, false);
    }

    public LineTraceConfig(boolean isFilled) {
        this(LINEAR, isFilled);
    }

    public LineTraceConfig(int mode) {
      this(mode, false);
    }

    public LineTraceConfig(int mode, boolean isFilled) {
        this.mode = mode;
        this.isFilled = isFilled;
    }

    @Override
    public BColor getColor() {
        return color;
    }

    @Override
    public void setColor(BColor color) {
        this.color = color;
    }

    public int getMode() {
        return mode;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public BStroke getLineStroke() {
        return lineStroke;
    }

    public void setLineStroke(BStroke lineStroke) {
        this.lineStroke = lineStroke;
    }

    public int getMarkSize() {
        return markSize;
    }

    public void setMarkSize(int markSize) {
        this.markSize = markSize;
    }
}
