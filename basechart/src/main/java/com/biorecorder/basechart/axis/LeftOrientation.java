package com.biorecorder.basechart.axis;


import com.biorecorder.basechart.graphics.BText;
import com.biorecorder.basechart.graphics.TextAnchor;
import com.biorecorder.basechart.graphics.TextMetric;
import com.biorecorder.basechart.graphics.*;

/**
 * Created by galafit on 22/8/18.
 */
class LeftOrientation extends VerticalOrientation {
    @Override
    protected int getLabelX(AxisConfig config) {
        if(config.isTickLabelOutside()) {
            return -config.getAxisLineWidth() / 2 - config.getTickMarkOutsideSize() - config.getTickPadding();
        } else {
            return config.getAxisLineWidth() / 2  + config.getTickPadding();
        }
    }

    @Override
    protected TextAnchor getLabelHTextAnchor(AxisConfig config) {
        if(config.isTickLabelOutside()) {
            return TextAnchor.END;
        } else {
            return TextAnchor.START;
        }
    }

    @Override
    public BLine createTickLine(int tickPosition, int axisLineWidth, int insideSize, int outsideSize) {
        int y = tickPosition;
        int x1 = -axisLineWidth / 2 - outsideSize;
        int x2 = axisLineWidth / 2 + insideSize;
        return new BLine(x1, y, x2, y);
    }

    @Override
    public BLine createGridLine(int tickPosition, BRectangle area) {
        int y = tickPosition;
        int x1 = 0;
        int x2 = area.width;
        return new BLine(x1, y, x2, y);
    }


    @Override
    public BText createTitle(String title, TextMetric tm, int start, int end, int width) {
        int x = -width;
        int y = (end + start) / 2;
        return new BText(title, x, y, TextAnchor.END, TextAnchor.MIDDLE, -90, tm);
    }

    @Override
    public void translateCanvas(BCanvas canvas, BRectangle area) {
        canvas.translate(area.x, 0);
    }
}
