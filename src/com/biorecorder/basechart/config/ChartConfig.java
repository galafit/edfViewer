package com.biorecorder.basechart.config;

import com.biorecorder.basechart.BStroke;
import com.biorecorder.basechart.Margin;
import com.biorecorder.basechart.config.traces.TraceConfig;
import com.biorecorder.basechart.data.Data;
import com.biorecorder.basechart.data.DataSeries;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by galafit on 2/12/17.
 */
public class ChartConfig {
    private Data data = new Data();
    private boolean isPreviewEnabled = true;
    private SimpleChartConfig chartConfig;
    private SimpleChartConfig previewConfig;
    private int gapBetweenChartPreview; //px
    private Margin margin;
    private ScrollConfig scrollConfig = new ScrollConfig();
    private Map<Integer, Double> scrollExtents = new Hashtable<Integer, Double>(2);

    private boolean autoScrollEnable = true;
    private boolean autoScaleEnableDuringScroll = true; // chart Y auto scale during scrolling

    private ArrayList<Float> previewGroupingIntervals = new ArrayList<Float>();

    public ChartConfig(boolean isDateTime, boolean isPreviewEnabled) {
        this(Theme.DARK, isDateTime, isPreviewEnabled);
    }

    public ChartConfig(Theme theme, boolean isDateTime, boolean isPreviewEnabled) {
        this.isPreviewEnabled = isPreviewEnabled;
        chartConfig = new SimpleChartConfig();
        previewConfig = new SimpleChartConfig();
        if(isPreviewEnabled) {
            chartConfig.setTracesSpreadEnabled(false);
            chartConfig.getDataProcessingConfig().setCropToAvailableSpaceEnabled(true);
            previewConfig.addStack();
            previewConfig.getXConfig(0).setVisible(true);
            previewConfig.setTracesSpreadEnabled(false);
            previewConfig.getDataProcessingConfig().setCropToAvailableSpaceEnabled(false);
        }

        AxisConfig leftAxisConfig = chartConfig.getLeftAxisDefaultConfig();
        AxisConfig rightAxisConfig = chartConfig.getRightAxisDefaultConfig();
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

        leftAxisConfig = previewConfig.getLeftAxisDefaultConfig();
        rightAxisConfig = previewConfig.getRightAxisDefaultConfig();
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

    public Data getData() {
        return data;
    }

    public boolean isAutoScrollEnable() {
        return autoScrollEnable;
    }

    public void setAutoScrollEnable(boolean autoScrollEnable) {
        this.autoScrollEnable = autoScrollEnable;
    }

    public boolean isAutoScaleEnableDuringScroll() {
        return autoScaleEnableDuringScroll;
    }

    public void setAutoScaleEnableDuringScroll(boolean autoScaleEnableDuringScroll) {
        this.autoScaleEnableDuringScroll = autoScaleEnableDuringScroll;
    }

    public boolean isPreviewEnabled() {
        return isPreviewEnabled;
    }

    public SimpleChartConfig getBaseChartConfig() {
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

    public ScrollConfig getScrollConfig() {
        return scrollConfig;
    }

    public Double getScrollExtent(int xAxisIndex) {
        return scrollExtents.get(xAxisIndex);
    }

    public void addScroll(int xAxisIndex, double extent) {
        scrollExtents.put(xAxisIndex, extent);
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

    /*********************************************
     *              CHART TRACES
     *********************************************/

    /**
     * add trace to the last chart stack
     */
    public void addTrace(TraceConfig traceConfig, DataSeries traceData, String name, boolean isXAxisOpposite, boolean isYAxisOpposite) {
        chartConfig.addTrace(traceConfig, data.addDataSeries(traceData), name,  isXAxisOpposite, isYAxisOpposite);
    }

    /**
     * add trace to the last chart stack
     */
    public void addTrace(TraceConfig traceConfig, DataSeries traceData, String name, String dataUnits, boolean isXAxisOpposite, boolean isYAxisOpposite) {
        addTrace(traceConfig, traceData, name, isXAxisOpposite, isYAxisOpposite);

        int traceYIndex = getBaseChartConfig().getTraceYIndex(getBaseChartConfig().traceCount() - 1);
        getBaseChartConfig().getYConfig(traceYIndex).getLabelFormatInfo().setSuffix(dataUnits);
    }

    /**
     * add trace to the last chart stack
     */
    public void addTrace(TraceConfig traceConfig, DataSeries traceData, boolean isXAxisOpposite, boolean isYAxisOpposite) {
        addTrace(traceConfig, traceData, null, isXAxisOpposite, isYAxisOpposite);
    }

    /**
     * add trace to the last chart stack
     */
    public void addTrace(TraceConfig traceConfig, DataSeries traceData, String name, String dataUnits) {
        addTrace(traceConfig, traceData, name, dataUnits, false, false);
    }

    /**
     * add trace to the last chart stack
     */
    public void addTrace(TraceConfig traceConfig, DataSeries traceData, String name) {
        addTrace(traceConfig, traceData, name, false, false);
    }

    /**
     * add trace to the last chart stack
     */
    public void addTrace(TraceConfig traceConfig, DataSeries traceData) {
        addTrace(traceConfig, traceData, null, false, false);
    }


    /*********************************************
     *              PREVIEW TRACES
     *********************************************/



    /**
     * add trace to the last preview stack
     */
    public void addPreviewTrace(TraceConfig traceConfig, DataSeries traceData, String name,  boolean isXAxisOpposite, boolean isYAxisOpposite) {
        getPreviewConfig().addTrace(traceConfig, data.addDataSeries(traceData), name,  isXAxisOpposite, isYAxisOpposite);
    }

    /**
     * add trace to the last preview stack
     */
    public void addPreviewTrace(TraceConfig traceConfig, DataSeries traceData, String name, String dataUnits, boolean isXAxisOpposite, boolean isYAxisOpposite) {
        addPreviewTrace(traceConfig, traceData, name, isXAxisOpposite, isYAxisOpposite);

        int traceYIndex = getPreviewConfig().getTraceYIndex(getPreviewConfig().traceCount() - 1);
        getPreviewConfig().getYConfig(traceYIndex).getLabelFormatInfo().setSuffix(dataUnits);
    }

    /**
     * add trace to the last preview stack
     */
    public void addPreviewTrace(TraceConfig traceConfig, DataSeries traceData, String name, String dataUnits) {
        addPreviewTrace(traceConfig, traceData, name, dataUnits, false, false);
    }


    /**
     * add trace to the last preview stack
     */
    public void addPreviewTrace(TraceConfig traceConfig, DataSeries data, String name) {
        addPreviewTrace(traceConfig, data, name, false, false);
    }

    /**
     * add trace to the last preview stack
     */
    public void addPreviewTrace(TraceConfig traceConfig, DataSeries data) {
        addPreviewTrace(traceConfig, data, null, false, false);
    }
}
