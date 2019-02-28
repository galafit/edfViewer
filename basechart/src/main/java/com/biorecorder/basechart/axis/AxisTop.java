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
    protected BText tickToLabel(TextMetric tm, int tickPosition, String tickLabel, int tickPixelInterval) {
        int charSize = tm.stringWidth("0");
        int axisWidth = config.getAxisLineStroke().getWidth();
        int labelPadding = config.getTickPadding();
        int space = 2;// px
        int charHalfWidth = charSize/2;
        int labelSize = charSize * tickLabel.length();
        if(config.isTickLabelOutside()) {
            int x = tickPosition - charHalfWidth;
            int y = -axisWidth / 2 - config.getTickMarkOutsideSize() - labelPadding;

            if(x < getStart()) {
                x = (int)getStart();
            }

            if(x + charSize * tickLabel.length() > getEnd()) {
                int x1 = x - (tickPixelInterval - 2 * labelSize);
                int x2 = (int)getEnd();
                x = Math.max(x1, x2);
                return new BText(tickLabel, x , y, TextAnchor.END, TextAnchor.START, tm);
            }
            return new BText(tickLabel, x, y, TextAnchor.START, TextAnchor.START, tm);

        } else {
            int y = axisWidth / 2  + labelPadding;
            int x = tickPosition + space;
            if(x + charSize * tickLabel.length() > getEnd()) {
                int x1 = x - (tickPixelInterval - 2 * labelSize);
                int x2 = (int)getEnd();
                x = Math.max(x1, x2);
                return new BText(tickLabel, x, y, TextAnchor.END, TextAnchor.END, tm);
            }
            return new BText(tickLabel, x, y, TextAnchor.START, TextAnchor.END, tm);
        }
    }


    @Override
    protected void drawTickMark(BCanvas canvas, int tickPosition, int insideSize, int outsideSize) {
        int axisWidth = config.getAxisLineStroke().getWidth();
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
