package com.biorecorder.basechart.axis;

import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BText;
import com.biorecorder.basechart.graphics.TextAnchor;
import com.biorecorder.basechart.graphics.TextMetric;
import com.biorecorder.basechart.scales.Tick;

import java.util.List;

/**
 * Created by galafit on 29/8/18.
 */
abstract class HorizontalAxisPainter implements AxisPainter {
    @Override
    public int labelSizeForWidth(TextMetric tm,  List<Tick> ticks) {
        return tm.height();
    }

    @Override
    public BText tickToLabel(TextMetric tm, int tickPosition, String tickLabel, int start, int end, int tickPixelInterval, AxisConfig config, int interLabelGap, boolean isCategory) {
        int charSize = tm.stringWidth("0");
        int space = 2;// px
        int charHalfWidth = charSize / 2;
        int labelSize = tm.stringWidth(tickLabel);
        TextAnchor labelVAnchor = getLabelVTextAnchor(config);
        int y = getLabelY(config);
        int x;
        int labelShift = -charHalfWidth;
        if(config.isTickLabelCentered() || isCategory) {
            labelShift = -(labelSize / 2);
        }
        if (config.isTickLabelOutside()) {
            x = tickPosition + labelShift;
            if (x < start) {
                int x1 = tickPosition + tickPixelInterval -  labelSize - interLabelGap + labelShift;
                int x2 = start;
                x = Math.max(x + labelShift, Math.min(x1, x2));
            }

            if (x + labelSize > end) {
                int x1 = tickPosition - tickPixelInterval + labelShift + 2 * labelSize + interLabelGap;
                int x2 =  end;
                x = Math.min(x + labelSize, Math.max(x1, x2));
                return new BText(tickLabel, x, y, TextAnchor.END, labelVAnchor, tm);
            }
        } else {
            x = tickPosition + space;
        }


        return new BText(tickLabel, x, y, TextAnchor.START, labelVAnchor, tm);

    }

    @Override
    public int labelSizeForOverlap(TextMetric tm, List<Tick> ticks) {
        String maxLabel = "";
        for (Tick tick : ticks) {
            if (tick.getLabel().length() > maxLabel.length()) {
                maxLabel = tick.getLabel();
            }
        }
        return tm.stringWidth(maxLabel);
    }

    @Override
    public void drawAxisLine(BCanvas canvas, int start, int end) {
        canvas.drawLine(start, 0, end, 0);
    }

    @Override
    public boolean contains(int point, int start, int end) {
        return point <= end && point >= start;
    }

    protected abstract int getLabelY(AxisConfig config);

    protected abstract TextAnchor getLabelVTextAnchor(AxisConfig config);
}
