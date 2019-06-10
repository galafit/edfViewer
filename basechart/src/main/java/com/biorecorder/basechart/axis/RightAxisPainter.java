package com.biorecorder.basechart.axis;

import com.biorecorder.basechart.graphics.BText;
import com.biorecorder.basechart.graphics.TextAnchor;
import com.biorecorder.basechart.graphics.TextMetric;
import com.biorecorder.basechart.graphics.*;

/**
 * Created by galafit on 30/8/18.
 */
class RightAxisPainter extends VerticalAxisPainter {
    @Override
    protected int getLabelX(AxisConfig config) {
        if(config.isTickLabelOutside()) {
            return config.getAxisLineWidth() / 2 + config.getTickMarkOutsideSize() + config.getTickPadding();
        } else {
            return -config.getAxisLineWidth() / 2  - config.getTickPadding();
        }
    }

    @Override
    protected TextAnchor getLabelHTextAnchor(AxisConfig config) {
        if(config.isTickLabelOutside()) {
            return TextAnchor.START;
        } else {
            return TextAnchor.END;
        }
    }

    @Override
    public void drawTickMark(BCanvas canvas, int tickPosition, int axisLineWidth, int insideSize, int outsideSize) {
        int y = tickPosition;
        int x1 = axisLineWidth / 2 + outsideSize;
        int x2 = -axisLineWidth / 2 - insideSize;
        canvas.drawLine(x1, y, x2, y);
    }

    @Override
    public void drawGridLine(BCanvas canvas, int tickPosition, BRectangle area) {
        int y = tickPosition;
        int x1 = 0;
        int x2 = -area.width;
        canvas.drawLine(x1, y, x2, y);
    }

    @Override
    public BText createTitle(BCanvas canvas, String title, int start, int end, int width, TextStyle textStyle) {
        TextMetric tm = canvas.getTextMetric(textStyle);
        int x = width;
        int y = (end + start) / 2;
        return new BText(title, x, y, TextAnchor.END, TextAnchor.MIDDLE, +90, tm);
    }

    @Override
    public void translateCanvas(BCanvas canvas, BRectangle area) {
        canvas.translate(area.x + area.width, 0);
    }
}
