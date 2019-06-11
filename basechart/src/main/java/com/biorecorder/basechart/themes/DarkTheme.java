package com.biorecorder.basechart.themes;

import com.biorecorder.basechart.ChartConfig;
import com.biorecorder.basechart.NavigableChartConfig;
import com.biorecorder.basechart.axis.XAxisPosition;
import com.biorecorder.basechart.axis.YAxisPosition;
import com.biorecorder.basechart.axis.AxisConfig;
import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.ScrollConfig;

/**
 * Created by galafit on 24/2/19.
 */
public class DarkTheme implements Theme {
    public static final BColor CYAN = new BColor(0, 200, 220);
    public static final BColor BLUE = new BColor(100, 120, 250);
    public static final BColor MAGENTA = new BColor(165, 80, 220);
    public static final BColor GREEN = new BColor(110, 250, 110);
    public static final BColor RED = new BColor(250, 64, 82);
    public static final BColor ORANGE = new BColor(200, 80, 0);//new BColor(173, 105, 49);
    public static final BColor YELLOW = new BColor(252, 177, 48);
    public static final BColor GRAY = new BColor(180, 180, 200);
    public static final BColor PINK = new BColor(255, 50, 200);//new BColor(255, 60, 130); //new BColor(250, 0, 200);
    public static final BColor GOLD = new BColor(190, 140, 110);

    private final ChartConfig chartConfig;
    private final NavigableChartConfig navigableChartConfig;

    public DarkTheme() {
        this(false);
    }

    public DarkTheme(boolean isYRoundingEnabled) {
        final BColor[] colors = {BLUE, RED, GRAY, MAGENTA, ORANGE, YELLOW, GREEN, CYAN, PINK, GOLD};

        /**========================== CHART ==========================**/
        BColor chartBgColor =  new BColor(30, 30, 40);  // new BColor(18, 15, 18);
        BColor chartMarginColor = new BColor(20, 20, 20);
        BColor labelColor = new BColor(160, 140, 110);
        BColor axisColor = new BColor(100, 86, 60);
        BColor gridColor = new BColor(70, 65, 45);

        BColor crosshairColor = BColor.WHITE_OBSCURE;

        AxisConfig xAxisConfig = new AxisConfig();
        xAxisConfig.setColors(axisColor, labelColor, gridColor, gridColor);
        xAxisConfig.setTickMarkSize(4, 0);
        xAxisConfig.setCrosshairLineColor(crosshairColor);
        xAxisConfig.setAxisLineWidth(0);
        //xAxisConfig.setMinorTickIntervalCount(3);

        AxisConfig yAxisConfig = new AxisConfig();
        yAxisConfig.setColors(axisColor, labelColor, gridColor, gridColor);
        yAxisConfig.setTickMarkSize(6, 0);
        yAxisConfig.setRoundingEnabled(isYRoundingEnabled);
        yAxisConfig.setCrosshairLineColor(crosshairColor);
        yAxisConfig.setAxisLineWidth(0);
        //yAxisConfig.setMinorTickIntervalCount(3);

        ChartConfig chartConfig = new ChartConfig();
        chartConfig.setTraceColors(colors);
        chartConfig.setBackgroundColor(chartBgColor);
        chartConfig.setMarginColor(chartMarginColor);
        chartConfig.getTitleConfig().setTextColor(labelColor);
        chartConfig.setYAxisConfig(yAxisConfig);
        chartConfig.setXAxisConfig(xAxisConfig);
        chartConfig.getLegendConfig().setBackgroundColor(chartBgColor);
        chartConfig.setDefaultStackWeight(4);
        chartConfig.setPrimaryYPosition(YAxisPosition.LEFT);
        chartConfig.setPrimaryXPosition(XAxisPosition.BOTTOM);

        this.chartConfig = chartConfig;

        /**========================== NAVIGATOR ==========================**/

        BColor navigatorBgColor = chartMarginColor;
        BColor navigatorMarginColor = navigatorBgColor;
        BColor scrollColor = crosshairColor;
        BColor bgColor = navigatorBgColor;

        ChartConfig navigatorConfig = new ChartConfig(chartConfig);
        navigatorConfig.setBackgroundColor(navigatorBgColor);
        navigatorConfig.setMarginColor(navigatorMarginColor);
        navigatorConfig.getTitleConfig().setTextColor(labelColor);
        navigatorConfig.getLegendConfig().setBackgroundColor(navigatorBgColor);
        navigatorConfig.setDefaultStackWeight(2);
        navigatorConfig.setStackGap(0);
        navigatorConfig.getYAxisConfig().setTickLabelOutside(false);
        navigatorConfig.getYAxisConfig().setRoundingEnabled(true);
        navigatorConfig.setPrimaryYPosition(YAxisPosition.RIGHT);
        navigatorConfig.setPrimaryXPosition(XAxisPosition.BOTTOM);


        ScrollConfig scrollConfig = new ScrollConfig();
        scrollConfig.setColor(scrollColor);

        ChartConfig chartConfig1 = new ChartConfig(chartConfig);
        chartConfig1.getYAxisConfig().setTickLabelOutside(false);
        chartConfig1.setPrimaryYPosition(YAxisPosition.RIGHT);
        chartConfig1.setPrimaryXPosition(XAxisPosition.TOP);


        navigableChartConfig = new NavigableChartConfig(chartConfig1, navigatorConfig, scrollConfig);
        navigableChartConfig.setBackgroundColor(bgColor);
    }

    @Override
    public ChartConfig getChartConfig() {
        return chartConfig;
    }

    @Override
    public NavigableChartConfig getNavigableChartConfig() {
        return navigableChartConfig;
    }
}
