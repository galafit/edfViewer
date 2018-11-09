package com.biorecorder.basecharts.themes;

import com.biorecorder.basecharts.ChartConfig;
import com.biorecorder.basecharts.ScrollConfig;

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
