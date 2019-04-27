package com.biorecorder.basechart.axis;

import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.graphics.BStroke;
import com.biorecorder.basechart.TextStyle;
import com.sun.istack.internal.Nullable;

/**
 * Created by galafit on 5/9/17.
 */
public class AxisConfig {
    private BColor axisLineColor =  BColor.GRAY;
    private BStroke axisLineStroke = new BStroke(1); // if width = 0 line will not be drawn

    private int tickPadding; // (px) padding between tick mark and tick label
    private TextStyle tickLabelTextStyle = new TextStyle(TextStyle.DEFAULT, TextStyle.NORMAL, 12);

    private int titlePadding; // px
    private TextStyle titleTextStyle = new TextStyle(TextStyle.DEFAULT, TextStyle.NORMAL, 12);
    private BColor titleColor = BColor.GRAY;

    private double tickInterval = -1; // in axis domain units (If <= 0 will not be taken into account)

    private int tickMarkInsideSize = 0; // px
    private int tickMarkOutsideSize = 3; // px
    private int tickMarkWidth = 1; // px
    private BColor tickMarkColor = BColor.GRAY;
    private BColor tickLabelColor = BColor.GRAY;
    private boolean isTickLabelCentered = true;

    private LabelPrefixAndSuffix tickLabelPrefixAndSuffix;

    private int minorTickMarkWidth = 1; // px
    private BColor minorTickMarkColor = BColor.GRAY;
    private BStroke gridLineStroke = new BStroke(1);
    private BColor gridColor = BColor.GRAY_LIGHT;

    private BStroke minorGridLineStroke = new BStroke(1, BStroke.DOT);
    private BColor minorGridColor = BColor.GRAY_LIGHT;
    private int minorTickMarkInsideSize = 0; // px
    private int minorTickMarkOutsideSize = 0; // px
    private int minorTickIntervalCount = 0; // number of minor intervals in one major interval
    private boolean isRoundingEnabled = false;
    private boolean isTickLabelOutside = true;


    public AxisConfig() {
        titlePadding = titleTextStyle.getSize() / 2;
        tickPadding = tickLabelTextStyle.getSize() / 2;
    }

    public AxisConfig(AxisConfig axisConfig) {
        axisLineColor = axisConfig.axisLineColor;
        axisLineStroke = new BStroke(axisConfig.axisLineStroke);

        gridColor = axisConfig.gridColor;
        minorGridColor = axisConfig.minorGridColor;
        gridLineStroke = new BStroke(axisConfig.gridLineStroke);
        minorGridLineStroke = new BStroke(axisConfig.minorGridLineStroke);

        tickPadding = axisConfig.tickPadding;
        tickMarkWidth = axisConfig.tickMarkWidth;
        tickMarkInsideSize = axisConfig.tickMarkInsideSize;
        tickMarkOutsideSize = axisConfig.tickMarkOutsideSize;
        tickMarkColor = axisConfig.tickMarkColor;
        tickLabelColor = axisConfig.tickLabelColor;
        tickLabelTextStyle = axisConfig.tickLabelTextStyle;
        tickInterval = axisConfig.tickInterval;
        tickLabelPrefixAndSuffix = axisConfig.tickLabelPrefixAndSuffix;
        isTickLabelCentered = axisConfig.isTickLabelCentered;

        minorTickMarkWidth = axisConfig.minorTickMarkWidth;
        minorTickMarkOutsideSize = axisConfig.minorTickMarkOutsideSize;
        minorTickMarkInsideSize = axisConfig.minorTickMarkInsideSize;
        minorTickMarkColor = axisConfig.minorTickMarkColor;
        minorTickIntervalCount = axisConfig.minorTickIntervalCount;
        isRoundingEnabled = axisConfig.isRoundingEnabled;

        titlePadding = axisConfig.titlePadding;
        titleTextStyle = axisConfig.titleTextStyle;
        titleColor = axisConfig.titleColor;

        isTickLabelOutside = axisConfig.isTickLabelOutside;
    }


    public boolean isRoundingEnabled() {
        return isRoundingEnabled;
    }

    public void setRoundingEnabled(boolean roundingEnabled) {
        isRoundingEnabled = roundingEnabled;
    }

    public void setTickInterval(double tickInterval) {
        this.tickInterval = tickInterval;
    }

