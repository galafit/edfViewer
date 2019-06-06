package com.biorecorder.basechart.axis;

import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BText;
import com.biorecorder.basechart.graphics.TextAnchor;
import com.biorecorder.basechart.graphics.TextMetric;
import com.biorecorder.basechart.scales.CategoryScale;
import com.biorecorder.basechart.scales.Scale;
import com.biorecorder.basechart.scales.Tick;
import com.biorecorder.basechart.utils.StringUtils;
import com.biorecorder.data.sequence.StringSequence;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by galafit on 29/8/18.
 */
abstract class AxisHorizontal extends Axis {
    public AxisHorizontal(Scale scale, AxisConfig axisConfig) {
        super(scale, axisConfig);
    }

    @Override
    protected int labelSizeForWidth(TextMetric tm) {
        return tm.height();
    }

    @Override
    protected BText tickToLabel(TextMetric tm, int tickPosition, String tickLabel, int tickPixelInterval) {
        int charSize = tm.stringWidth("0");
        int space = 2;// px
        int charHalfWidth = charSize / 2;
        int labelSize = tm.stringWidth(tickLabel);
        TextAnchor labelVAnchor = getLabelVTextAnchor();
        int y = getLabelY();
        int x;
        int labelShift = -charHalfWidth;
        if(config.isTickLabelCentered() || scale instanceof CategoryScale) {
            labelShift = -(labelSize / 2);
        }
        if (config.isTickLabelOutside()) {
            x = tickPosition + labelShift;
            if (x < getStart()) {
                int x1 = tickPosition + tickPixelInterval -  labelSize - getInterLabelGap() + labelShift;
                int x2 = (int) getStart();
                x = Math.max(x + labelShift, Math.min(x1, x2));
            }

            if (x + labelSize > getEnd()) {
                int x1 = tickPosition - tickPixelInterval + labelShift + 2 * labelSize + getInterLabelGap();
                int x2 = (int) getEnd();
                x = Math.min(x + labelSize, Math.max(x1, x2));
                return new BText(tickLabel, x, y, TextAnchor.END, labelVAnchor, tm);
            }
        } else {
            x = tickPosition + space;
        }


        return new BText(tickLabel, x, y, TextAnchor.START, labelVAnchor, tm);

    }

    protected abstract int getLabelY();

    protected abstract TextAnchor getLabelVTextAnchor();

    @Override
    protected int labelSizeForOverlap(TextMetric tm, List<Tick> ticks) {
        String maxLabel = "";
        for (Tick tick : ticks) {
            if (tick.getLabel().length() > maxLabel.length()) {
                maxLabel = tick.getLabel();
            }
        }
        return tm.stringWidth(maxLabel);
    }

    @Override
    protected void drawAxisLine(BCanvas canvas) {
        canvas.drawLine((int) getStart(), 0, (int) getEnd(), 0);
    }

    @Override
    protected boolean contains(int point) {
        return point <= Math.round(getEnd()) && point >= Math.round(getStart());
    }

}
