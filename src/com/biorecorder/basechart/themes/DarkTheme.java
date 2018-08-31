package com.biorecorder.basechart.themes;

import com.biorecorder.basechart.axis.AxisConfig;
import com.biorecorder.basechart.graphics.BColor;

/**
 * Created by galafit on 31/8/18.
 */
public class DarkTheme extends Theme {
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

    public DarkTheme() {
        BColor chartBgColor = new BColor(18, 15, 18);//BColor.BLACK;
        BColor chartMarginColor = chartBgColor;
        BColor titleColor = new BColor(160, 140, 110);

        BColor previewBgColor = new BColor(25, 25, 30); //new BColor(28, 25, 28);
        BColor previewMarginColor = previewBgColor;

        BColor axisColor = titleColor;
        BColor gridColor = new BColor(70, 65, 45);

        BColor crosshairColor = new BColor(245, 226, 208); //new BColor(201, 182, 163); //new BColor(252, 242, 227);
        BColor scrollColor = crosshairColor;

        AxisConfig xAxisConfig = new AxisConfig();
        xAxisConfig.setColors(axisColor, gridColor, gridColor);
        xAxisConfig.setTickLabelInside(false);
        xAxisConfig.setTickMarkSize(3, 0);

        AxisConfig yAxisConfig = new AxisConfig();
        yAxisConfig.setColors(axisColor, gridColor, gridColor);
        yAxisConfig.setTickLabelInside(true);
        yAxisConfig.setTickMarkSize(0, 3);


        chartConfig.setBackgroundColor(chartBgColor);
        chartConfig.setMarginColor(chartMarginColor);
        chartConfig.getTitleConfig().setTextColor(titleColor);
        chartConfig.setLeftAxisConfig(yAxisConfig);
        chartConfig.setRightAxisConfig(yAxisConfig);
        chartConfig.setTopAxisConfig(xAxisConfig);
        chartConfig.setBottomAxisConfig(xAxisConfig);
        chartConfig.getCrossHairConfig().setLineColor(crosshairColor);

        previewConfig.setBackgroundColor(previewBgColor);
        previewConfig.setMarginColor(previewMarginColor);
        previewConfig.getTitleConfig().setTextColor(titleColor);
        previewConfig.setLeftAxisConfig(yAxisConfig);
        previewConfig.setRightAxisConfig(yAxisConfig);
        previewConfig.setTopAxisConfig(xAxisConfig);
        previewConfig.setBottomAxisConfig(xAxisConfig);
        previewConfig.getCrossHairConfig().setLineColor(crosshairColor);

        scrollConfig.setScrollColor(scrollColor);
    }
}
