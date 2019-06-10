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
abstract class VerticalAxisPainter implements AxisPainter {
    @Override
    public int labelSizeForWidth(TextMetric tm,  List<Tick> ticks) {
        String longestLabel = "";
        for (Tick tick : ticks) {
            if(tick.getLabel().length() > longestLabel.length()) {
                longestLabel = tick.getLabel();
            }
        }
        return tm.stringWidth(longestLabel);
    }

    @Override
    public BText tickToLabel(TextMetric tm, int tickPosition, String tickLabel, int start, int end, int tickPixelInterval, AxisConfig config, int interLabelGap, boolean isCategory) {
        int space = 2;// px
        int labelHeight = tm.ascent();
        int x = getLabelX(config);
        TextAnchor labelHAnchor = getLabelHTextAnchor(config);

        if(config.isTickLabelOutside()) {
            int y = tickPosition;
            if(y + labelHeight/2 + 1 > start) {
                y = start - space;
                return new BText(tickLabel, x, y, labelHAnchor, TextAnchor.START, tm);
            }
            if(y - labelHeight/2 - 1 < end) {
                y = end;
                return new BText(tickLabel, x, y, labelHAnchor, TextAnchor.END, tm);
            }
            return new BText(tickLabel, x, y, labelHAnchor, TextAnchor.MIDDLE, tm);

        } else {
            int y = tickPosition;

            if(y - labelHeight - 1 < end) {
                //y = (int)getEnd();
                return new BText(tickLabel, x, y, labelHAnchor, TextAnchor.END , tm);
            }
            y = tickPosition - space;
            return new BText(tickLabel, x, y, labelHAnchor, TextAnchor.START, tm);
        }
    }

    @Override
    public int labelSizeForOverlap(TextMetric tm, List<Tick> ticks) {
        return tm.height();
    }

    @Override
    public void drawAxisLine(BCanvas canvas, int start, int end) {
        canvas.drawLine(0, start, 0, end);
    }

    @Override
    public boolean contains(int point, int start, int end) {
        return point <= start && point >= end;
    }

    protected abstract int getLabelX(AxisConfig config);
    protected abstract TextAnchor getLabelHTextAnchor(AxisConfig config);
}
