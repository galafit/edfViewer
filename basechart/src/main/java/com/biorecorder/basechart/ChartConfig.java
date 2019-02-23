package com.biorecorder.basechart;

import com.biorecorder.basechart.axis.AxisConfig;
import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.graphics.Insets;
import com.sun.istack.internal.Nullable;

/**
 * Created by galafit on 18/8/17.
 */
public class ChartConfig {
    protected BColor[] traceColors;

    protected BColor backgroundColor;
    protected BColor marginColor;

    protected TitleConfig titleConfig = new TitleConfig();
    protected LegendConfig legendConfig = new LegendConfig();
    protected TooltipConfig tooltipConfig = new TooltipConfig();
    protected CrosshairConfig crossHairConfig = new CrosshairConfig();

    protected Insets margin;
    protected Insets spacing;
    protected int autoSpacing = 5; //px taken into account only if spacing is null

    protected AxisConfig leftAxisConfig = new AxisConfig();
    protected AxisConfig rightAxisConfig = new AxisConfig();
    protected AxisConfig bottomAxisConfig = new AxisConfig();
    protected AxisConfig topAxisConfig = new AxisConfig();

    protected boolean isLeftAxisPrimary = true;
    protected boolean isBottomAxisPrimary = true;
    protected boolean isMultiCurveTooltip = true;

    protected int defaultStackWeight = 4;
    protected int axisRoundingAccuracyPctIfRoundingEnabled = 5;
    protected int axisRoundingAccuracyPctIfRoundingDisabled = 20;
    protected boolean isYAxisRoundingEnabled = true;
    protected boolean isXAxisRoundingEnabled = false;

    public ChartConfig() {
        final BColor CYAN = new BColor(0, 200, 220);
        final BColor BLUE = new BColor(100, 120, 250);
        final BColor MAGENTA = new BColor(165, 80, 220);
        final BColor GREEN = new BColor(110, 250, 110);
        final BColor RED = new BColor(250, 64, 82);
        final BColor ORANGE = new BColor(200, 80, 0);//new BColor(173, 105, 49);
        final BColor YELLOW = new BColor(252, 177, 48);
        final BColor GRAY = new BColor(180, 180, 200);
        final BColor PINK = new BColor(255, 50, 200);//new BColor(255, 60, 130); //new BColor(250, 0, 200);
        final BColor GOLD = new BColor(190, 140, 110);

        final BColor[] colors = {BLUE, RED, GRAY, MAGENTA, ORANGE, YELLOW, GREEN, CYAN, PINK, GOLD};

        BColor bgColor = new BColor(18, 15, 18);//BColor.BLACK;
        BColor marginBgColor = BColor.BLACK;//chartBgColor;
        BColor titleColor = new BColor(160, 140, 110);

        BColor axisColor = titleColor;
        BColor gridColor = new BColor(70, 65, 45);

        BColor crosshairColor = new BColor(245, 226, 208); //new BColor(201, 182, 163); //new BColor(252, 242, 227);

        AxisConfig xAxisConfig = new AxisConfig();
        xAxisConfig.setColors(axisColor, gridColor, gridColor);
        xAxisConfig.setTickMarkSize(4, 0);

        AxisConfig yAxisConfig = new AxisConfig();
        yAxisConfig.setColors(axisColor, gridColor, gridColor);
        yAxisConfig.setTickMarkSize(4, 0);

        traceColors = colors;
        backgroundColor = bgColor;
        marginColor = marginBgColor;
        titleConfig.setTextColor(titleColor);
        leftAxisConfig = new AxisConfig(yAxisConfig);
        rightAxisConfig = new AxisConfig(yAxisConfig);
        topAxisConfig = new AxisConfig(xAxisConfig);
        bottomAxisConfig = new AxisConfig(xAxisConfig);
        crossHairConfig.setLineColor(crosshairColor);
        legendConfig.setBackgroundColor(bgColor);
        legendConfig.setMargin(new Insets(1));
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
