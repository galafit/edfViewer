package com.biorecorder.basechart.chart.config;

import com.biorecorder.basechart.chart.*;
import com.biorecorder.basechart.chart.config.traces.TraceConfig;
import com.biorecorder.basechart.chart.RangeInt;

import java.util.*;

/**
 * Created by galafit on 18/8/17.
 */
public class SimpleChartConfig {
    private static final int DEFAULT_WEIGHT = 10;

    private boolean isDataCropEnable = true;
    private boolean isGroupingEnable = true;
    private int minPixelsPerDataPoint = 1;

    private BColor[] defaultTraceColors = {BColor.MAGENTA, BColor.BLUE};
    private String title;
    private BColor background;
    private BColor marginColor;
    private Margin margin;
    private TextStyle titleTextStyle = new TextStyle(TextStyle.DEFAULT, TextStyle.BOLD, 14);
    private BColor titleColor = BColor.BLACK;
    private LegendConfig legendConfig = new LegendConfig();
    private TooltipConfig tooltipConfig = new TooltipConfig();
    private CrosshairConfig crosshairConfig = new CrosshairConfig();
    private ArrayList<Integer> stackWeights = new ArrayList<Integer>();
    /*
     * 2 X-axis: 0(even) - BOTTOM and 1(odd) - TOP
     * 2 Y-axis for every section(stack): even - LEFT and odd - RIGHT;
     * All LEFT and RIGHT Y-axis are stacked.
     * If there is no trace associated with some axis... this axis is invisible.
     **/
    private ArrayList<AxisConfig> xAxisConfigs = new ArrayList<AxisConfig>();
    private ArrayList<AxisConfig> yAxisConfigs = new ArrayList<AxisConfig>();
    private Map<Integer, Range> xAxisExtremes = new HashMap<Integer, Range>();
    private Map<Integer, Range> yAxisExtremes = new HashMap<Integer, Range>();
    private AxisConfig leftAxisConfig = new AxisConfig(AxisOrientation.LEFT);
    private AxisConfig rightAxisConfig = new AxisConfig(AxisOrientation.RIGHT);;
    private boolean isLeftAxisPrimary = true;
    private boolean isBottomAxisPrimary = true;

    private ArrayList<TraceInfo> traces = new ArrayList<TraceInfo>();

    public SimpleChartConfig() {
        xAxisConfigs.add(new AxisConfig(AxisOrientation.BOTTOM));
        xAxisConfigs.add(new AxisConfig(AxisOrientation.TOP));
    }

    public boolean isDataCropEnable() {
        return isDataCropEnable;
    }

    public int getMinPixelsPerDataPoint() {
        return minPixelsPerDataPoint;
    }

    public void setMinPixelsPerDataPoint(int minPixelsPerDataPoint) {
        this.minPixelsPerDataPoint = minPixelsPerDataPoint;
    }

    public void setDataCropEnable(boolean dataCropEnable) {
        isDataCropEnable = dataCropEnable;
    }

    public boolean isGroupingEnable() {
        return isGroupingEnable;
    }

    public void setGroupingEnable(boolean groupingEnable) {
        isGroupingEnable = groupingEnable;
    }

    public void setLeftAxisPrimary(boolean isLeftAxisPrimary) {
        this.isLeftAxisPrimary = isLeftAxisPrimary;
    }

    public void setBottomAxisPrimary(boolean isBottomAxisPrimary) {
        this.isBottomAxisPrimary = isBottomAxisPrimary;
    }

    public AxisConfig getLeftAxisConfig() {
        return leftAxisConfig;
    }

    public AxisConfig getRightAxisConfig() {
        return rightAxisConfig;
    }

    public void addStack() {
        addStack(DEFAULT_WEIGHT);
    }

    public void addStack(Range yMinMax) {
        addStack(DEFAULT_WEIGHT, yMinMax);
    }

    /**
     * Set Min and Max of both Y axis of the last stack
     * @param yMinMax  - min and max values. Can be null. If min == null
     *  only max will be set and vise versa
     */
    public void addStack(int weight, Range yMinMax) {
        addStack(weight);
        setYMinMax(yAxisConfigs.size() - 1, yMinMax);
        setYMinMax(yAxisConfigs.size() - 2, yMinMax);
    }

    public void addStack(int weight) {
        if (leftAxisConfig == null) {
            this.leftAxisConfig = new AxisConfig(AxisOrientation.LEFT);
            if (isLeftAxisPrimary) {
                this.leftAxisConfig.setGridLineStroke(new BStroke(1));
            }
        }
        if (rightAxisConfig == null) {
            this.rightAxisConfig = new AxisConfig(AxisOrientation.RIGHT);
            if (!isLeftAxisPrimary) {
                this.rightAxisConfig.setGridLineStroke(new BStroke(1));
            }
        }
        AxisConfig leftConfig = new AxisConfig(leftAxisConfig);
        AxisConfig rightConfig = new AxisConfig(rightAxisConfig);
        yAxisConfigs.add(leftConfig);
        yAxisConfigs.add(rightConfig);
        stackWeights.add(weight);
    }

    public void setDefaultTraceColors(BColor[] defaultTraceColors) {
        this.defaultTraceColors = defaultTraceColors;
    }

    public int getSumWeight() {
        int weightSum = 0;
        for (Integer weight : stackWeights) {
            weightSum += weight;
        }
        return weightSum;
    }

