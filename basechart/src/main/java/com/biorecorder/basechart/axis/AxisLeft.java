package com.biorecorder.basechart.axis;


import com.biorecorder.basechart.graphics.Text;
import com.biorecorder.basechart.graphics.TextAnchor;
import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.TextMetric;
import com.biorecorder.basechart.scales.Scale;

/**
 * Created by galafit on 22/8/18.
 */
public class AxisLeft extends AxisVertical {

    public AxisLeft(Scale scale, AxisConfig axisConfig) {
        super(scale, axisConfig);
    }

    @Override
    protected Text tickToLabel(TextMetric tm, int tickPosition, String tickLabel, int charSize) {
        int axisWidth = config.getAxisLineStroke().getWidth();
        int labelPadding = config.getTickPadding();
        int space = 2;// px
        int labelHeight = charSize;

        if(config.isTickLabelInside()) {
            int y = tickPosition - space;
            int x = axisWidth / 2  + labelPadding;
            if(y - labelHeight/2 - 1 < getEnd()) {
                y += space;
                return new Text(tickLabel, x, y, TextAnchor.START, TextAnchor.END, tm);
            }
            return new Text(tickLabel, x, y, TextAnchor.START, TextAnchor.START, tm);

        } else {
            int y = tickPosition;
            int x = -axisWidth / 2 - config.getTickMarkOutsideSize() - labelPadding;
            if(y + labelHeight/2 + 1 > getStart()) {
                return new Text(tickLabel, x, y, TextAnchor.END, TextAnchor.START, tm);
            }
            if(y - labelHeight/2 - 1 < getEnd()) {
                return new Text(tickLabel, x, y, TextAnchor.END, TextAnchor.END, tm);
            }
            return new Text(tickLabel, x, y, TextAnchor.END, TextAnchor.MIDDLE, tm);

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
        int x1 = -axisWidth / 2 - outsideSize;
        int x2 = axisWidth / 2 + insideSize;
        canvas.drawLine(x1, y, x2, y);
    }

    @Override
    protected void drawGridLine(BCanvas canvas, int tickPosition, int length) {
        int y = tickPosition;
        int x1 = 0;
        int x2 = length;
        canvas.drawLine(x1, y, x2, y);
    }

    @Override
    protected Text createTitle(BCanvas canvas) {
        TextMetric tm = canvas.getTextMetric(config.getTitleTextStyle());
        int x = -getWidth(canvas);
        int y = (int)(getEnd() + getStart()) / 2;
        return new Text(title, x, y, TextAnchor.END, TextAnchor.MIDDLE, -90, tm);
    }
}
