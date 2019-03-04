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
        final BColor MAGENTA = new BColor(150, 50, 185);
        final BColor RED = new BColor(250, 60, 90); //new BColor(230, 10, 60);
        final BColor BLUE_DARK = new BColor(30, 30, 180);
        final BColor PINK = new BColor(230, 0, 230);
        final BColor RED_DARK = new BColor(180, 0, 0);
        final BColor CYAN = new BColor(0, 160, 160);
        final BColor GRAY = new BColor(120, 56, 7); //new BColor(60, 70, 100);

        final BColor[] colors = {BLUE_DARK, RED_DARK, GRAY, MAGENTA, CYAN, ORANGE, BLUE, PINK, GREEN_DARK, RED};

        /**========================== CHART ==========================**/
        BColor chartBgColor = BColor.WHITE_DARK;
        BColor chartMarginColor = BColor.BEIGE_WHITE;

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

        ChartConfig chartConfig = new ChartConfig();
        chartConfig.setTraceColors(colors);
        chartConfig.setBackgroundColor(chartBgColor);
        chartConfig.setMarginColor(chartMarginColor);
        chartConfig.getTitleConfig().setTextColor(titleColor);
        chartConfig.setYAxisConfig(yAxisConfig);
        chartConfig.setXAxisConfig(xAxisConfig);
        chartConfig.getCrossHairConfig().setLineColor(crosshairColor);
        chartConfig.getLegendConfig().setBackgroundColor(chartBgColor);
        chartConfig.setDefaultStackWeight(4);
        chartConfig.setLeftAxisPrimary(false);
        chartConfig.setBottomAxisPrimary(true);

        this.chartConfig = chartConfig;

        /**========================== NAVIGATOR ==========================**/

        BColor navigatorBgColor = BColor.BEIGE_WHITE;
        BColor navigatorMarginColor = navigatorBgColor;
        BColor bgColor = BColor.WHITE_DARK;
        BColor scrollColor = crosshairColor;


        ChartConfig navigatorConfig = new ChartConfig(chartConfig);
        navigatorConfig.setBackgroundColor(navigatorBgColor);
        navigatorConfig.setMarginColor(navigatorMarginColor);
        navigatorConfig.getTitleConfig().setTextColor(titleColor);
        navigatorConfig.getLegendConfig().setBackgroundColor(navigatorBgColor);
        navigatorConfig.setDefaultStackWeight(2);
        navigatorConfig.setStackGap(0);
        navigatorConfig.getYAxisConfig().setTickLabelOutside(false);
        navigatorConfig.getYAxisConfig().setRoundingEnabled(true, AxisConfig.TICK_ACCURACY_IF_ROUNDING_ENABLED);

        ScrollConfig scrollConfig = new ScrollConfig();
        scrollConfig.setColor(scrollColor);

        ChartConfig chartConfig1 = new ChartConfig(chartConfig);
        chartConfig1.getYAxisConfig().setTickLabelOutside(false);
        chartConfig1.setBottomAxisPrimary(false);

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
