package com.biorecorder.basechart;

import com.biorecorder.basechart.axis.AxisConfig;
import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.scroll.ScrollConfig;

/**
 * Created by galafit on 31/8/18.
 */
public class NavigableChartConfig {
    private ChartConfig chartConfig;
    private ChartConfig navigatorConfig;
    private ScrollConfig scrollConfig;

    private BColor backgroundColor = BColor.BEIGE_WHITE;
    private int gap = 0; // between Chart and Preview px
    private Insets spacing = new Insets(0);
    private int navigatorHeightMin = 30; // px
    private boolean autoScrollEnabled = true;
    private boolean autoScaleEnabled = true; // chart Y auto scale during scrolling

    public NavigableChartConfig() {
        BColor navigatorBgColor = BColor.BEIGE_WHITE;
        BColor navigatorMarginColor = navigatorBgColor;

        chartConfig = new ChartConfig();
        navigatorConfig = new ChartConfig();
        scrollConfig = new ScrollConfig();

        chartConfig.getYAxisConfig().setTickLabelOutside(false);
        chartConfig.setLeftAxisPrimary(false);

        navigatorConfig.getYAxisConfig().setTickLabelOutside(false);
        navigatorConfig.setLeftAxisPrimary(false);
        navigatorConfig.setBackgroundColor(navigatorBgColor);
        navigatorConfig.setMarginColor(navigatorMarginColor);
        navigatorConfig.setStackGap(0);
        navigatorConfig.setDefaultStackWeight(2);
        navigatorConfig.getLegendConfig().setBackgroundColor(navigatorBgColor);
        navigatorConfig.getYAxisConfig().setRoundingEnabled(true);


        BColor scrollColor = chartConfig.getCrossHairConfig().getLineColor();
        scrollConfig.setColor(scrollColor);
    }


    public NavigableChartConfig(ChartConfig chartConfig, ChartConfig navigatorConfig, ScrollConfig scrollConfig) {
        this.chartConfig = chartConfig;
        this.navigatorConfig = navigatorConfig;
        this.scrollConfig = scrollConfig;
    }

    public NavigableChartConfig(NavigableChartConfig config) {
        chartConfig = new ChartConfig(config.chartConfig);
        navigatorConfig = new ChartConfig(config.navigatorConfig);
        scrollConfig = new ScrollConfig(config.scrollConfig);

        backgroundColor = config.backgroundColor;
        gap = config.gap;
        spacing = config.spacing;
        navigatorHeightMin = config.navigatorHeightMin;
        autoScrollEnabled = config.autoScrollEnabled;
        autoScaleEnabled = config.autoScaleEnabled;
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

    public boolean isAutoScrollEnabled() {
        return autoScrollEnabled;
    }

    public void setAutoScrollEnabled(boolean autoScrollEnabled) {
        this.autoScrollEnabled = autoScrollEnabled;
    }

    public boolean isAutoScaleEnabled() {
        return autoScaleEnabled;
    }

    public void setAutoScaleEnabled(boolean autoScaleEnabled) {
        this.autoScaleEnabled = autoScaleEnabled;
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
