package com.biorecorder.basechart.themes;

import com.biorecorder.basechart.ChartConfig;
import com.biorecorder.basechart.ScrollConfig;

/**
 * Created by galafit on 31/8/18.
 */
public abstract class Theme {
    protected ChartConfig chartConfig = new ChartConfig();
    protected ChartConfig previewConfig = new ChartConfig();
    protected ScrollConfig scrollConfig = new ScrollConfig();

    public ChartConfig getChartConfig() {
        return chartConfig;
    }

    public ChartConfig getPreviewConfig() {
        return previewConfig;
    }

    public ScrollConfig getScrollConfig() {
        return scrollConfig;
    }
}
