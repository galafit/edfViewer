package com.biorecorder.basecharts.axis;

import com.biorecorder.basecharts.graphics.BCanvas;
import com.biorecorder.basecharts.graphics.TextMetric;
import com.biorecorder.basecharts.scales.Scale;

/**
 * Created by galafit on 29/8/18.
 */
abstract class AxisHorizontal extends Axis {
    public AxisHorizontal(Scale scale, AxisConfig axisConfig) {
        super(scale, axisConfig);
    }

    @Override
    protected void translateCanvas(BCanvas canvas, int axisOriginPoint) {
        canvas.translate(0, axisOriginPoint);
    }

    @Override
    protected int labelSizeForWidth(TextMetric tm, int angle, String label) {
        return tm.height();
    }

    @Override
    protected int labelSizeForOverlap(TextMetric tm, int angle, String label) {
        return tm.stringWidth(label);
    }

    @Override
    protected void drawAxisLine(BCanvas canvas) {
        canvas.drawLine((int)getStart(), 0, (int)getEnd(), 0);
    }
}
