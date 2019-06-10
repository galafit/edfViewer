package com.biorecorder.basechart;

import com.biorecorder.basechart.axis.AxisConfig;
import com.biorecorder.basechart.axis.XAxisPosition;
import com.biorecorder.basechart.axis.YAxisPosition;
import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.graphics.Insets;
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

    private Insets margin;
    private Insets spacing;
    private int autoSpacing = 5; //px taken into account only if spacing is null

    private int stackGap = 4; //px


    private AxisConfig yAxisConfig = new AxisConfig();
    private AxisConfig xAxisConfig = new AxisConfig();

    private XAxisPosition primaryXPosition = XAxisPosition.BOTTOM;
    private YAxisPosition primaryYPosition = YAxisPosition.LEFT;

    private int defaultStackWeight = 4;

    public ChartConfig() {
        final BColor BLUE = new BColor(0, 130, 230);
        final BColor ORANGE = new BColor(235, 80, 0); //new BColor(250, 100, 30);
        final BColor GREEN_DARK = new BColor(0, 130, 0);
        final BColor MAGENTA = new BColor(150, 50, 185);
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

        xAxisConfig.setColors(axisColor, gridColor, gridColor);
        xAxisConfig.setTickMarkSize(4, 0);
        xAxisConfig.setCrosshairLineColor(crosshairColor);

        yAxisConfig.setColors(axisColor, gridColor, gridColor);
        yAxisConfig.setTickMarkSize(4, 0);
        yAxisConfig.setCrosshairLineColor(crosshairColor);
        
        traceColors = colors;
        backgroundColor = bgColor;
        marginColor = marginBgColor;
        titleConfig.setTextColor(titleColor);
        yAxisConfig = new AxisConfig(yAxisConfig);
        xAxisConfig = new AxisConfig(yAxisConfig);

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
        spacing = chartConfig.spacing;
        margin = chartConfig.margin;
        autoSpacing = chartConfig.autoSpacing;
        stackGap = chartConfig.stackGap;
        yAxisConfig = new AxisConfig(chartConfig.yAxisConfig);
        xAxisConfig = new AxisConfig(chartConfig.xAxisConfig);
        primaryXPosition = chartConfig.primaryXPosition;
        primaryYPosition = chartConfig.primaryYPosition;
        defaultStackWeight = chartConfig.defaultStackWeight;
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

    public XAxisPosition getPrimaryXPosition() {
        return primaryXPosition;
    }

    public void setPrimaryXPosition(XAxisPosition primaryXPosition) {
        this.primaryXPosition = primaryXPosition;
    }

    public YAxisPosition getPrimaryYPosition() {
        return primaryYPosition;
    }

    public void setPrimaryYPosition(YAxisPosition primaryYPosition) {
        this.primaryYPosition = primaryYPosition;
    }

    public int getDefaultStackWeight() {
        return defaultStackWeight;
    }

    public void setDefaultStackWeight(int defaultStackWeight) {
        this.defaultStackWeight = defaultStackWeight;
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

    public AxisConfig getYAxisConfig() {
        return yAxisConfig;
    }

    public void setYAxisConfig(AxisConfig yAxisConfig) {
        this.yAxisConfig = yAxisConfig;
    }

    public AxisConfig getXAxisConfig() {
        return xAxisConfig;
    }

    public void setXAxisConfig(AxisConfig xAxisConfig) {
        this.xAxisConfig = xAxisConfig;
    }
}
