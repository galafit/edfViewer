package com.biorecorder.basechart;

import com.biorecorder.basechart.axis.AxisConfig;
import com.biorecorder.basechart.graphics.BColor;
import com.sun.istack.internal.Nullable;

/**
 * Created by galafit on 18/8/17.
 */
public class ChartConfig {
    private BColor[] traceColors;

    private BColor backgroundColor;
    private BColor marginColor;

    private TitleConfig titleConfig = new TitleConfig();
    private LegendConfig legendConfig = new LegendConfig();
    private TooltipConfig tooltipConfig = new TooltipConfig();
    private CrosshairConfig crossHairConfig = new CrosshairConfig();

    private Insets margin;
    private Insets spacing;
    private int autoSpacing = 5; //px taken into account only if spacing is null

    private int stackGap = 4; //px

    private AxisConfig leftAxisConfig = new AxisConfig();
    private AxisConfig rightAxisConfig = new AxisConfig();
    private AxisConfig bottomAxisConfig = new AxisConfig();
    private AxisConfig topAxisConfig = new AxisConfig();

    private boolean isMultiCurveTooltip = true;

    private boolean isLeftAxisPrimary = true;
    private boolean isBottomAxisPrimary = true;

    private int defaultStackWeight = 4;
    private boolean isYAxisRoundingEnabled = false;
    private boolean isXAxisRoundingEnabled = false;
    private boolean isXAxisGridVisible = true;
    private boolean isYAxisGridVisible = true;

    public ChartConfig() {
        final BColor BLUE = new BColor(0, 130, 230);
        final BColor ORANGE = new BColor(235, 80, 0); //new BColor(250, 100, 30);
        final BColor GREEN_DARK = new BColor(0, 130, 0);
        final BColor MAGENTA = new BColor(120, 50, 185);
        final BColor RED = new BColor(250, 60, 90); //new BColor(230, 10, 60);
        final BColor BLUE_DARK = new BColor(30, 30, 180);
        final BColor PINK = new BColor(230, 0, 230);
        final BColor RED_DARK = new BColor(180, 0, 0);
        final BColor CYAN = new BColor(0, 160, 160);
        final BColor GRAY = new BColor(120, 56, 7); //new BColor(60, 70, 100);

        //final BColor[] colors = {BLUE, ORANGE, RED, GREEN_DARK, MAGENTA, BLUE_DARK, PINK, RED_DARK, CYAN, GRAY};
        final BColor[] colors = {BLUE_DARK, RED_DARK, GRAY, MAGENTA, CYAN, ORANGE, BLUE, PINK, GREEN_DARK, RED};

        BColor bgColor = BColor.WHITE_DARK;
        BColor marginBgColor = BColor.BEIGE_WHITE;

        BColor titleColor = BColor.GRAY;
        BColor axisColor = titleColor;
        BColor gridColor = BColor.GRAY_LIGHT;

        BColor crosshairColor = axisColor;

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
        stackGap = chartConfig.stackGap;
        leftAxisConfig = new AxisConfig(chartConfig.leftAxisConfig);
        rightAxisConfig = new AxisConfig(chartConfig.rightAxisConfig);
        topAxisConfig = new AxisConfig(chartConfig.topAxisConfig);
        bottomAxisConfig = new AxisConfig(chartConfig.bottomAxisConfig);
        isLeftAxisPrimary = chartConfig.isLeftAxisPrimary;
        isBottomAxisPrimary = chartConfig.isBottomAxisPrimary;
        isMultiCurveTooltip = chartConfig.isMultiCurveTooltip;
        defaultStackWeight = chartConfig.defaultStackWeight;

        isYAxisRoundingEnabled = chartConfig.isYAxisRoundingEnabled;
        isXAxisRoundingEnabled = chartConfig.isXAxisRoundingEnabled;
    }

    public int getStackGap() {
        return stackGap;
    }

    public void setStackGap(int stackGap) {
        this.stackGap = stackGap;
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
