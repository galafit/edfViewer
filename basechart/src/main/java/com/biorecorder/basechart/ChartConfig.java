package com.biorecorder.basechart;

import com.biorecorder.basechart.axis.AxisConfig;
import com.biorecorder.basechart.graphics.BColor;
import com.sun.istack.internal.Nullable;

/**
 * Created by galafit on 18/8/17.
 */
public class ChartConfig {
    private BColor[] traceColors = {BColor.MAGENTA, BColor.BLUE};

    private BColor backgroundColor;
    private BColor marginColor;

    private TitleConfig titleConfig = new TitleConfig();
    private LegendConfig legendConfig = new LegendConfig();
    private TooltipConfig tooltipConfig = new TooltipConfig();
    private CrosshairConfig crossHairConfig = new CrosshairConfig();

    private Insets margin;
    private Insets spacing;
    private int autoSpacing = 5; //px taken into account only if spacing is null

    private AxisConfig leftAxisConfig = new AxisConfig();
    private AxisConfig rightAxisConfig = new AxisConfig();
    private AxisConfig bottomAxisConfig = new AxisConfig();
    private AxisConfig topAxisConfig = new AxisConfig();

    private boolean isLeftAxisPrimary = true;
    private boolean isBottomAxisPrimary = true;
    private boolean isMultiCurveTooltip = true;

    private int defaultStackWeight = 4;
    private int axisRoundingAccuracyPctIfRoundingEnabled = 5;
    private int axisRoundingAccuracyPctIfRoundingDisabled = 20;
    private boolean isYAxisRoundingEnabled = true;
    private boolean isXAxisRoundingEnabled = false;

    public ChartConfig() {
    }

    public ChartConfig(ChartConfig chartConfig) {
        traceColors = chartConfig.traceColors;
        backgroundColor = chartConfig.backgroundColor;
        marginColor = chartConfig.marginColor;
        titleConfig = new TitleConfig(chartConfig.titleConfig);
        legendConfig = new LegendConfig(chartConfig.legendConfig);
        tooltipConfig = new TooltipConfig(chartConfig.tooltipConfig);
        crossHairConfig = new CrosshairConfig(chartConfig.crossHairConfig);
        spacing = chartConfig.spacing;
        margin = chartConfig.margin;
        autoSpacing = chartConfig.autoSpacing;
        leftAxisConfig = new AxisConfig(chartConfig.leftAxisConfig);
        rightAxisConfig = new AxisConfig(chartConfig.rightAxisConfig);
        topAxisConfig = new AxisConfig(chartConfig.topAxisConfig);
        bottomAxisConfig = new AxisConfig(chartConfig.bottomAxisConfig);
        isLeftAxisPrimary = chartConfig.isLeftAxisPrimary;
        isBottomAxisPrimary = chartConfig.isBottomAxisPrimary;
        isMultiCurveTooltip = chartConfig.isMultiCurveTooltip;
        defaultStackWeight = chartConfig.defaultStackWeight;
        axisRoundingAccuracyPctIfRoundingEnabled = chartConfig.axisRoundingAccuracyPctIfRoundingEnabled;
        axisRoundingAccuracyPctIfRoundingDisabled = chartConfig.axisRoundingAccuracyPctIfRoundingDisabled;

        isYAxisRoundingEnabled = chartConfig.isYAxisRoundingEnabled;
        isXAxisRoundingEnabled = chartConfig.isXAxisRoundingEnabled;
    }

    public Insets getMargin() {
        return margin;
    }

    /**
     * if null (default) margins will be calculated automatically
     */
    public void setMargin(@Nullable Insets margin) {
        this.margin = margin;
    }

    public Insets getSpacing() {
        return spacing;
    }

    /**
     * if null (default) spaces will be calculated automatically on the base of autoSpacing
     */
    public void setSpacing(@Nullable Insets spacing) {
        this.spacing = spacing;
    }

    public int getAutoSpacing() {
        return autoSpacing;
    }

    public void setAutoSpacing(int autoSpacing) {
        this.autoSpacing = autoSpacing;
    }

    public boolean isLeftAxisPrimary() {
        return isLeftAxisPrimary;
    }

    public void setLeftAxisPrimary(boolean isLeftAxisPrimary) {
        this.isLeftAxisPrimary = isLeftAxisPrimary;
    }

    public boolean isBottomAxisPrimary() {
        return isBottomAxisPrimary;
    }

    public void setBottomAxisPrimary(boolean isBottomAxisPrimary) {
        this.isBottomAxisPrimary = isBottomAxisPrimary;
    }

    public boolean isMultiCurveTooltip() {
        return isMultiCurveTooltip;
    }

    public void setMultiCurveTooltip(boolean isSingleCurveTooltip) {
        this.isMultiCurveTooltip = isSingleCurveTooltip;
    }

    public int getDefaultStackWeight() {
        return defaultStackWeight;
    }

    public void setDefaultStackWeight(int defaultStackWeight) {
        this.defaultStackWeight = defaultStackWeight;
    }

    public int getAxisRoundingAccuracyPctIfRoundingEnabled() {
        return axisRoundingAccuracyPctIfRoundingEnabled;
    }

    public void setAxisRoundingAccuracyPctIfRoundingEnabled(int axisRoundingAccuracyPctIfRoundingEnabled) {
        this.axisRoundingAccuracyPctIfRoundingEnabled = axisRoundingAccuracyPctIfRoundingEnabled;
    }

    public int getAxisRoundingAccuracyPctIfRoundingDisabled() {
        return axisRoundingAccuracyPctIfRoundingDisabled;
    }

    public void setAxisRoundingAccuracyPctIfRoundingDisabled(int axisRoundingAccuracyPctIfRoundingDisabled) {
        this.axisRoundingAccuracyPctIfRoundingDisabled = axisRoundingAccuracyPctIfRoundingDisabled;
    }

    public boolean isYAxisRoundingEnabled() {
        return isYAxisRoundingEnabled;
    }

    public void setYAxisRoundingEnabled(boolean isYAxisRoundingEnabled) {
        this.isYAxisRoundingEnabled = isYAxisRoundingEnabled;
    }

    public boolean isXAxisRoundingEnabled() {
        return isXAxisRoundingEnabled;
    }

    public void setXAxisRoundingEnabled(boolean isXAxisRoundingEnabled) {
        this.isXAxisRoundingEnabled = isXAxisRoundingEnabled;
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

    public CrosshairConfig getCrossHairConfig() {
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