    public void setLabelPrefixAndSuffix(@Nullable String prefix, @Nullable String suffix) {
        tickLabelPrefixAndSuffix = new LabelPrefixAndSuffix(prefix, suffix);
    }

    public boolean isTickLabelCentered() {
        return isTickLabelCentered;
    }

    public void setTickLabelCentered(boolean tickLabelCentered) {
        isTickLabelCentered = tickLabelCentered;
    }

    public double getTickInterval() {
        return tickInterval;
    }

    public LabelPrefixAndSuffix getTickLabelPrefixAndSuffix() {
        return tickLabelPrefixAndSuffix;
    }

    public int getMinorTickIntervalCount() {
        return minorTickIntervalCount;
    }

    public void setMinorTickIntervalCount(int minorTickIntervalCount) {
        this.minorTickIntervalCount = minorTickIntervalCount;
    }

    public void setColors(BColor baseColor, BColor gridColor, BColor minorGridColor) {
        axisLineColor = baseColor;
        tickLabelColor = baseColor;
        tickMarkColor = baseColor;
        minorTickMarkColor = baseColor;
        titleColor = baseColor;
        this.gridColor = gridColor;
        this.minorGridColor = minorGridColor;
    }

    public void setLineColor(BColor lineColor) {
        this.axisLineColor = lineColor;
    }

    public BColor getAxisLineColor() {
        return axisLineColor;
    }

    public void setAxisLineColor(BColor lineColor) {
        this.axisLineColor = lineColor;
    }

    public BStroke getAxisLineStroke() {
        return axisLineStroke;
    }

    public void setAxisLineStroke(BStroke axisLineStroke) {
        this.axisLineStroke = axisLineStroke;
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

    public BColor getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(BColor titleColor) {
        this.titleColor = titleColor;
    }



    /** ======================= Ticks ========================== **/


    public boolean isTickLabelOutside() {
        return isTickLabelOutside;
    }

    public void setTickLabelOutside(boolean isTickLabelOutside) {
        this.isTickLabelOutside = isTickLabelOutside;

    }

    public void setTickMarkSize(int insideSize, int outsideSize) {
        this.tickMarkOutsideSize = outsideSize;
        this.tickMarkInsideSize = insideSize;
    }

    public int getTickMarkInsideSize() {
        return tickMarkInsideSize;
    }

    public int getTickMarkOutsideSize() {
        return tickMarkOutsideSize;
    }

    public void setTickMarkColor(BColor tickMarkColor) {
        this.tickMarkColor = tickMarkColor;
    }

    public BColor getTickMarkColor() {
        return tickMarkColor;
    }

    public int getTickMarkWidth() {
        return tickMarkWidth;
    }

    public void setTickMarkWidth(int tickMarkWidth) {
        this.tickMarkWidth = tickMarkWidth;
    }

    public int getTickPadding() {
        return tickPadding;
    }

    public void setTickPadding(int tickPadding) {
        this.tickPadding = tickPadding;
    }


    public TextStyle getTickLabelTextStyle() {
        return tickLabelTextStyle;
    }

    public BColor getTickLabelColor() {
        return tickLabelColor;
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

    public void setMinorTickMarkColor(BColor minorTickMarkColor) {
        this.minorTickMarkColor = minorTickMarkColor;
    }

    public BColor getMinorTickMarkColor() {
        return minorTickMarkColor;
    }

    public BStroke getMinorGridLineStroke() {
        return minorGridLineStroke;
    }

    public void setMinorGridLineStroke(BStroke minorGridLineStroke) {
        this.minorGridLineStroke = minorGridLineStroke;
    }


    public int getMinorTickMarkWidth() {
        return minorTickMarkWidth;
    }

    public void setMinorTickMarkWidth(int minorTickMarkWidth) {
        this.minorTickMarkWidth = minorTickMarkWidth;
    }


    /** ======================= Grid ========================== **/


    public void setGridColor(BColor gridColor) {
        this.gridColor = gridColor;
    }

    public void setMinorGridColor(BColor minorGridColor) {
        this.minorGridColor = minorGridColor;
    }

    public BColor getGridColor() {
        return gridColor;
    }

    public BColor getMinorGridColor() {
        return minorGridColor;
    }

    public BStroke getGridLineStroke() {
        return gridLineStroke;
    }

    public void setGridLineStroke(BStroke gridLineStroke) {
        this.gridLineStroke = gridLineStroke;
    }

}
