package com.biorecorder.basechart;

import com.biorecorder.basechart.chart.config.ScrollableChartConfig;
import com.biorecorder.basechart.chart.config.Theme;
import com.biorecorder.basechart.chart.config.traces.TraceConfig;
import com.biorecorder.basechart.data.DataSeries;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by galafit on 6/10/17.
 */
public class ChartConfig extends ScrollableChartConfig {
    private boolean isDataCropEnable = true;
    private boolean isGroupingEnable = true;
    private ArrayList<Float> previewGroupingIntervals = new ArrayList<Float>();
    private List<DataSeries> chartData = new ArrayList<>();
    private List<DataSeries> previewData = new ArrayList<>();
    // true for pc and false for phone
    private boolean isChartGroupedDatCachingEnable = true;

    private boolean autoScrollEnable = true;
    private boolean autosSaleEnableDuringScroll = true; // chart Y auto scale during scrolling


    public ChartConfig(boolean isDateTime) {
        super(isDateTime);
    }

    public ChartConfig(Theme theme, boolean isDateTime) {
        super(theme, isDateTime);
    }

    public boolean isPreviewEnable() {
        if(getScrollsExtents().length > 0 || getPreviewConfig().getTraceCount() > 0) {
            return true;
        }
        return false;
    }

    public boolean isAutoScrollEnable() {
        return autoScrollEnable;
    }

    public void setAutoScrollEnable(boolean autoScrollEnable) {
        this.autoScrollEnable = autoScrollEnable;
    }

    public boolean isAutosSaleEnableDuringScroll() {
        return autosSaleEnableDuringScroll;
    }

    public void setAutosSaleEnableDuringScroll(boolean autosSaleEnableDuringScroll) {
        this.autosSaleEnableDuringScroll = autosSaleEnableDuringScroll;
    }

    public boolean isChartGroupedDatCachingEnable() {
        return isChartGroupedDatCachingEnable;
    }

    public void setChartGroupedDatCachingEnable(boolean isChartGroupedDatCachingEnable) {
        this.isChartGroupedDatCachingEnable = isChartGroupedDatCachingEnable;
    }

    public List<Float> getPreviewGroupingIntervals() {
        return previewGroupingIntervals;
    }

    public void addPreviewGroupingInterval(float groupingInterval) {
        previewGroupingIntervals.add(groupingInterval);
    }

    public boolean isCropEnable() {
        return isDataCropEnable;
    }

    public void setCropEnable(boolean isCropEnable) {
        this.isDataCropEnable = isCropEnable;
    }

    public boolean isGroupingEnable() {
        return isGroupingEnable;
    }

    public void setGroupingEnable(boolean isGroupingEnable) {
        this.isGroupingEnable = isGroupingEnable;
    }

    public List<DataSeries> getChartData() {
        return chartData;
    }

    public List<DataSeries> getPreviewData() {
        return previewData;
    }

    /*********************************************
     *              CHART CONFIG
     *********************************************/

    /**
     * add trace to the last chart stack
     */
    public void addTrace(TraceConfig traceConfig, DataSeries traceData, String name, boolean isXAxisOpposite, boolean isYAxisOpposite) {
        chartData.add(traceData);
        getChartConfig().addTrace(traceConfig, chartData.size() - 1, name,  isXAxisOpposite, isYAxisOpposite);
    }

    /**
     * add trace to the last chart stack
     */
    public void addTrace(TraceConfig traceConfig, DataSeries traceData, String name, String dataUnits, boolean isXAxisOpposite, boolean isYAxisOpposite) {
        addTrace(traceConfig, traceData, name, isXAxisOpposite, isYAxisOpposite);

        int traceYIndex = getChartConfig().getTraceYIndex(getChartConfig().getTraceCount() - 1);
        getChartConfig().getYConfig(traceYIndex).getLabelFormatInfo().setSuffix(dataUnits);
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
     *              PREVIEW CONFIG
     *********************************************/



    /**
     * add trace to the last preview stack
     */
    public void addPreviewTrace(TraceConfig traceConfig, DataSeries traceData, String name,  boolean isXAxisOpposite, boolean isYAxisOpposite) {
        previewData.add(traceData);
        getPreviewConfig().addTrace(traceConfig, previewData.size() - 1, name,  isXAxisOpposite, isYAxisOpposite);
    }

    /**
     * add trace to the last preview stack
     */
    public void addPreviewTrace(TraceConfig traceConfig, DataSeries traceData, String name, String dataUnits, boolean isXAxisOpposite, boolean isYAxisOpposite) {
        addPreviewTrace(traceConfig, traceData, name, isXAxisOpposite, isYAxisOpposite);

        int traceYIndex = getPreviewConfig().getTraceYIndex(getPreviewConfig().getTraceCount() - 1);
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
