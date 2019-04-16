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
abstract class AxisVertical extends Axis {

    @Override
    public int calculateWidth(BCanvas canvas) {
        int width = 0;
        width += config.getAxisLineStroke().getWidth() / 2;
        width += config.getTickMarkOutsideSize();

        if (config.isTickLabelOutside()) {
            if (isTicksDirty()) {
                createTicks(canvas);
            }
            String longestLabel = "";
            for (BText tickLabel : tickLabels) {
                if(tickLabel.getText().length() > longestLabel.length())  {
                    longestLabel = tickLabel.getText();
                }
            }
            if(longestLabel.length() > 0) {
                TextMetric tm = canvas.getTextMetric(config.getTickLabelTextStyle());
                width += config.getTickPadding() + tm.stringWidth(longestLabel);
            }
        }
        if (! StringUtils.isNullOrBlank(title)) {
            TextMetric tm = canvas.getTextMetric(config.getTitleTextStyle());
            width += config.getTitlePadding() + tm.height();
        }
        return width;
    }


    @Override
    protected BText tickToLabel(TextMetric tm, int tickPosition, String tickLabel, int tickPixelInterval) {
        int space = 2;// px
        int labelHeight = tm.ascent();
        int x = getLabelX();
        TextAnchor labelHAnchor = getLabelHTextAnchor();

        if(config.isTickLabelOutside()) {
            int y = tickPosition;
            if(y + labelHeight/2 + 1 > getStart()) {
                y = (int)getStart() - space;
                return new BText(tickLabel, x, y, labelHAnchor, TextAnchor.START, tm);
            }
            if(y - labelHeight/2 - 1 < getEnd()) {
                y = (int)getEnd();
                return new BText(tickLabel, x, y, labelHAnchor, TextAnchor.END, tm);
            }
            return new BText(tickLabel, x, y, labelHAnchor, TextAnchor.MIDDLE, tm);

        } else {
            int y = tickPosition;

            if(y - labelHeight - 1 < getEnd()) {
                //y = (int)getEnd();
                return new BText(tickLabel, x, y, labelHAnchor, TextAnchor.END , tm);
            }
            y = tickPosition - space;
            return new BText(tickLabel, x, y, labelHAnchor, TextAnchor.START, tm);
        }
    }

    protected abstract int getLabelX();

    protected abstract TextAnchor getLabelHTextAnchor();

    public AxisVertical(Scale scale, AxisConfig axisConfig) {
        super(scale, axisConfig);
    }

    @Override
    protected int labelSizeForOverlap(TextMetric tm, int angle, String label) {
        return tm.height();
    }

    @Override
    protected void drawAxisLine(BCanvas canvas) {
        canvas.drawLine(0, (int)getStart(), 0, (int)getEnd());
    }
}
