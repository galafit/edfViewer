package com.biorecorder.basechart.axis;

import com.biorecorder.basechart.graphics.BCanvas;

/**
 * Created by galafit on 20/8/18.
 */
public class AxisBottom extends Axis1 {


    @Override
    protected int getMaxTickLabelSize(BCanvas canvas) {
        // if tick rotation disabled
        return canvas.getTextMetric(config.getTickLabelTextStyle()).height();
    }
}
