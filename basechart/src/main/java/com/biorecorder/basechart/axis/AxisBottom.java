package com.biorecorder.basechart.axis;


import com.biorecorder.basechart.graphics.*;
import com.biorecorder.basechart.scales.Scale;

/**
 * Created by galafit on 20/8/18.
 */
public class AxisBottom extends AxisHorizontal {

    public AxisBottom(Scale scale, AxisConfig axisConfig) {
        super(scale, axisConfig);
    }

    @Override
    protected Text tickToLabel(TextMetric tm, int tickPosition, String tickLabel, int charSize) {
        int axisWidth = config.getAxisLineStroke().getWidth();
        int labelPadding = config.getTickPadding();
        int space = 2;// px
        int charHalfWidth = charSize/2;

        if(config.isTickLabelOutside()) {
            int y = axisWidth / 2 + config.getTickMarkOutsideSize() + labelPadding;
            int x = tickPosition - charHalfWidth;
            if(x < getStart()) {
                x = (int)getStart() + space;
            }
            if(x + charSize * tickLabel.length() > getEnd()) {
                return new Text(tickLabel, (int)getEnd(), y, TextAnchor.END, TextAnchor.END, tm);
            }

            return new Text(tickLabel, x, y, TextAnchor.START, TextAnchor.END, tm);
        } else {
            int y = -axisWidth / 2 - labelPadding;
            int x = tickPosition + space;

            if(x + charSize * tickLabel.length() > getEnd()) {
                return new Text(tickLabel, (int)getEnd(), y, TextAnchor.END, TextAnchor.START, tm);
            }
            return new Text(tickLabel, x, y, TextAnchor.START, TextAnchor.START, tm);
        }
    }

    @Override
    protected int charSize(TextMetric tm) {
        return tm.stringWidth("0");
    }

    @Override
    protected void drawTickMark(BCanvas canvas, int tickPosition, int insideSize, int outsideSize) {
        int axisWidth = config.getAxisLineStroke().getWidth();
        int x = tickPosition;
        int y1 = axisWidth / 2 + outsideSize;
        int y2 = -axisWidth / 2 - insideSize;
        canvas.drawLine(x, y1, x, y2);
    }

    @Override
    protected void drawGridLine(BCanvas canvas, int tickPosition, BRectangle area) {
        int x = tickPosition;
        int y1 = 0;
        int y2 = -area.height;
        canvas.drawLine(x, y1, x, y2);
    }

    @Override
    protected Text createTitle(BCanvas canvas) {
        TextMetric tm = canvas.getTextMetric(config.getTitleTextStyle());
        int y = getWidth(canvas) - tm.height() / 2;
        int x = (int)(getEnd() + getStart()) / 2;
        return new Text(title, x, y, TextAnchor.MIDDLE, TextAnchor.MIDDLE, tm);
    }

    @Override
    protected void translateCanvas(BCanvas canvas, BRectangle area) {
        canvas.translate(0, area.y + area.height);
    }

}
