package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.scroll.ScrollConfig;

/**
 * Created by galafit on 31/8/18.
 */
public class ScrollableChartConfig {
    private BColor backgroundColor = BColor.RED;
    private int gap = 10; // between Chart and Preview px
    private Insets spacing = new Insets(0);
    private int previewHeightMin = 30; // px

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

    public int getPreviewHeightMin() {
        return previewHeightMin;
    }

    public void setPreviewHeightMin(int previewHeightMin) {
        this.previewHeightMin = previewHeightMin;
    }
}
