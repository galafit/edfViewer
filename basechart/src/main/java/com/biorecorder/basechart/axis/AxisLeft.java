package com.biorecorder.basechart.axis;


import com.biorecorder.basechart.graphics.BText;
import com.biorecorder.basechart.graphics.TextAnchor;
import com.biorecorder.basechart.graphics.TextMetric;
import com.biorecorder.basechart.graphics.*;
import com.biorecorder.basechart.scales.Scale;

/**
 * Created by galafit on 22/8/18.
 */
public class AxisLeft extends AxisVertical {

    public AxisLeft(Scale scale, AxisConfig axisConfig) {
        super(scale, axisConfig);
    }

    @Override
    protected int getLabelX() {
        if(config.isTickLabelOutside()) {
            return -config.getAxisLineStroke().getWidth() / 2 - config.getTickMarkOutsideSize() - config.getTickPadding();
        } else {
            return config.getAxisLineStroke().getWidth() / 2  + config.getTickPadding();
        }
    }

    @Override
    protected TextAnchor getLabelHTextAnchor() {
        if(config.isTickLabelOutside()) {
            return TextAnchor.END;
        } else {
            return TextAnchor.START;
        }
    }

    @Override
    protected void drawTickMark(BCanvas canvas, int tickPosition, int insideSize, int outsideSize) {
        int axisWidth = config.getAxisLineStroke().getWidth();
        int y = tickPosition;
        int x1 = -axisWidth / 2 - outsideSize;
        int x2 = axisWidth / 2 + insideSize;
        canvas.drawLine(x1, y, x2, y);
    }

    @Override
    protected void drawGridLine(BCanvas canvas, int tickPosition, BRectangle area) {
        int y = tickPosition;
        int x1 = 0;
        int x2 = area.width;
        canvas.drawLine(x1, y, x2, y);
    }


    @Override
    protected BText createTitle(BCanvas canvas) {
        TextMetric tm = canvas.getTextMetric(config.getTitleTextStyle());
        int x = -getWidth(canvas);
        int y = (int)(getEnd() + getStart()) / 2;
        return new BText(title, x, y, TextAnchor.END, TextAnchor.MIDDLE, -90, tm);
    }

    @Override
    protected void translateCanvas(BCanvas canvas, BRectangle area) {
        canvas.translate(area.x, 0);
    }
}
