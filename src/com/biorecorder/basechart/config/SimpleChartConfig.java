package com.biorecorder.basechart.config;

import com.biorecorder.basechart.*;
import com.biorecorder.basechart.config.traces.TraceConfig;
import com.biorecorder.basechart.RangeInt;
import com.biorecorder.basechart.data.Data;
import com.biorecorder.basechart.data.DataSeries;
import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.graphics.BRectangle;
import com.biorecorder.basechart.graphics.BStroke;
import com.biorecorder.basechart.graphics.TextStyle;

import java.util.*;

/**
 * Created by galafit on 18/8/17.
 */
public class SimpleChartConfig {
    private static final int DEFAULT_WEIGHT = 10;

    Data data = new Data();

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
    private Map<Integer, Double> xAxisMins = new HashMap<Integer, Double>();
    private Map<Integer, Double> xAxisMaxs = new HashMap<Integer, Double>();
    private Map<Integer, Double> yAxisMins = new HashMap<Integer, Double>();
    private Map<Integer, Double> yAxisMaxs = new HashMap<Integer, Double>();
    private AxisConfig leftAxisDefaultConfig = new AxisConfig(AxisOrientation.LEFT);
    private AxisConfig rightAxisDefaultConfig = new AxisConfig(AxisOrientation.RIGHT);;
    private boolean isLeftAxisPrimary = true;
    private boolean isBottomAxisPrimary = true;

    // if true chart will intent to draw traces so that every trace point mark
    // occupies the specified in traceConfig markSize (in pixels)
    private boolean isTracesNaturalDrawingEnabled = false;

    private DataProcessingConfig dataProcessingConfig = new DataProcessingConfig();

    private ArrayList<TraceInfo> traces = new ArrayList<TraceInfo>();

    public SimpleChartConfig() {
        xAxisConfigs.add(new AxisConfig(AxisOrientation.BOTTOM));
        xAxisConfigs.add(new AxisConfig(AxisOrientation.TOP));
    }

    public Data getData() {
        return data;
    }

    public DataProcessingConfig getDataProcessingConfig() {
        return dataProcessingConfig;
    }

    public boolean isTracesNaturalDrawingEnabled() {
        return isTracesNaturalDrawingEnabled;
    }

    public void setTracesNaturalDrawingEnabled(boolean tracesNaturalDrawingEnabled) {
        isTracesNaturalDrawingEnabled = tracesNaturalDrawingEnabled;
    }

    public void setLeftAxisPrimary(boolean isLeftAxisPrimary) {
        this.isLeftAxisPrimary = isLeftAxisPrimary;
    }

    public void setBottomAxisPrimary(boolean isBottomAxisPrimary) {
        this.isBottomAxisPrimary = isBottomAxisPrimary;
    }

    public AxisConfig getLeftAxisDefaultConfig() {
        return leftAxisDefaultConfig;
    }

    public AxisConfig getRightAxisDefaultConfig() {
        return rightAxisDefaultConfig;
    }

    public void addStack() {
        addStack(DEFAULT_WEIGHT);
    }

    public void addStack(Double yMin, Double yMax) {
        addStack(DEFAULT_WEIGHT, yMin, yMax);
    }

    /**
     * Set Min and Max of both Y axis of the last stack
     */
    public void addStack(int weight, Double yMin, Double yMax) {
        addStack(weight);
        setYMinMax(yAxisConfigs.size() - 1, yMin, yMax);
        setYMinMax(yAxisConfigs.size() - 2, yMin, yMax);
    }

    public void addStack(int weight) {
        if (leftAxisDefaultConfig == null) {
            this.leftAxisDefaultConfig = new AxisConfig(AxisOrientation.LEFT);
            if (isLeftAxisPrimary) {
                this.leftAxisDefaultConfig.setGridLineStroke(new BStroke(1));
            }
        }
        if (rightAxisDefaultConfig == null) {
            this.rightAxisDefaultConfig = new AxisConfig(AxisOrientation.RIGHT);
            if (!isLeftAxisPrimary) {
                this.rightAxisDefaultConfig.setGridLineStroke(new BStroke(1));
            }
        }
        AxisConfig leftConfig = new AxisConfig(leftAxisDefaultConfig);
        AxisConfig rightConfig = new AxisConfig(rightAxisDefaultConfig);
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
    public void addTrace(TraceConfig traceConfig, DataSeries traceData, String traceName, boolean isXAxisOpposite, boolean isYAxisOpposite) {
        if(yAxisConfigs.size() == 0) {
           addStack();
        }
        if(traceConfig.getColor() == null) {
            traceConfig.setColor(defaultTraceColors[traces.size() % defaultTraceColors.length]);
        }
        data.addSeries(traceData);

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
        traceInfo.setTraceConfig(traceConfig);
        traces.add(traceInfo);
    }

    public BColor getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(BColor titleColor) {
        this.titleColor = titleColor;
    }

    public int xAxisCount() {
        return xAxisConfigs.size();
    }

    public int getYAxisCount() {
        return yAxisConfigs.size();
    }

    public int traceCount() {
        return traces.size();
    }

    public AxisConfig getXConfig(int axisIndex) {
        return xAxisConfigs.get(axisIndex);
    }

    public AxisConfig getYConfig(int axisIndex) {
        return yAxisConfigs.get(axisIndex);
    }

    public void setXMinMax(int xAxisIndex, Double min,  Double max) {
        if(min != null && max != null && min > max) {
            throw new IllegalArgumentException("min = "+ min + " max = " + max + ". Expected: min < max");
        }

        if(min != null) {
            xAxisMins.put(xAxisIndex, min);
        } else {
            xAxisMins.remove(xAxisIndex);
        }

        if(max != null) {
            xAxisMaxs.put(xAxisIndex, max);
        } else {
            xAxisMaxs.remove(xAxisIndex);
        }
    }

    public void setYMinMax(int yAxisIndex, Double min,  Double max) {
        if(min != null && max != null && min > max) {
            throw new IllegalArgumentException("min = "+ min + " max = " + max + ". Expected: min < max");
        }

        if(min != null) {
            yAxisMins.put(yAxisIndex, min);
        } else {
            yAxisMins.remove(yAxisIndex);
        }

        if(max != null) {
            yAxisMaxs.put(yAxisIndex, max);
        } else {
            yAxisMaxs.remove(yAxisIndex);
        }
    }

    public Double getXMin(int xAxisIndex) {
        return xAxisMins.get(xAxisIndex);
    }

    public Double getXMax(int xAxisIndex) {
        return xAxisMaxs.get(xAxisIndex);
    }

    public Double getYMin(int yAxisIndex) {
        return yAxisMins.get(yAxisIndex);
    }

    public Double getYMax(int yAxisIndex) {
        return yAxisMaxs.get(yAxisIndex);
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
        private int xAxisIndex;
        private int yAxisIndex;
        private String name;

        public TraceConfig getTraceConfig() {
            return traceConfig;
        }

        public void setTraceConfig(TraceConfig traceConfig) {
            this.traceConfig = traceConfig;
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
