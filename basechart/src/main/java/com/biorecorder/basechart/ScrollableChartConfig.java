package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.graphics.HorizontalAlign;
import com.biorecorder.basechart.scroll.ScrollConfig;

/**
 * Created by galafit on 31/8/18.
 */
public class ScrollableChartConfig {
    protected ChartConfig chartConfig;
    protected ChartConfig previewConfig;
    protected ScrollConfig scrollConfig;

    public ScrollableChartConfig() {
        BColor previewBgColor = BColor.BLACK; //new BColor(25, 25, 30); //new BColor(28, 25, 28);
        BColor previewMarginColor = previewBgColor;

        BColor scrollColor = new BColor(245, 226, 208);

        chartConfig = new ChartConfig();
        previewConfig = new ChartConfig();
        scrollConfig = new ScrollConfig();

        chartConfig.setMarginColor(chartConfig.getBackgroundColor());
        chartConfig.getRightAxisConfig().setTickLabelOutside(false);
        chartConfig.getLeftAxisConfig().setTickLabelOutside(false);
        chartConfig.setLeftAxisPrimary(false);

        previewConfig.getRightAxisConfig().setTickLabelOutside(false);
        previewConfig.getLeftAxisConfig().setTickLabelOutside(false);
        previewConfig.setLeftAxisPrimary(false);
        previewConfig.setBackgroundColor(previewBgColor);
        previewConfig.setMarginColor(previewMarginColor);
        previewConfig.setDefaultStackWeight(2);
        previewConfig.getLegendConfig().setBackgroundColor(previewBgColor);

        scrollConfig.setColor(scrollColor);
    }

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
