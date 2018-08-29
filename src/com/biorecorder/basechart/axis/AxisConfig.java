package com.biorecorder.basechart.axis;

import com.biorecorder.basechart.graphics.TextStyle;

/**
 * Created by galafit on 5/9/17.
 */
public class AxisConfig {
    private Style style = new Style();

    private TickFormatInfo tickFormatInfo = new TickFormatInfo();
    private boolean isTickLabelInside = false;
    private int tickPadding; // (px) padding between tick mark and tick label
    private TextStyle tickLabelTextStyle = new TextStyle(TextStyle.DEFAULT, TextStyle.NORMAL, 12);

    private int titlePadding; // px
    private TextStyle titleTextStyle = new TextStyle(TextStyle.DEFAULT, TextStyle.NORMAL, 14);


    private float tickInterval = -1; // in axis domain units

    private int tickMarkInsideSize = 0; // px
    private int tickMarkOutsideSize = 3; // px

    private int minorTickMarkInsideSize = 1; // px
    private int minorTickMarkOutsideSize = 1; // px
    private int minorTickIntervalCount = 3; // number of minor intervals in one major interval

    private boolean isVisible = false;
    private boolean isAxisLineVisible = false;
    private boolean isTitleVisible = false;
    private boolean isTickLabelsVisible = true;
    private boolean isGridVisible = false;
    private boolean isMinorGridVisible = false;

    private boolean isMinMaxRoundingEnabled = false;

    public AxisConfig() {
        titlePadding = (int)(0.4 * titleTextStyle.getSize());
        tickPadding = (int)(0.3 * tickLabelTextStyle.getSize());
    }

    public AxisConfig(AxisConfig axisConfig) {
        style = new Style(axisConfig.style);
        tickPadding = axisConfig.tickPadding;
        tickFormatInfo = new TickFormatInfo(axisConfig.tickFormatInfo);
        isTickLabelInside = axisConfig.isTickLabelInside;

        tickMarkInsideSize = axisConfig.tickMarkInsideSize;
        tickMarkOutsideSize = axisConfig.tickMarkOutsideSize;
        tickInterval = axisConfig.tickInterval;
        minorTickIntervalCount = axisConfig.minorTickIntervalCount;

        isTitleVisible = axisConfig.isTitleVisible;
        titlePadding = axisConfig.titlePadding;
        tickLabelTextStyle = new TextStyle(axisConfig.tickLabelTextStyle);
        titleTextStyle = new TextStyle(axisConfig.titleTextStyle);

        isVisible = axisConfig.isVisible;
        isAxisLineVisible = axisConfig.isAxisLineVisible;
        isTickLabelsVisible = axisConfig.isTickLabelsVisible;
        isTitleVisible = axisConfig.isTitleVisible;
        isGridVisible = axisConfig.isGridVisible;
        isMinorGridVisible = axisConfig.isMinorGridVisible;

        isMinMaxRoundingEnabled = axisConfig.isMinMaxRoundingEnabled;
    }

    public boolean isMinMaxRoundingEnabled() {
        return isMinMaxRoundingEnabled;
    }

    public void setMinMaxRoundingEnabled(boolean isMinMaxRoundingEnabled) {
        this.isMinMaxRoundingEnabled = isMinMaxRoundingEnabled;
    }

    public Style getStyle() {
        return style;
    }


    /** ======================= Title ========================== **/

    public int getTitlePadding() {
        return titlePadding;
    }

    public void setTitlePadding(int titlePadding) {
        this.titlePadding = titlePadding;
    }

    public TextStyle getTitleTextStyle() {
        return titleTextStyle;
    }

    /** ======================= Ticks ========================== **/


    public boolean isTickLabelInside() {
        return isTickLabelInside;
    }

    public void setTickLabelInside(boolean tickLabelInside) {
        isTickLabelInside = tickLabelInside;
    }

    public void setTickMarkSize(int insideSize, int outsideSize) {
        this.tickMarkOutsideSize = outsideSize;
        this.tickMarkInsideSize = insideSize;
    }

    public int getTickPadding() {
        return tickPadding;
    }

    public void setTickPadding(int tickPadding) {
        this.tickPadding = tickPadding;
    }

    public int getTickMarkInsideSize() {
        return tickMarkInsideSize;
    }

    public int getTickMarkOutsideSize() {
        return tickMarkOutsideSize;
    }

    public float getTickInterval() {
        return tickInterval;
    }

    public void setTickInterval(float tickInterval) {
        this.tickInterval = tickInterval;
    }

    public TickFormatInfo getTickFormatInfo() {
        return tickFormatInfo;
    }

    public TextStyle getTickLabelTextStyle() {
        return tickLabelTextStyle;
    }

    /** =======================Minor Ticks ========================== **/


    public int getMinorTickMarkInsideSize() {
        return minorTickMarkInsideSize;
    }

    public void setMinorTickMarkSize(int insideSize, int outsideSize) {
        this.minorTickMarkInsideSize = insideSize;
        this.minorTickMarkOutsideSize = outsideSize;
    }

    public int getMinorTickMarkOutsideSize() {
        return minorTickMarkOutsideSize;
    }

    public void setMinorTickIntervalCount(int minorTickIntervalCount) {
        this.minorTickIntervalCount = minorTickIntervalCount;
    }

    public int getMinorTickIntervalCount() {
        return minorTickIntervalCount;
    }


    /** ======================= Visibility ========================== **/


    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public boolean isTitleVisible() {
        return isTitleVisible;
    }

    public void setTitleVisible(boolean titleVisible) {
        isTitleVisible = titleVisible;
    }

    public boolean isTickLabelsVisible() {
        return isTickLabelsVisible;
    }

    public void setTickLabelsVisible(boolean tickLabelsVisible) {
        isTickLabelsVisible = tickLabelsVisible;
    }

    public boolean isGridVisible() {
        return isGridVisible;
    }

    public void setGridVisible(boolean gridVisible) {
        isGridVisible = gridVisible;
    }

    public boolean isMinorGridVisible() {
        return isMinorGridVisible;
    }

    public void setMinorGridVisible(boolean minorGridVisible) {
        isMinorGridVisible = minorGridVisible;
    }

    public boolean isTicksVisible() {
        if(tickMarkOutsideSize == 0 && tickMarkInsideSize == 0) {
            return false;
        }
        return true;
    }


    public boolean isMinorTicksVisible() {
        if(minorTickMarkOutsideSize == 0 && minorTickMarkInsideSize == 0) {
            return false;
        }
        return true;
    }

    public boolean isAxisLineVisible() {
        return isAxisLineVisible;
    }
}
