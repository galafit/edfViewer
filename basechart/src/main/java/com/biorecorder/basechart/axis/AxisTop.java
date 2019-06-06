package com.biorecorder.basechart.axis;

import com.biorecorder.basechart.graphics.BText;
import com.biorecorder.basechart.graphics.TextAnchor;
import com.biorecorder.basechart.graphics.TextMetric;
import com.biorecorder.basechart.graphics.*;
import com.biorecorder.basechart.scales.Scale;

/**
 * Created by galafit on 30/8/18.
 */
public class AxisTop extends AxisHorizontal {

    public AxisTop(Scale scale, AxisConfig axisConfig) {
        super(scale, axisConfig);
    }

    @Override
    protected int getLabelY() {
        if(config.isTickLabelOutside()) {
            return  -config.getAxisLineWidth() / 2 - config.getTickMarkOutsideSize() - config.getTickPadding();
        } else {
            return config.getAxisLineWidth() / 2  + config.getTickPadding();
        }
    }

    @Override
    protected TextAnchor getLabelVTextAnchor() {
        if(config.isTickLabelOutside()) {
            return TextAnchor.START;
        } else {
            return TextAnchor.END;
        }
    }

    @Override
    protected void drawTickMark(BCanvas canvas, int tickPosition, int insideSize, int outsideSize) {
        int axisWidth = config.getAxisLineWidth();
        int x = tickPosition;
        int y1 = -axisWidth / 2 - outsideSize;
        int y2 = axisWidth / 2 + insideSize;
        canvas.drawLine(x, y1, x, y2);
    }

    @Override
    protected void drawGridLine(BCanvas canvas, int tickPosition, BRectangle area) {
        int x = tickPosition;
        int y1 = 0;
        int y2 = area.height;
        canvas.drawLine(x, y1, x, y2);
    }

    @Override
    protected BText createTitle(BCanvas canvas) {
        TextMetric tm = canvas.getTextMetric(config.getTitleTextStyle());
        int y = - getWidth(canvas) + tm.height();
        int x = (int)(getEnd() + getStart()) / 2;
        return new BText(title, x, y, TextAnchor.MIDDLE, TextAnchor.START, tm);
    }

    @Override
    protected void translateCanvas(BCanvas canvas, BRectangle area) {
        canvas.translate(0, area.y);
    }
}
