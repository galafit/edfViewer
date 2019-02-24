package com.biorecorder.basechart.themes;

import com.biorecorder.basechart.ChartConfig;
import com.biorecorder.basechart.NavigableChartConfig;
import com.biorecorder.basechart.axis.AxisConfig;
import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.scroll.ScrollConfig;

/**
 * Created by galafit on 24/2/19.
 */
public class WhiteTheme implements Theme {
    private final ChartConfig chartConfig;
    private final NavigableChartConfig navigableChartConfig;

    public WhiteTheme() {
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

        final BColor[] colors = {BLUE_DARK, RED_DARK, GRAY, MAGENTA, CYAN, ORANGE, BLUE, PINK, GREEN_DARK, RED};

        /**========================== CHART ==========================**/
        BColor chartBgColor = new BColor(245, 245, 245);
        BColor chartMarginColor = chartBgColor;

        BColor titleColor = new BColor(70, 70, 70);
        BColor axisColor = titleColor;
        BColor gridColor = new BColor(214, 214, 214);

        BColor crosshairColor = new BColor(30, 30, 30);

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

        BColor navigatorBgColor = new BColor(230, 230, 230);
        BColor navigatorMarginColor = navigatorBgColor;
        BColor scrollColor = crosshairColor;
        BColor bgColor = BColor.WHITE;

        ChartConfig navigatorConfig = new ChartConfig(chartConfig);
        navigatorConfig.setBackgroundColor(navigatorBgColor);
        navigatorConfig.setMarginColor(navigatorMarginColor);
        navigatorConfig.getTitleConfig().setTextColor(titleColor);
        navigatorConfig.getLegendConfig().setBackgroundColor(navigatorBgColor);
        navigatorConfig.setDefaultStackWeight(2);
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
