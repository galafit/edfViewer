package com.biorecorder.basechart.axis;

import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.TextMetric;
import com.biorecorder.basechart.scales.Tick;

/**
 * Created by galafit on 22/8/18.
 */
public class AxisLeft extends Axis1 {

    @Override
    protected int getMaxTickLabelSize(BCanvas canvas) {
        Tick minTick, maxTick;
        if(tickProvider == null) {
            tickProvider = getAndConfigTickProvider();
        }
        // simplified algorithm assuming that the biggest tick size ae on the axis edges
        // (it is reasonable for all axis except the category one that at the moment not used)
        if(config.isMinMaxRoundingEnabled()) {

            // if ticks overlaps we may need to add ticks on the edges to have
            // number of ticks multiple to tick dividers (to skip every n-th tick)
            // And (max number of additional ticks) = (max tick divider - 1)

            minTick = tickProvider.getLowerTick(getMin());
            int maxTickDivider = AVAILABLE_TICKS_SKIP_STEPS[AVAILABLE_TICKS_SKIP_STEPS.length - 1];
            for (int i = 1; i < maxTickDivider; i++) {
                minTick = tickProvider.getPreviousTick();
            }

            maxTick = tickProvider.getUpperTick(getMax());
            for (int i = 1; i < maxTickDivider; i++) {
                maxTick = tickProvider.getNextTick();
            }

        } else {
            minTick = tickProvider.getUpperTick(getMin());
            maxTick = tickProvider.getLowerTick(getMax());
        }
        TextMetric tm = canvas.getTextMetric(config.getTickLabelTextStyle());
        return Math.max(tm.stringWidth(minTick.getLabel()), tm.stringWidth(maxTick.getLabel()));
    }
}
