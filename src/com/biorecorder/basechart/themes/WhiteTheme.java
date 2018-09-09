package com.biorecorder.basechart.themes;

import com.biorecorder.basechart.axis.AxisConfig;
import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.graphics.TextStyle;

/**
 * Created by galafit on 31/8/18.
 */
public class WhiteTheme extends Theme {
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

    public WhiteTheme() {
        BColor chartBgColor = BColor.WHITE;
        BColor chartMarginColor = chartBgColor;

        BColor previewBgColor = BColor.WHITE;
        BColor previewMarginColor = previewBgColor;

        BColor titleColor = new BColor(60, 60, 60);
        BColor axisColor = titleColor;
        BColor gridColor = new BColor(220, 220, 220);

        BColor crosshairColor = new BColor(30, 30, 30);
        BColor scrollColor = crosshairColor;

        AxisConfig xAxisConfig = new AxisConfig();
        xAxisConfig.setColors(axisColor, gridColor, gridColor);
        xAxisConfig.setTickMarkSize(4, 0);


        AxisConfig yAxisConfig = new AxisConfig();
        yAxisConfig.setColors(axisColor, gridColor, gridColor);
        yAxisConfig.setTickMarkSize(4, 0);

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

        previewConfig.setTraceColors(colors);
        previewConfig.setBackgroundColor(previewBgColor);
        previewConfig.setMarginColor(previewMarginColor);
        previewConfig.getTitleConfig().setTextColor(titleColor);
        previewConfig.setLeftAxisConfig(yAxisConfig);
        previewConfig.setRightAxisConfig(yAxisConfig);
        previewConfig.setTopAxisConfig(xAxisConfig);
        previewConfig.setBottomAxisConfig(xAxisConfig);
        previewConfig.getCrossHairConfig().setLineColor(crosshairColor);
        previewConfig.getLegendConfig().setBackgroundColor(chartBgColor);

        scrollConfig.setScrollColor(scrollColor);
    }
}
