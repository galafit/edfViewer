package com.biorecorder.basechart.traces;

import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.graphics.BStroke;

/**
 * Created by galafit on 11/10/17.
 */
public class LineTraceConfig {
    public static final int LINEAR = 0;
    public static final int STEP = 1;
    public static final int VERTICAL_LINES = 2;

    private BStroke lineStroke = new BStroke();
    private int mode;
    private boolean isFilled = false;
    private int markSize = 10 ; // point rowCount
    private BColor[] curveColors;

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

    public BColor[] getCurveColors() {
        return curveColors;
    }

    public void setCurveColors(BColor[] curveColors) {
        this.curveColors = curveColors;
    }

    public int getMarkSize() {
        return markSize;
    }

    public void setMarkSize(int markSize) {
        this.markSize = markSize;
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
}
