package com.biorecorder.basechart.axis;

import com.biorecorder.basechart.graphics.TextMetric;
import com.biorecorder.basechart.scales.Tick;

/**
 * Created by galafit on 20/8/18.
 */
public class AxisBottom extends Axis1 {

    @Override
    protected int ticksLabelSizeForWidth(TextMetric tm, int angle) {
        if(angle == 0) {
            return tm.height();
        }
        if(ticks == null) {
            createTicks();
        }
    }

    @Override
    protected int ticksPixelIntervalToAvoidOverlap(TextMetric tm, int angle, String label1, String label2) {
        return 0;
    }
}
