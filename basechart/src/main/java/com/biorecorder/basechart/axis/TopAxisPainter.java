package com.biorecorder.basechart.axis;

import com.biorecorder.basechart.graphics.BText;
import com.biorecorder.basechart.graphics.TextAnchor;
import com.biorecorder.basechart.graphics.TextMetric;
import com.biorecorder.basechart.graphics.*;

/**
 * Created by galafit on 30/8/18.
 */
class TopAxisPainter extends HorizontalAxisPainter {
    @Override
    protected int getLabelY(AxisConfig config) {
        if(config.isTickLabelOutside()) {
            return  -config.getAxisLineWidth() / 2 - config.getTickMarkOutsideSize() - config.getTickPadding();
        } else {
            return config.getAxisLineWidth() / 2  + config.getTickPadding();
        }
    }

    @Override
    protected TextAnchor getLabelVTextAnchor(AxisConfig config) {
        if(config.isTickLabelOutside()) {
            return TextAnchor.START;
        } else {
            return TextAnchor.END;
        }
    }

    @Override
    public void drawTickMark(BCanvas canvas, int tickPosition, int axisLineWidth, int insideSize, int outsideSize) {
        int x = tickPosition;
        int y1 = -axisLineWidth / 2 - outsideSize;
        int y2 = axisLineWidth / 2 + insideSize;
        canvas.drawLine(x, y1, x, y2);
    }

    @Override
    public void drawGridLine(BCanvas canvas, int tickPosition, BRectangle area) {
        int x = tickPosition;
        int y1 = 0;
        int y2 = area.height;
        canvas.drawLine(x, y1, x, y2);
    }

    @Override
    public BText createTitle(BCanvas canvas, String title, int start, int end, int width, TextStyle textStyle) {
        TextMetric tm = canvas.getTextMetric(textStyle);
        int y = - width + tm.height();
        int x = (end + start) / 2;
        return new BText(title, x, y, TextAnchor.MIDDLE, TextAnchor.START, tm);
    }

    @Override
    public void translateCanvas(BCanvas canvas, BRectangle area) {
        canvas.translate(0, area.y);
    }
}
