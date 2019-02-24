package com.biorecorder.basechart.axis;

import com.biorecorder.basechart.graphics.BText;
import com.biorecorder.basechart.graphics.TextAnchor;
import com.biorecorder.basechart.graphics.TextMetric;
import com.biorecorder.basechart.graphics.*;
import com.biorecorder.basechart.scales.Scale;

/**
 * Created by galafit on 30/8/18.
 */
public class AxisRight extends AxisVertical {

    public AxisRight(Scale scale, AxisConfig axisConfig) {
        super(scale, axisConfig);
    }

    @Override
    protected boolean isRight() {
        return true;
    }

    @Override
    protected BText tickToLabel(TextMetric tm, int tickPosition, String tickLabel, int charSize) {
        int axisWidth = config.getAxisLineStroke().getWidth();
        int labelPadding = config.getTickPadding();
        int space = 1;// px
        int labelHeight = charSize;

        if (config.isTickLabelOutside()) {
            int y = tickPosition;
            int x = axisWidth / 2 + config.getTickMarkOutsideSize() + labelPadding;
            if (y + labelHeight / 2 + 1 > getStart()) {
                y = tickPosition - space;
                return new BText(tickLabel, x, y, TextAnchor.START, TextAnchor.START, tm);
            }
            if (y - labelHeight / 2 - 1 < getEnd()) {
                return new BText(tickLabel, x, y, TextAnchor.START, TextAnchor.END, tm);
            }
            return new BText(tickLabel, x, y, TextAnchor.START, TextAnchor.MIDDLE, tm);

        } else {
            int y = tickPosition;
            int x = -axisWidth / 2 - labelPadding;
            if (y - labelHeight / 2 - 1 < getEnd()) {
                return new BText(tickLabel, x, y, TextAnchor.END, TextAnchor.END, tm);
            }
            y = tickPosition - space;
            return new BText(tickLabel, x, y, TextAnchor.END, TextAnchor.START, tm);
        }
    }

    @Override
    protected int charSize(TextMetric tm) {
        return tm.ascent();
    }

    @Override
    protected void drawTickMark(BCanvas canvas, int tickPosition, int insideSize, int outsideSize) {
        int axisWidth = config.getAxisLineStroke().getWidth();
        int y = tickPosition;
        int x1 = axisWidth / 2 + outsideSize;
        int x2 = -axisWidth / 2 - insideSize;
        canvas.drawLine(x1, y, x2, y);
    }

    @Override
    protected void drawGridLine(BCanvas canvas, int tickPosition, BRectangle area) {
        int y = tickPosition;
        int x1 = 0;
        int x2 = -area.width;
        canvas.drawLine(x1, y, x2, y);
    }

    @Override
    protected BText createTitle(BCanvas canvas) {
        TextMetric tm = canvas.getTextMetric(config.getTitleTextStyle());
        int x = getWidth(canvas);
        int y = (int)(getEnd() + getStart()) / 2;
        return new BText(title, x, y, TextAnchor.END, TextAnchor.MIDDLE, +90, tm);
    }

    @Override
    protected void translateCanvas(BCanvas canvas, BRectangle area) {
        canvas.translate(area.x + area.width, 0);
    }
}
