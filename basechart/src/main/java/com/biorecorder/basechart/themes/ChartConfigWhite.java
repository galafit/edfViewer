package com.biorecorder.basechart.themes;

import com.biorecorder.basechart.ChartConfig;
import com.biorecorder.basechart.axis.AxisConfig;
import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.Insets;

/**
 * Created by galafit on 23/2/19.
 */
public class ChartConfigWhite extends ChartConfig {
    public ChartConfigWhite() {
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
        BColor bgColor = new BColor(245, 245, 245);
        BColor marginBgColor = bgColor;

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
}
