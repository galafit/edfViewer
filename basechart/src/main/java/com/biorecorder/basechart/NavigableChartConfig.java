package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.scroll.ScrollConfig;

/**
 * Created by galafit on 31/8/18.
 */
public class NavigableChartConfig {
    private BColor backgroundColor = BColor.WHITE;
    private int gap = 0; // between Chart and Preview px
    private Insets spacing = new Insets(0);
    private int navigatorHeightMin = 30; // px
    private ChartConfig chartConfig;
    private ChartConfig navigatorConfig;
    private ScrollConfig scrollConfig;

    public NavigableChartConfig() {
        BColor navigatorBgColor = BColor.BEIGE_WHITE;
        BColor navigatorMarginColor = navigatorBgColor;

        chartConfig = new ChartConfig();
        navigatorConfig = new ChartConfig();
        scrollConfig = new ScrollConfig();

        chartConfig.getyAxisConfig().setTickLabelOutside(false);
        chartConfig.setLeftAxisPrimary(false);

        navigatorConfig.getyAxisConfig().setTickLabelOutside(false);
        navigatorConfig.setLeftAxisPrimary(false);
        navigatorConfig.setBackgroundColor(navigatorBgColor);
        navigatorConfig.setMarginColor(navigatorMarginColor);
        navigatorConfig.setStackGap(0);
        navigatorConfig.setYAxisRoundingEnabled(true, ChartConfig.TICK_ACCURACY_IF_ROUNDING_ENABLED);
        navigatorConfig.setDefaultStackWeight(2);
        navigatorConfig.getLegendConfig().setBackgroundColor(navigatorBgColor);

        BColor scrollColor = chartConfig.getCrossHairConfig().getLineColor();
        scrollConfig.setColor(scrollColor);
    }


    public NavigableChartConfig(ChartConfig chartConfig, ChartConfig navigatorConfig, ScrollConfig scrollConfig) {
        this.chartConfig = chartConfig;
        this.navigatorConfig = navigatorConfig;
        this.scrollConfig = scrollConfig;
    }


    public ChartConfig getChartConfig() {
        return chartConfig;
    }

    public ChartConfig getNavigatorConfig() {
        return navigatorConfig;
    }

    public ScrollConfig getScrollConfig() {
        return scrollConfig;
    }

    public BColor getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(BColor backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getGap() {
        return gap;
    }

    public void setGap(int gap) {
        this.gap = gap;
    }

    public Insets getSpacing() {
        return spacing;
    }

    public void setSpacing(Insets spacing) {
        this.spacing = spacing;
    }

    public int getNavigatorHeightMin() {
        return navigatorHeightMin;
    }

    public void setNavigatorHeightMin(int navigatorHeightMin) {
        this.navigatorHeightMin = navigatorHeightMin;
    }
}
