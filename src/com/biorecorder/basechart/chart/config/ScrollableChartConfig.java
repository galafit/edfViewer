package com.biorecorder.basechart.chart.config;

import com.biorecorder.basechart.chart.BStroke;
import com.biorecorder.basechart.chart.Margin;
import com.biorecorder.basechart.chart.Range;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * Created by galafit on 2/12/17.
 */
public class ScrollableChartConfig {

    private SimpleChartConfig chartConfig;
    private SimpleChartConfig previewConfig;
    private int gapBetweenChartPreview; //px
    private Margin margin;
    private ScrollConfig scrollConfig = new ScrollConfig();
    private Map<Integer, Double> scrollExtents = new Hashtable<Integer, Double>(2);
    private Range previewMinMax;

    public ScrollableChartConfig() {
        this(Theme.DARK, true);
    }

    public ScrollableChartConfig(boolean isDateTime) {
        this(Theme.DARK, isDateTime);
    }

    public ScrollableChartConfig(Theme theme, boolean isDateTime) {
        chartConfig = new SimpleChartConfig();
        previewConfig = new SimpleChartConfig();

        AxisConfig leftAxisConfig = chartConfig.getLeftAxisConfig();
        AxisConfig rightAxisConfig = chartConfig.getRightAxisConfig();
        leftAxisConfig.setMinMaxRoundingEnable(true);
        leftAxisConfig.setLabelInside(true);
        leftAxisConfig.setTickMarkInsideSize(3);
        leftAxisConfig.setTickMarkOutsideSize(0);
        leftAxisConfig.setGridLineStroke(new BStroke(0));
        rightAxisConfig.setGridLineStroke(new BStroke(1));
        leftAxisConfig.setColor(theme.getAxisColor());
        leftAxisConfig.setGridColor(theme.getGridColor());
        leftAxisConfig.setMinorGridColor(theme.getGridColor());

        rightAxisConfig.setLabelInside(true);
        rightAxisConfig.setTickMarkInsideSize(3);
        rightAxisConfig.setTickMarkOutsideSize(0);
        rightAxisConfig.setMinMaxRoundingEnable(true);
        rightAxisConfig.setColor(theme.getAxisColor());
        rightAxisConfig.setGridColor(theme.getGridColor());
        rightAxisConfig.setMinorGridColor(theme.getGridColor());

        chartConfig.setBottomAxisPrimary(false);
        chartConfig.setLeftAxisPrimary(false);

        leftAxisConfig = previewConfig.getLeftAxisConfig();
        rightAxisConfig = previewConfig.getRightAxisConfig();
        leftAxisConfig.setMinMaxRoundingEnable(true);
        leftAxisConfig.setLabelInside(true);
        leftAxisConfig.setTickMarkInsideSize(3);
        leftAxisConfig.setTickMarkOutsideSize(0);
        leftAxisConfig.setGridLineStroke(new BStroke(0));
        rightAxisConfig.setGridLineStroke(new BStroke(1));
        leftAxisConfig.setColor(theme.getAxisColor());
        leftAxisConfig.setGridColor(theme.getGridColor());
        leftAxisConfig.setMinorGridColor(theme.getGridColor());

        rightAxisConfig.setLabelInside(true);
        rightAxisConfig.setTickMarkInsideSize(3);
        rightAxisConfig.setTickMarkOutsideSize(0);
        rightAxisConfig.setMinMaxRoundingEnable(true);
        rightAxisConfig.setColor(theme.getAxisColor());
        rightAxisConfig.setGridColor(theme.getGridColor());
        rightAxisConfig.setMinorGridColor(theme.getGridColor());

        previewConfig.setLeftAxisPrimary(false);

        AxisConfig chartBottomAxisConfig = chartConfig.getXConfig(0);
        chartBottomAxisConfig.setColor(theme.getAxisColor());
        chartBottomAxisConfig.setGridColor(theme.getGridColor());
        chartBottomAxisConfig.setMinorGridColor(theme.getGridColor());

        AxisConfig chartTopAxisConfig = chartConfig.getXConfig(1);
        chartTopAxisConfig.setColor(theme.getAxisColor());
        chartTopAxisConfig.setGridColor(theme.getGridColor());
        chartTopAxisConfig.setMinorGridColor(theme.getGridColor());

        AxisConfig previewBottomAxisConfig = previewConfig.getXConfig(0);
        previewBottomAxisConfig.setColor(theme.getAxisColor());
        previewBottomAxisConfig.setGridColor(theme.getGridColor());
        previewBottomAxisConfig.setMinorGridColor(theme.getGridColor());

        AxisConfig previewTopAxisConfig = previewConfig.getXConfig(1);
        previewTopAxisConfig.setColor(theme.getAxisColor());
        previewTopAxisConfig.setGridColor(theme.getGridColor());
        previewTopAxisConfig.setMinorGridColor(theme.getGridColor());

        if(isDateTime) {
            chartBottomAxisConfig.setAxisType(AxisType.TIME);
            chartTopAxisConfig.setAxisType(AxisType.TIME);

            previewBottomAxisConfig.setAxisType(AxisType.TIME);
            previewTopAxisConfig.setAxisType(AxisType.TIME);
         }

        chartConfig.setBackground(theme.getChartBgColor());
        chartConfig.setMarginColor(theme.getChartMarginColor());
        chartConfig.getLegendConfig().setBackgroundColor(theme.getChartBgColor());
        chartConfig.setTitleColor(theme.getTitleColor());
        chartConfig.setDefaultTraceColors(theme.getTraceColors());
        chartConfig.getCrosshairConfig().setLineColor(theme.getCrosshairColor());

        previewConfig.setBackground(theme.getPreviewBgColor());
        previewConfig.setMarginColor(theme.getPreviewMarginColor());
        previewConfig.getLegendConfig().setBackgroundColor(theme.getPreviewBgColor());
        previewConfig.setTitleColor(theme.getTitleColor());
        previewConfig.setDefaultTraceColors(theme.getTraceColors());
        previewConfig.getCrosshairConfig().setLineColor(theme.getCrosshairColor());

        scrollConfig.setScrollColor(theme.getScrollColor());
    }

