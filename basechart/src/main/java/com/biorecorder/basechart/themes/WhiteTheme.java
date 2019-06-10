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
public class WhiteTheme implements Theme {
    private final ChartConfig chartConfig;
    private final NavigableChartConfig navigableChartConfig;

    public WhiteTheme() {
        this(false);
    }

    public WhiteTheme(boolean isYRoundingEnabled) {
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


        final BColor[] fujiColors = {
                new BColor(82, 89, 179),
                new BColor(43, 194, 189),
                new BColor(240, 98, 96),
                new BColor(255, 194, 51),
                new BColor(79, 179, 132),
                new BColor(177, 129, 222),
                new BColor(17, 186, 240)
        };

        final BColor[] plotlyColors = {
                new BColor(32, 120, 179),
                new BColor(255, 127, 15),
                new BColor(44, 158, 44),
                new BColor(148, 104, 189),
                new BColor(138, 85, 74),
                new BColor(227, 120, 193),
                new BColor(125, 125, 125),
                new BColor(189, 189, 34),
                new BColor(25, 191, 207),

        };

        BColor[] colors = {BLUE_DARK, RED_DARK, GRAY, MAGENTA, CYAN, ORANGE, BLUE, PINK, GREEN_DARK, RED};
        colors = plotlyColors;

        /**========================== CHART ==========================**/
        BColor chartBgColor = BColor.WHITE_OBSCURE_LIGHT;
        BColor chartMarginColor = BColor.WHITE_OBSCURE;

        BColor labelColor = new BColor(70, 60, 60);
        BColor axisColor = new BColor(160, 160, 160);
        BColor gridColor = new BColor(215, 215, 215);

        BColor crosshairColor = new BColor(100, 95, 95);

        AxisConfig xAxisConfig = new AxisConfig();
        xAxisConfig.setColors(axisColor, labelColor, gridColor, gridColor);
        xAxisConfig.setTickMarkSize(4, 0);
        xAxisConfig.setCrosshairLineColor(crosshairColor);
        xAxisConfig.setAxisLineWidth(0);

        AxisConfig yAxisConfig = new AxisConfig();
        yAxisConfig.setColors(axisColor, labelColor, gridColor, gridColor);
        yAxisConfig.setTickMarkSize(6, 0);
        yAxisConfig.setRoundingEnabled(isYRoundingEnabled);
        yAxisConfig.setCrosshairLineColor(crosshairColor);
        yAxisConfig.setAxisLineWidth(0);

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
        BColor bgColor = chartBgColor;
        BColor scrollColor = crosshairColor;


        ChartConfig navigatorConfig = new ChartConfig(chartConfig);
        navigatorConfig.setBackgroundColor(navigatorBgColor);
        navigatorConfig.setMarginColor(navigatorMarginColor);
        navigatorConfig.getTitleConfig().setTextColor(labelColor);
        navigatorConfig.getLegendConfig().setBackgroundColor(navigatorBgColor);
        navigatorConfig.setDefaultStackWeight(2);
        navigatorConfig.setStackGap(0);
        navigatorConfig.getYAxisConfig().setTickLabelOutside(false);
        navigatorConfig.getYAxisConfig().setRoundingEnabled(true);

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
