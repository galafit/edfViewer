package com.biorecorder.basechart.themes;

import com.biorecorder.basechart.ScrollableChartConfig;
import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.scroll.ScrollConfig;
import com.biorecorder.basechart.themes.ChartConfigWhite;

/**
 * Created by galafit on 23/2/19.
 */
public class ScrollableChartConfigWhite extends ScrollableChartConfig {
    public ScrollableChartConfigWhite() {
        BColor previewBgColor = new BColor(230, 230, 230);
        BColor previewMarginColor = previewBgColor;

        BColor scrollColor = new BColor(30, 30, 30);;

        chartConfig = new ChartConfigWhite();
        previewConfig = new ChartConfigWhite();
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
}
