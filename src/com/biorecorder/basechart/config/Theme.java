package com.biorecorder.basechart.config;

import com.biorecorder.basechart.BColor;

/**
 * Chart color themes
 */
public enum Theme {
    WHITE(1),
    DARK(2),
    GRAY(3);

    private BColor chartBgColor;
    private BColor chartMarginColor;

    private BColor previewBgColor;
    private BColor previewMarginColor;
    private BColor titleColor;

    private BColor axisColor;
    private BColor gridColor;
    private BColor[] traceColors;

    private BColor crosshairColor;
    private BColor scrollColor;

    Theme(int themeId) {
        if(themeId == 1) { // WHITE
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

            //BColor[] colors = {BLUE, ORANGE, RED, GREEN_DARK, MAGENTA, BLUE_DARK, PINK, RED_DARK, CYAN, GRAY};
            BColor[] colors = {BLUE_DARK, RED_DARK, GRAY, MAGENTA , CYAN, ORANGE, BLUE,   PINK, GREEN_DARK, RED};

            chartBgColor =  BColor.WHITE;
            chartMarginColor = chartBgColor;

            previewBgColor = BColor.WHITE;
            previewMarginColor = previewBgColor;

            titleColor = new BColor(60, 60, 60);
            axisColor = titleColor;
            gridColor = new BColor(220, 220, 220);
            traceColors = colors;

            crosshairColor = new BColor(30, 30, 30);
            scrollColor = crosshairColor;
        }
        if(themeId == 2) { // DARK
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

            BColor[] colors = {BLUE, RED, GRAY, MAGENTA, ORANGE, YELLOW, GREEN, CYAN, PINK, GOLD};

            chartBgColor = new BColor(18, 15, 18);//BColor.BLACK;
            chartMarginColor = chartBgColor;
            titleColor = new BColor(160, 140, 110);

            previewBgColor = new BColor(25, 25, 30); //new BColor(28, 25, 28);
            previewMarginColor = previewBgColor;

            axisColor = titleColor;
            gridColor = new BColor(70, 65, 45);

            traceColors = colors;

            crosshairColor = new BColor(245, 226, 208); //new BColor(201, 182, 163); //new BColor(252, 242, 227);
            scrollColor = crosshairColor;
        }
        if(themeId == 3) { // GRAY
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

            BColor[] colors = {BLUE_DARK, RED_DARK, GRAY, MAGENTA , CYAN, ORANGE, BLUE,   PINK, GREEN_DARK, RED};

            chartBgColor = new BColor(215, 210, 215);
            chartMarginColor = chartBgColor;

            previewBgColor = new BColor(230, 230, 230);
            previewMarginColor = previewBgColor;

            titleColor = new BColor(40, 40, 40); //new BColor(70, 60, 40);
            axisColor = titleColor;
            gridColor = new BColor(180, 180, 180);
            traceColors = colors;

            crosshairColor = new BColor(35, 10, 35);
            scrollColor = crosshairColor;
        }
    }

    public BColor getCrosshairColor() {
        return crosshairColor;
    }

    public BColor getScrollColor() {
        return scrollColor;
    }

    public BColor getChartBgColor() {
        return chartBgColor;
    }

    public BColor getChartMarginColor() {
        return chartMarginColor;
    }

    public BColor getPreviewBgColor() {
        return previewBgColor;
    }

    public BColor getPreviewMarginColor() {
        return previewMarginColor;
    }

    public BColor getTitleColor() {
        return titleColor;
    }

    public BColor getAxisColor() {
        return axisColor;
    }

    public BColor getGridColor() {
        return gridColor;
    }

    public BColor[] getTraceColors() {
        return traceColors;
    }
}