    public SimpleChartConfig getChartConfig() {
        return chartConfig;
    }

    public SimpleChartConfig getPreviewConfig() {
        return previewConfig;
    }

    public int getGapBetweenChartPreview() {
        return gapBetweenChartPreview;
    }

    public void setGapBetweenChartPreview(int gapBetweenChartPreview) {
        this.gapBetweenChartPreview = gapBetweenChartPreview;
    }

    public Margin getMargin() {
        return margin;
    }

    public void setMargin(Margin margin) {
        this.margin = margin;
    }

    public void setPreviewMinMax(Range minMax) {
      previewMinMax = minMax;
        for (int i = 0; i < previewConfig.getXAxisCount(); i++) {
            previewConfig.setXMin(i, minMax.getMin());
            previewConfig.setXMax(i, minMax.getMax());
        }
    }

    public Range getPreviewMinMax() {
        return previewMinMax;
    }

    public ScrollConfig getScrollConfig() {
        return scrollConfig;
    }

    public Double getScrollExtent(int xAxisIndex) {
        return scrollExtents.get(xAxisIndex);
    }

    public double[] getScrollsExtents() {
        double[] extents = new double[scrollExtents.keySet().size()];
        int i = 0;
        for (Integer xAxisIndex : scrollExtents.keySet()) {
            extents[i] = scrollExtents.get(xAxisIndex);
            i++;
        }
        return extents;
    }

    public void addScroll(int xAxisIndex, double extent) {
        scrollExtents.put(xAxisIndex, extent);
    }

    public Set<Integer> getXAxisWithScroll() {
        return scrollExtents.keySet();
    }

    public void addChartStack(int weight, double min, double max) {
        chartConfig.addStack(weight, min, max);
    }

    public void addChartStack(double min, double max) {
        chartConfig.addStack(min, max);
    }

    public void addChartStack(int weight) {
        chartConfig.addStack(weight);

    }

    public void addChartStack() {
        chartConfig.addStack();
    }

    public void addPreviewStack(int weight, double min, double max) {
        previewConfig.addStack(weight, min, max);
    }

    public void addPreviewStack(double min, double max) {
        previewConfig.addStack(min, max);
    }


    public void addPreviewStack(int weight) {
        previewConfig.addStack(weight);
    }

    public void addPreviewStack() {
        previewConfig.addStack();
    }
}
