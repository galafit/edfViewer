package com.biorecorder.basechart.axis;


import com.biorecorder.basechart.graphics.Text;
import com.biorecorder.basechart.graphics.TextAnchor;
import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.TextMetric;
import com.biorecorder.basechart.scales.Scale;

/**
 * Created by galafit on 20/8/18.
 */
public class AxisBottom extends AxisHorizontal {

    public AxisBottom(Scale scale, AxisConfig axisConfig) {
        super(scale, axisConfig);
    }

    @Override
    protected Text tickToLabel(TextMetric tm, int tickPosition, String tickLabel) {
        int axisWidth = config.getAxisLineStroke().getWidth();
        int labelPadding = config.getTickPadding();
        int space = 2;// px
        int charHalfWidth = tm.stringWidth("0")/2;

        if(isTickLabelInside) {
            int x = tickPosition + space;
            int y = -axisWidth / 2 - labelPadding;
            return new Text(tickLabel, x, y, TextAnchor.START, TextAnchor.START, tm);

        } else {
            int x = tickPosition - charHalfWidth;
            if(x < getStart()) {
                x = (int)getStart() + space;
            }
            int y = axisWidth / 2 + config.getTickMarkOutsideSize() + labelPadding;
            return new Text(tickLabel, x, y, TextAnchor.START, TextAnchor.END, tm);
        }
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
    protected void drawGridLine(BCanvas canvas, int tickPosition, int length) {
        int x = tickPosition;
        int y1 = 0;
        int y2 = -length;
        canvas.drawLine(x, y1, x, y2);
    }


    @Override
    protected Text createTitle(BCanvas canvas) {
        TextMetric tm = canvas.getTextMetric(config.getTitleTextStyle());
        int y = getWidth(canvas) - tm.height() / 2;
        int x = (int)(getEnd() + getStart()) / 2;
        return new Text(title, x, y, TextAnchor.MIDDLE, TextAnchor.MIDDLE, tm);
    }
}