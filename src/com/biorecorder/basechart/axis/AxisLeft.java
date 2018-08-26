package com.biorecorder.basechart.axis;

import com.biorecorder.basechart.graphics.TextMetric;
import com.biorecorder.basechart.scales.Tick;

/**
 * Created by galafit on 22/8/18.
 */
public class AxisLeft extends Axis1 {

    @Override
    protected int getLabelsSizeForWidth(TextMetric tm, int angle) {
        if (config.isTickLabelsVisible() && !config.isTickLabelInside()) {
            Tick minTick;
            Tick maxTick;
            if(tickProvider == null) {
                tickProvider = getAndConfigTickProvider();
            }
            // simplified algorithm assuming that the biggest tick size ae on the axis edges
            // (it is reasonable for all axis except the category one that at the moment not used)
            if(config.isMinMaxRoundingEnabled()) {

                // if ticks overlaps we may need to add ticks on the edges to have
                // number of ticks multiple to ticks skip step (to skip every n-th tick)
                int maxTicksCount = TICKS_DEFAULT_COUNT + TICKS_DEFAULT_COUNT / 2;
                int maxNumberAdditionalTicks = (int) (maxTicksCount * TICKS_ROUNDING_UNCERTAINTY) + 1;

                minTick = tickProvider.getLowerTick(getMin());
                for (int i = 1; i < maxNumberAdditionalTicks; i++) {
                    minTick = tickProvider.getPreviousTick();
                }

                maxTick = tickProvider.getUpperTick(getMax());
                for (int i = 1; i < maxNumberAdditionalTicks; i++) {
                    maxTick = tickProvider.getNextTick();
                }

            } else {
                minTick = tickProvider.getUpperTick(getMin());
                maxTick = tickProvider.getLowerTick(getMax());
            }
    }

    @Override
    protected int getInterLabelSpaceToAvoidOverlap(TextMetric tm, int angle, String label1, String label2) {
        return 0;
    }
}
