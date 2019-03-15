package com.biorecorder.basechart.axis;

import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BText;
import com.biorecorder.basechart.graphics.TextAnchor;
import com.biorecorder.basechart.graphics.TextMetric;
import com.biorecorder.basechart.scales.Scale;
import com.biorecorder.basechart.utils.StringUtils;

/**
 * Created by galafit on 29/8/18.
 */
abstract class AxisHorizontal extends Axis {
    public AxisHorizontal(Scale scale, AxisConfig axisConfig) {
        super(scale, axisConfig);
    }

    @Override
    public int calculateWidth(BCanvas canvas) {
        int width = 0;
        width += config.getAxisLineStroke().getWidth() / 2;
        width += config.getTickMarkOutsideSize();

        if (config.isTickLabelOutside()) {
            TextMetric tm = canvas.getTextMetric(config.getTickLabelTextStyle());
            width += config.getTickPadding() + tm.height();

        }
        if (! StringUtils.isNullOrBlank(title)) {
            TextMetric tm = canvas.getTextMetric(config.getTitleTextStyle());
            width += config.getTitlePadding() + tm.height();
        }
        return width;
    }

    @Override
    protected BText tickToLabel(TextMetric tm, int tickPosition, String tickLabel, int tickPixelInterval) {
        int charSize = tm.stringWidth("0");
        int space = 2;// px
        int charHalfWidth = charSize/2;
        int labelSize = tm.stringWidth(tickLabel);
        TextAnchor labelVAnchor = getLabelVTextAnchor();
        int y = getLabelY();
        int x;
        if(config.isTickLabelOutside()) {
            x = tickPosition - charHalfWidth;
            if(x < getStart()) {
                x = (int)getStart();
            }
        } else {
            x = tickPosition + space;
        }

        if(x + labelSize > getEnd()) {
            int x1 = x - (tickPixelInterval - 2 * labelSize - 2 * charSize);
            int x2 = (int)getEnd();
            x = Math.max(x1, x2);
            return new BText(tickLabel, x , y, TextAnchor.END, labelVAnchor, tm);
        }
        return new BText(tickLabel, x, y, TextAnchor.START, labelVAnchor, tm);

    }

    protected abstract int getLabelY();

    protected abstract TextAnchor getLabelVTextAnchor();

    @Override
    protected int labelSizeForOverlap(TextMetric tm, int angle, String label) {
        return tm.stringWidth(label);
    }

    @Override
    protected void drawAxisLine(BCanvas canvas) {
        canvas.drawLine((int)getStart(), 0, (int)getEnd(), 0);
    }
}
