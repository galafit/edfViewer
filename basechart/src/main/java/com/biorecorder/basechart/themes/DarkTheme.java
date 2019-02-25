package com.biorecorder.basechart.themes;

import com.biorecorder.basechart.ChartConfig;
import com.biorecorder.basechart.NavigableChartConfig;
import com.biorecorder.basechart.axis.AxisConfig;
import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.scroll.ScrollConfig;

/**
 * Created by galafit on 24/2/19.
 */
public class DarkTheme implements Theme {
    private final ChartConfig chartConfig;
    private final NavigableChartConfig navigableChartConfig;

    public DarkTheme() {
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

        /**========================== CHART ==========================**/
        BColor chartBgColor =  BColor.STEEL_DARK; new BColor(18, 15, 18);
        BColor chartMarginColor = BColor.BLACK_LIGHT;
        BColor titleColor = BColor.BEIGE;
        BColor axisColor = titleColor;
        BColor gridColor = BColor.BEIGE_LIGHT;

        BColor crosshairColor = axisColor;

        AxisConfig xAxisConfig = new AxisConfig();
        xAxisConfig.setColors(axisColor, gridColor, gridColor);
        xAxisConfig.setTickMarkSize(4, 0);

        AxisConfig yAxisConfig = new AxisConfig();
        yAxisConfig.setColors(axisColor, gridColor, gridColor);
        yAxisConfig.setTickMarkSize(4, 0);

        ChartConfig chartConfig = new ChartConfig();
        chartConfig.setTraceColors(colors);
        chartConfig.setBackgroundColor(chartBgColor);
        chartConfig.setMarginColor(chartMarginColor);
        chartConfig.getTitleConfig().setTextColor(titleColor);
        chartConfig.setLeftAxisConfig(yAxisConfig);
        chartConfig.setRightAxisConfig(yAxisConfig);
        chartConfig.setTopAxisConfig(xAxisConfig);
        chartConfig.setBottomAxisConfig(xAxisConfig);
        chartConfig.getCrossHairConfig().setLineColor(crosshairColor);
        chartConfig.getLegendConfig().setBackgroundColor(chartBgColor);
        chartConfig.setDefaultStackWeight(4);

        this.chartConfig = chartConfig;

        /**========================== NAVIGATOR ==========================**/

        BColor navigatorBgColor = BColor.BLACK_LIGHT;
        BColor navigatorMarginColor = navigatorBgColor;
        BColor scrollColor = crosshairColor;
        BColor bgColor = BColor.BLACK_LIGHT;

        ChartConfig navigatorConfig = new ChartConfig(chartConfig);
        navigatorConfig.setBackgroundColor(navigatorBgColor);
        navigatorConfig.setMarginColor(navigatorMarginColor);
        navigatorConfig.getTitleConfig().setTextColor(titleColor);
        navigatorConfig.getLegendConfig().setBackgroundColor(navigatorBgColor);
        navigatorConfig.setDefaultStackWeight(2);
        navigatorConfig.setStackGap(0);
        navigatorConfig.setYAxisRoundingEnabled(true);
        navigatorConfig.getLeftAxisConfig().setTickLabelOutside(false);
        navigatorConfig.getRightAxisConfig().setTickLabelOutside(false);

        ScrollConfig scrollConfig = new ScrollConfig();
        scrollConfig.setColor(scrollColor);

        ChartConfig chartConfig1 = new ChartConfig(chartConfig);
        chartConfig1.getLeftAxisConfig().setTickLabelOutside(false);
        chartConfig1.getRightAxisConfig().setTickLabelOutside(false);

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
