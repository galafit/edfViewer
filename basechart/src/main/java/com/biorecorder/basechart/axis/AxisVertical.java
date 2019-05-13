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

import java.util.List;


/**
 * Created by galafit on 29/8/18.
 */
abstract class AxisVertical extends Axis {

    @Override
    protected int labelSizeForWidth(TextMetric tm) {
        if(ticks == null) {
            createTicks(tm);
        }
        String longestLabel = "";
        for (Tick tick : ticks) {
            if(tick.getLabel().length() > longestLabel.length()) {
                longestLabel = tick.getLabel();
            }
        }
        return tm.stringWidth(longestLabel);
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
    protected int labelSizeForOverlap(TextMetric tm, List<Tick> ticks) {
        return tm.height();
    }

    @Override
    protected void drawAxisLine(BCanvas canvas) {
        canvas.drawLine(0, (int)getStart(), 0, (int)getEnd());
    }

    @Override
    protected boolean contains(int point) {
        return point >= Math.round(getEnd()) && point <= Math.round(getStart());
    }

    @Override
    public double getBestExtent(BCanvas canvas, int length) {
        if (scale instanceof CategoryScale) {
            TextMetric tm = canvas.getTextMetric(config.getTickLabelTextStyle());
            StringSequence labels = ((CategoryScale) scale).getLabels();

            if(labels!= null && labels.size() > 0) {
                int bestLength = labels.size() * tm.height() + getInterLabelGap() * (labels.size() - 1);
                bestLength = Math.max(bestLength, length);
                Scale s = new CategoryScale(labels);
                s.setDomain(0, labels.size());
                s.setRange(0, bestLength);
                return s.invert(length);
            }
        }
        return -1;
    }
}