    public RangeInt getYStartEnd(int yAxisIndex, BRectangle area) {
        int weightSum = getSumWeight();

        int weightSumTillYAxis = 0;
        for (int i = 0; i < yAxisIndex / 2; i++) {
            weightSumTillYAxis += stackWeights.get(i);
        }

        int yAxisWeight = stackWeights.get(yAxisIndex / 2);
        int axisHeight = area.height * yAxisWeight / weightSum;

        int end = area.y + area.height * weightSumTillYAxis / weightSum;
        int start = end + axisHeight;
        return new RangeInt(end, start);
    }


    // add trace to the last stack
    public void addTrace(TraceConfig traceConfig, int dataIndex, String traceName, boolean isXAxisOpposite, boolean isYAxisOpposite) {
        if(yAxisConfigs.size() == 0) {
           addStack();
        }
        if(traceConfig.getColor() == null) {
            traceConfig.setColor(defaultTraceColors[traces.size() % defaultTraceColors.length]);
        }

        boolean isBottomXAxis = true;
        boolean isLeftYAxis = true;
        if (isXAxisOpposite && isBottomAxisPrimary) {
            isBottomXAxis = false;
        }
        if (!isXAxisOpposite && !isBottomAxisPrimary) {
            isBottomXAxis = false;
        }
        if (isYAxisOpposite && isLeftAxisPrimary) {
            isLeftYAxis = false;
        }
        if (!isYAxisOpposite && !isLeftAxisPrimary) {
            isLeftYAxis = false;
        }
        int xAxisIndex = isBottomXAxis ? 0 : 1;
        int yAxisIndex = isLeftYAxis ? yAxisConfigs.size() - 2 : yAxisConfigs.size() - 1;

        xAxisConfigs.get(xAxisIndex).setVisible(true);
        yAxisConfigs.get(yAxisIndex).setVisible(true);
        TraceInfo traceInfo = new TraceInfo();
        String name = (traceName != null) ? traceName : "Trace " + traces.size();
        traceInfo.setName(name);
        traceInfo.setXAxisIndex(xAxisIndex);
        traceInfo.setYAxisIndex(yAxisIndex);
        traceInfo.setDataIndex(dataIndex);
        traceInfo.setTraceConfig(traceConfig);
        traces.add(traceInfo);
    }

    public BColor getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(BColor titleColor) {
        this.titleColor = titleColor;
    }

    public int getXAxisCount() {
        return xAxisConfigs.size();
    }

    public int getYAxisCount() {
        return yAxisConfigs.size();
    }

    public int getTraceCount() {
        return traces.size();
    }

    public AxisConfig getXConfig(int axisIndex) {
        return xAxisConfigs.get(axisIndex);
    }

    public AxisConfig getYConfig(int axisIndex) {
        return yAxisConfigs.get(axisIndex);
    }

    public void setXMinMax(int xAxisIndex, Range minMax) {
        xAxisExtremes.put(xAxisIndex, minMax);
    }

    public void setYMinMax(int yAxisIndex, Range minMax) {
        yAxisExtremes.put(yAxisIndex, minMax);
    }

    public Range getXMinMax(int xAxisIndex) {
        return xAxisExtremes.get(xAxisIndex);
    }

    public Range getYMinMax(int xAxisIndex) {
        return yAxisExtremes.get(xAxisIndex);
    }

    public TraceConfig getTraceConfig(int index) {
        return traces.get(index).getTraceConfig();
    }

    public String getTraceName(int traceIndex) {
        return traces.get(traceIndex).getName();
    }

    public int getTraceXIndex(int traceIndex) {
        return traces.get(traceIndex).getXAxisIndex();
    }

    public int getTraceYIndex(int traceIndex) {
        return traces.get(traceIndex).getYAxisIndex();
    }

    public int getTraceDataIndex(int traceIndex) {
        return traces.get(traceIndex).getDataIndex();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BColor getBackground() {
        return background;
    }

    public void setBackground(BColor background) {
        this.background = background;
    }

    public BColor getMarginColor() {
        return marginColor;
    }

    public void setMarginColor(BColor marginColor) {
        this.marginColor = marginColor;
    }

    public Margin getMargin() {
        return margin;
    }

    public void setMargin(Margin margin) {
        this.margin = margin;
    }

    public TextStyle getTitleTextStyle() {
        return titleTextStyle;
    }

    public LegendConfig getLegendConfig() {
        return legendConfig;
    }

    public TooltipConfig getTooltipConfig() {
        return tooltipConfig;
    }

    public CrosshairConfig getCrosshairConfig() {
        return crosshairConfig;
    }

    class TraceInfo {
        private TraceConfig traceConfig;
        private int dataIndex;
        private int xAxisIndex;
        private int yAxisIndex;
        private String name;

        public TraceConfig getTraceConfig() {
            return traceConfig;
        }

        public void setTraceConfig(TraceConfig traceConfig) {
            this.traceConfig = traceConfig;
        }

        public int getDataIndex() {
            return dataIndex;
        }

        public void setDataIndex(int dataIndex) {
            this.dataIndex = dataIndex;
        }

        public int getXAxisIndex() {
            return xAxisIndex;
        }

        public void setXAxisIndex(int xAxisIndex) {
            this.xAxisIndex = xAxisIndex;
        }

        public int getYAxisIndex() {
            return yAxisIndex;
        }

        public void setYAxisIndex(int yAxisIndex) {
            this.yAxisIndex = yAxisIndex;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
