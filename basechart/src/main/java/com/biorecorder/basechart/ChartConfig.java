package com.biorecorder.basechart;

import com.biorecorder.basechart.axis.AxisConfig;
import com.biorecorder.basechart.graphics.BColor;

/**
 * Created by galafit on 18/8/17.
 */
public class ChartConfig {
    private BColor[] traceColors = {BColor.MAGENTA, BColor.BLUE};

    private BColor backgroundColor;
    private BColor marginColor;

    private Insets spacing = new Insets(0, 0, 10, 10);
    private boolean isLeftAxisPrimary = true;
    private boolean isBottomAxisPrimary = true;


    private TitleConfig titleConfig = new TitleConfig();
    private LegendConfig legendConfig = new LegendConfig();
    private TooltipConfig tooltipConfig = new TooltipConfig();
    private CrossHairConfig crossHairConfig = new CrossHairConfig();

    private AxisConfig leftAxisConfig = new AxisConfig();
    private AxisConfig rightAxisConfig = new AxisConfig();
    private AxisConfig bottomAxisConfig = new AxisConfig();
    private AxisConfig topAxisConfig = new AxisConfig();

    public ChartConfig() {
    }

    public ChartConfig(ChartConfig chartConfig) {
        traceColors = chartConfig.traceColors;
        backgroundColor = chartConfig.backgroundColor;
        marginColor = chartConfig.marginColor;
        titleConfig = new TitleConfig(chartConfig.titleConfig);
        legendConfig = new LegendConfig(chartConfig.legendConfig);
        tooltipConfig = chartConfig.tooltipConfig;
        crossHairConfig = chartConfig.crossHairConfig;
        leftAxisConfig = new AxisConfig(chartConfig.leftAxisConfig);
        rightAxisConfig = new AxisConfig(chartConfig.rightAxisConfig);
        topAxisConfig = new AxisConfig(chartConfig.topAxisConfig);
        bottomAxisConfig = new AxisConfig(chartConfig.bottomAxisConfig);
    }

    public TitleConfig getTitleConfig() {
        return titleConfig;
    }

    public BColor[] getTraceColors() {
        return traceColors;
    }

    public void setTraceColors(BColor[] traceColors) {
        this.traceColors = traceColors;
    }

    public BColor getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(BColor backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public BColor getMarginColor() {
        return marginColor;
    }

    public void setMarginColor(BColor marginColor) {
        this.marginColor = marginColor;
    }

    public LegendConfig getLegendConfig() {
        return legendConfig;
    }

    public TooltipConfig getTooltipConfig() {
        return tooltipConfig;
    }

    public CrossHairConfig getCrossHairConfig() {
        return crossHairConfig;
    }

    public AxisConfig getLeftAxisConfig() {
        return leftAxisConfig;
    }

    public void setLeftAxisConfig(AxisConfig leftAxisConfig) {
        this.leftAxisConfig = leftAxisConfig;
    }

    public AxisConfig getRightAxisConfig() {
        return rightAxisConfig;
    }

    public void setRightAxisConfig(AxisConfig rightAxisConfig) {
        this.rightAxisConfig = rightAxisConfig;
    }

    public AxisConfig getBottomAxisConfig() {
        return bottomAxisConfig;
    }

    public void setBottomAxisConfig(AxisConfig bottomAxisConfig) {
        this.bottomAxisConfig = bottomAxisConfig;
    }

    public AxisConfig getTopAxisConfig() {
        return topAxisConfig;
    }

    public void setTopAxisConfig(AxisConfig topAxisConfig) {
        this.topAxisConfig = topAxisConfig;
    }
}
