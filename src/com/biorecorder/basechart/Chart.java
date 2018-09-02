package com.biorecorder.basechart;

import com.biorecorder.basechart.axis.*;
import com.biorecorder.basechart.button.StateListener;
import com.biorecorder.basechart.button.SwitchButton;
import com.biorecorder.basechart.button.ButtonGroup;
import com.biorecorder.basechart.graphics.*;
import com.biorecorder.basechart.scales.LinearScale;
import com.biorecorder.basechart.scales.Scale;
import com.biorecorder.basechart.traces.Trace;
import com.biorecorder.basechart.data.DataSeries;

import java.util.*;
import java.util.List;

/**
 * Created by hdablin on 24.03.17.
 */
public class Chart {
    private String title;
    private static final int DEFAULT_WEIGHT = 10;

    private HorizontalAlign legendHAlign = HorizontalAlign.LEFT;
    private VerticalAlign legendVAlign = VerticalAlign.TOP;

    private boolean isLegendVisible = true;
    private boolean isTitleVisible = true;

    /*
 * 2 X-axis: 0(even) - BOTTOM and 1(odd) - TOP
 * 2 Y-axis for every section(stack): even - LEFT and odd - RIGHT;
 * All LEFT and RIGHT Y-axis are stacked.
 * If there is no trace associated with some axis... this axis is invisible.
 **/
    private List<Axis> xAxisList = new ArrayList<Axis>(2);
    private List<Axis> yAxisList = new ArrayList<Axis>();

    private Map<Integer, Double> xAxisMins = new HashMap<Integer, Double>();
    private Map<Integer, Double> xAxisMaxs = new HashMap<Integer, Double>();
    private Map<Integer, Double> yAxisMins = new HashMap<Integer, Double>();
    private Map<Integer, Double> yAxisMaxs = new HashMap<Integer, Double>();

    // if true chart will intent to draw traces so that every trace point mark
    // occupies the specified in traceConfig markSize (in pixels)
    private boolean isTracesNaturalDrawingEnabled = false;

    private boolean isLeftAxisPrimary = true;
    private boolean isBottomAxisPrimary = true;

    private boolean isYAxisMinMaxRoundingEnabled = true;

    private ArrayList<Integer> stackWeights = new ArrayList<Integer>();

    private ChartConfig chartConfig = new ChartConfig();
    private DataProcessingConfig dataProcessingConfig = new DataProcessingConfig();

    private List<Trace> traces = new ArrayList<Trace>();
    private List<Legend> legends = new ArrayList<Legend>();
    private Title titleText;
    private BRectangle fullArea;
    private BRectangle titleArea;
    private BRectangle chartArea;
    private BRectangle graphArea;
    private Margin margin;
    private DataManager dataManager;

    /** Dirty data in js means that the data have been changed recently and
     ** DOM haven't been re-rendered according to this changes yet.
     **/
    private boolean dataDirty = true;
    private boolean areasDirty = true;

    private Crosshair crosshair;
    private Tooltip tooltip;

    private int selectedTraceIndex = -1;
    private int hoverPointIndex = -1;
    private int hoverTraceIndex = -1;

    private ButtonGroup buttonGroup = new ButtonGroup();

    public Chart(ChartConfig chartConfig1) {
        this.chartConfig = new ChartConfig(chartConfig1);
        dataManager = new DataManager(dataProcessingConfig);

        xAxisList.add(new AxisBottom(new LinearScale(), chartConfig.getBottomAxisConfig()));
        xAxisList.add(new AxisTop(new LinearScale(), chartConfig.getTopAxisConfig()));
        if(isBottomAxisPrimary) {
            xAxisList.get(0).setGridVisible(true);
        } else {
            xAxisList.get(1).setGridVisible(true);
        }

        // titleText
        titleText = new Title(title, chartConfig.getTitleConfig());

        // tooltips
        tooltip = new Tooltip(chartConfig.getTooltipConfig());
        crosshair = new Crosshair(chartConfig.getCrossHairConfig());
    }


    private void initiateAxisAndTraces() {
        // calculate and set min and max for every xAxis
        for (int xIndex = 0; xIndex < xAxisList.size(); xIndex++) {
            Range xExtremes = null;
            Double min = xAxisMins.get(xIndex);
            Double max = xAxisMaxs.get(xIndex);
            if (min != null && max != null) {
                xExtremes = new Range(min, max);
            } else if (min != null && max == null) {
                xExtremes = new Range(min, min);
            } else if (min == null && max != null) {
                xExtremes = new Range(max, max);
            }

            for (int i = 0; i < traces.size(); i++) {
                if(traces.get(i).getXAxisIndex() == xIndex) {
                    Range traceXExtremes = calculateInitialXExtremes(min, max,
                            dataManager.getOriginalTraceData(i),
                            traces.get(i).getMarkSize(),
                            isTracesNaturalDrawingEnabled);
                    if(isTracesNaturalDrawingEnabled) {
                        xExtremes = Range.min(xExtremes, traceXExtremes);
                    } else {
                        xExtremes = Range.join(xExtremes, traceXExtremes);
                    }
                }
            }

            if(xExtremes != null) {
                xAxisList.get(xIndex).setMinMax(xExtremes.getMin(), xExtremes.getMax());
            }
        }

        // set traces data
        for (int i = 0; i < traces.size(); i++) {
            Axis traceXAxis = xAxisList.get(traces.get(i).getXAxisIndex());
            traces.get(i).setData( dataManager.getProcessedTraceData(i, traceXAxis.getMin(), traceXAxis.getMax()));
        }

        // calculate and set min and max for every yAxis
        for (int yIndex = 0; yIndex < yAxisList.size(); yIndex++) {
            Range yExtremes = null;
            for (int i = 0; i < traces.size(); i++) {
                if(traces.get(i).getYAxisIndex() == yIndex) {
                    Range traceYExtremes = calculateInitialYExtremes(yAxisMins.get(yIndex),
                            yAxisMaxs.get(yIndex),
                            traces.get(i));
                    yExtremes = Range.join(yExtremes, traceYExtremes);
                }
            }

            if(yExtremes != null) {
                yAxisList.get(yIndex).setMinMax(yExtremes.getMin(), yExtremes.getMax());
            }
        }

    }

    private Range calculateInitialXExtremes(Double configXMin, Double configXMax, DataSeries traceOriginalData, int pixelsInTraceDataPoint, boolean isTracesNaturalDrawingEnabled) {
        int pixelsInDataPoint = pixelsInTraceDataPoint;
        if(pixelsInDataPoint == 0) {
            pixelsInDataPoint = 1;
        }
        Double min = configXMin;
        Double max = configXMax;

        if (min != null && max != null) {
            return new Range(min, max);
        }

        if (traceOriginalData.size() == 0) {
            if(min != null) {
                return new Range(min, min);
            }
            if(max != null) {
                return new Range(max, max);
            }
            if(min == null && max == null) {
                return null;
            }
        }

        Range traceMinMax = traceOriginalData.getXExtremes();

        if (isTracesNaturalDrawingEnabled && traceOriginalData.size() > 1) {
            double screenLength = traceOriginalData.getDataInterval() * fullArea.width; // / pixelsInDataPoint;
            if (min != null) {
                max = min + screenLength;
                return new Range(min, max);
            }
            if (max != null) {
                min = max - screenLength;
                return new Range(min, max);
            }

            if (min == null && max == null) {
                min = traceMinMax.getMin();
                max = min + screenLength;
                return new Range(min, max);
            }
        }

        // usual scaling
        if (min != null) {
            max = traceMinMax.getMax();
            if (min < max) {
                new Range(min, max);
            }
            return new Range(min, min);
        }
        if (max != null) {
            min = traceMinMax.getMin();
            if (min < max) {
                new Range(min, max);
            }
            return new Range(max, max);
        }
        // if min == null && max == null
        return traceMinMax;
    }


    private Range calculateInitialYExtremes(Double configYMin, Double configYMax, Trace trace) {
        Double min = configYMin;
        Double max = configYMax;

        if(min != null && max != null) {
            return new Range(min, max);
        }

        Range traceMinMax = trace.getYExtremes();

        if(min == null && max == null) {
            return traceMinMax;
        }

        if(traceMinMax == null) {
            if(min != null) {
                return new Range(min, min);
            }
            if(max != null) {
                return new Range(max, max);
            }
            if(min == null && max == null) {
                return null;
            }
        }

        // traceMinMax != null
        if(max != null) {
            min = traceMinMax.getMin();
            if (min < max) {
                return new Range(min, max);
            }
            return new Range(max, max);
        }
        if(min != null) {
            max = traceMinMax.getMax();
            if(min < max) {
                return new Range(min, max);
            }
            return new Range(max, max);
        }
        // if min == null && max == null
        return traceMinMax;
    }

    /**
     * 2 Y-axis for every section(stack): even - LEFT and odd - RIGHT;
     */
    private boolean isYAxisLeft(int axisIndex) {
        if ( (axisIndex & 1) == 0 ) { // even (left)
            return true;
        }
        //odd (right)
        return false;
    }

    /**
     * X-axis: 0(even) - BOTTOM and 1(odd) - TOP
     */
    private boolean isXAxisBottom(int axisIndex) {
        if ( (axisIndex & 1) == 0 ) { // even (bottom)
            return true;
        }
        //odd (top)
        return false;
    }


    Margin getMargin(BCanvas canvas) {
        if (margin == null) {
            System.out.println("margins get");
            calculateMarginsAndAreas(canvas, chartConfig.getMargin());
        }
        return margin;
    }

    BRectangle getGraphArea(BCanvas canvas) {
        if (graphArea == null) {
            System.out.println("margins get graph area");
            calculateMarginsAndAreas(canvas, chartConfig.getMargin());
        }
        return graphArea;
    }

    void setMargin(BCanvas canvas, Margin margin) {
        System.out.println("margins set");
        calculateMarginsAndAreas(canvas, margin);
    }

    Scale getXAxisScale(int xAxisIndex) {
        return xAxisList.get(xAxisIndex).getScale();
    }


    void calculateMarginsAndAreas(BCanvas canvas, Margin margin) {
        if(dataDirty) {
            initiateAxisAndTraces();
            dataDirty = false;
        }

        int titleHeight = titleText.getTitleHeight(canvas, fullArea.width);
        titleArea = new BRectangle(fullArea.x, fullArea.y, fullArea.width, titleHeight);
        chartArea = new BRectangle(fullArea.x, fullArea.y + titleHeight, fullArea.width, fullArea.height - titleHeight);
        int left = -1;
        int right = -1;
        int bottom = -1;
        int top = -1;
        if (margin != null) {
            top = margin.top();
            bottom = margin.bottom();
            left = margin.left();
            right = margin.right();
        }

        // set XAxis ranges
        int xStart = fullArea.x;
        int xEnd = fullArea.x + fullArea.width;
        xAxisList.get(0).setStartEnd(xStart, xEnd);
        xAxisList.get(1).setStartEnd(xStart, xEnd);
        if (top < 0) {
            top = titleHeight + xAxisList.get(1).getWidth(canvas);

        }
        if (bottom < 0) {
            bottom = xAxisList.get(0).getWidth(canvas);
        }

        // set YAxis ranges
        BRectangle paintingArea = new BRectangle(fullArea.x, fullArea.y + top, fullArea.width, fullArea.height - top - bottom);
        for (int i = 0; i < yAxisList.size(); i++) {
            RangeInt yRange = getYStartEnd(i, paintingArea);
            yAxisList.get(i).setStartEnd(yRange.getEnd(), yRange.getStart());
        }
        if (left < 0) {
            for (int i = 0; i < yAxisList.size() / 2; i++) {
                left = Math.max(left, yAxisList.get(i * 2).getWidth(canvas));
            }
        }
        if (right < 0) {
            for (int i = 0; i < yAxisList.size() / 2; i++) {
                right = Math.max(right, yAxisList.get(i * 2 + 1).getWidth(canvas));
            }
        }

        this.margin = new Margin(top, right, bottom, left);
        graphArea = new BRectangle(fullArea.x + left, fullArea.y + top,
                fullArea.width - left - right, fullArea.height - top - bottom);

        for (int stackIndex = 0; stackIndex < legends.size(); stackIndex++) {
            int legendAreaYStart = yAxisList.get(2 * stackIndex).getEnd();
            int legendAreaYEnd = yAxisList.get(2 * stackIndex).getStart();
            BRectangle legendArea = new BRectangle(graphArea.x + 1, legendAreaYStart + 1, graphArea.width, legendAreaYEnd - legendAreaYStart);
            legends.get(stackIndex).setArea(legendArea);
        }

        // adjust XAxis ranges
        xStart = graphArea.x;
        xEnd = graphArea.x + graphArea.width;
        xAxisList.get(0).setStartEnd(xStart, xEnd);
        xAxisList.get(1).setStartEnd(xStart, xEnd);

        for (Axis axis : xAxisList) {
            if(axis.isMinMaxRoundingEnabled() && axis.isVisible()) {
                axis.roundMinMax(canvas);
            }
        }
        for (Axis axis : yAxisList) {
            if(axis.isMinMaxRoundingEnabled() && axis.isVisible()) {
                axis.roundMinMax(canvas);
            }
        }
        areasDirty = false;
    }

    public int getStacksSumWeight() {
        int weightSum = 0;
        for (Integer weight : stackWeights) {
            weightSum += weight;
        }
        return weightSum;
    }

    private RangeInt getYStartEnd(int yAxisIndex, BRectangle area) {
        int weightSum = getStacksSumWeight();

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


    private void updateTraceData(int xAxisIndex) {
        Axis xAxis = xAxisList.get(xAxisIndex);
        for (int i = 0; i < traces.size(); i++) {
            if(traces.get(i).getXAxisIndex() == xAxisIndex) {
                traces.get(i).setData(dataManager.getProcessedTraceData(i, xAxis.getMin(), xAxis.getMax()));
            }
        }
    }

    Range getOriginalDataMinMax() {
        Range minMax = null;
        for (int i = 0; i < traces.size(); i++) {
            Range traceMinMax = dataManager.getOriginalTraceData(i).getXExtremes();
            minMax = Range.join(minMax, traceMinMax);
        }
        return minMax;
    }

    boolean isXAxisUsed(int xAxisIndex) {
        for (Trace trace : traces) {
           if(trace.getXAxisIndex() == xAxisIndex) {
               return true;
           }
        }
        return false;
    }


    public void draw(BCanvas canvas) {
        if (areasDirty) {
            System.out.println("calculate areas");
            calculateMarginsAndAreas(canvas, chartConfig.getMargin());
        }

        canvas.setColor(chartConfig.getMarginColor());
        canvas.fillRect(fullArea.x, fullArea.y, fullArea.width, fullArea.height);

        canvas.setColor(chartConfig.getBackgroundColor());
        canvas.fillRect(graphArea.x, graphArea.y, graphArea.width, graphArea.height);

        canvas.enableAntiAliasAndHinting();

        int topPosition = graphArea.y;
        int bottomPosition = graphArea.y + graphArea.height;
        int leftPosition = graphArea.x;
        int rightPosition = graphArea.x + graphArea.width;

        for (int i = 0; i < xAxisList.size() / 2; i++) {
            xAxisList.get(i * 2).drawGrid(canvas, bottomPosition, graphArea.height);
            xAxisList.get(i * 2 + 1).drawGrid(canvas, topPosition, graphArea.height);
        }

        for (int i = 0; i < yAxisList.size() / 2; i++) {
            yAxisList.get(i * 2).drawGrid(canvas, leftPosition, graphArea.width);
            yAxisList.get(i * 2 + 1).drawGrid(canvas, rightPosition, graphArea.width);
        }

        for (int i = 0; i < xAxisList.size() / 2; i++) {
            xAxisList.get(i * 2).drawAxis(canvas, bottomPosition);
            xAxisList.get(i * 2 + 1).drawAxis(canvas, topPosition);
        }

        for (int i = 0; i < yAxisList.size() / 2; i++) {
            yAxisList.get(i * 2).drawAxis(canvas, leftPosition);
            yAxisList.get(i * 2 + 1).drawAxis(canvas, rightPosition);
        }

        canvas.save();
        canvas.setClip(graphArea.x, graphArea.y, graphArea.width, graphArea.height);

        for (Trace trace : traces) {
            trace.draw(canvas, xAxisList.get(trace.getXAxisIndex()).getScale(), yAxisList.get(trace.getYAxisIndex()).getScale());
        }
        canvas.restore();

        titleText.draw(canvas, titleArea);
        if(isLegendVisible) {
            for (Legend legend : legends) {
                legend.draw(canvas);
            }
        }

        if (hoverTraceIndex >= 0 && hoverPointIndex >= 0) {
            crosshair.draw(canvas, graphArea);
            tooltip.draw(canvas, fullArea);
        }
    }


    /**
     * =======================Base methods to interact==========================
     **/


    public ChartConfig getConfig() {
        return chartConfig;
    }


    public void setConfig(ChartConfig chartConfig1) {
        this.chartConfig = new ChartConfig(chartConfig1);
        for (int i = 0; i < xAxisList.size(); i++) {
            if(isXAxisBottom(i)) {
                xAxisList.get(i).setConfig(chartConfig.getBottomAxisConfig());
            } else {
                xAxisList.get(i).setConfig(chartConfig.getTopAxisConfig());
            }
        }

        for (int i = 0; i < yAxisList.size(); i++) {
           if(isYAxisLeft(i)) {
               yAxisList.get(i).setConfig(chartConfig.getLeftAxisConfig());
           } else {
               yAxisList.get(i).setConfig(chartConfig.getRightAxisConfig());
           }
        }
        // tooltips
        tooltip = new Tooltip(chartConfig.getTooltipConfig());
        crosshair = new Crosshair(chartConfig.getCrossHairConfig());

        //legends
        for (Legend legend : legends) {
            legend.setConfig(chartConfig.getLegendConfig());
        }

        int colorsCount = chartConfig.getTraceColors().length;
        for (int i = 0; i < traces.size(); i++) {
            BColor traceColor = chartConfig.getTraceColors()[i % colorsCount];
            traces.get(i).setMainColor(traceColor);
            int stackNumber = traces.get(i).getYAxisIndex() / 2;
            legends.get(stackNumber).getButton(i).setColor(traceColor);
        }

        areasDirty = true;
    }

    public void setTracesNaturalDrawingEnabled(boolean tracesNaturalDrawingEnabled) {
        isTracesNaturalDrawingEnabled = tracesNaturalDrawingEnabled;
    }

    public void addStack(int weight) {
        Axis leftAxis = new AxisLeft(new LinearScale(), chartConfig.getLeftAxisConfig());
        Axis rightAxis = new AxisRight(new LinearScale(), chartConfig.getRightAxisConfig());
        leftAxis.setConfig(chartConfig.getLeftAxisConfig());
        rightAxis.setConfig(chartConfig.getRightAxisConfig());
        leftAxis.setMinMaxRoundingEnabled(isYAxisMinMaxRoundingEnabled);
        rightAxis.setMinMaxRoundingEnabled(isYAxisMinMaxRoundingEnabled);
        if(isLeftAxisPrimary) {
            leftAxis.setGridVisible(true);
        } else {
            rightAxis.setGridVisible(true);
        }
        yAxisList.add(leftAxis);
        yAxisList.add(rightAxis);
        stackWeights.add(weight);

        legends.add(new Legend(chartConfig.getLegendConfig(), legendHAlign, legendVAlign, buttonGroup));
        areasDirty = true;
    }

    public void addStack() {
        addStack(DEFAULT_WEIGHT);
    }


    /**
     * add trace to the stack with the given number
     * @param stackNumber
     * @param trace
     * @param traceData
     * @param isXAxisOpposite
     * @param isYAxisOpposite
     */
    public void addTrace(int stackNumber, Trace trace, DataSeries traceData,  boolean isXAxisOpposite, boolean isYAxisOpposite) {
        dataManager.addTrace(traceData.copy(), trace.getMarkSize());

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
        int yAxisIndex = isLeftYAxis ? stackNumber * 2 : stackNumber * 2 + 1;
        trace.setXAxisIndex(xAxisIndex);
        trace.setYAxisIndex(yAxisIndex);
        yAxisList.get(yAxisIndex).setVisible(true);
        xAxisList.get(xAxisIndex).setVisible(true);

        if(trace.getName() == null) {
            trace.setName("Trace " + traces.size());
        }
        if(trace.getMainColor() == null) {
            BColor[] traceColors = chartConfig.getTraceColors();
            trace.setMainColor(traceColors[traces.size() % traceColors.length]);
        }

        traces.add(trace);

        double xMin = xAxisList.get(xAxisIndex).getMin();
        double xMax = xAxisList.get(xAxisIndex).getMax();
        if(!dataDirty) {
            trace.setData(dataManager.getProcessedTraceData(traces.size() - 1, xMin, xMax));
        }

        // add trace legend button
        SwitchButton legendButton = new SwitchButton(trace.getMainColor(), trace.getName());
        final int traceIndex = traces.size() - 1;
        legendButton.addListener(new StateListener() {
            @Override
            public void stateChanged(boolean isSelected) {
                if(isSelected) {
                    selectedTraceIndex = traceIndex;
                }
                if(!isSelected && selectedTraceIndex == traceIndex) {
                    selectedTraceIndex = -1;
                }
            }
        });
        legends.get(stackNumber).add(traceIndex, legendButton);
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

    public void setArea(BRectangle area) {
        fullArea = area;
        margin = null;
        areasDirty = true;
    }

    public int traceCount() {
        return traces.size();
    }

    public boolean selectTrace(int x, int y) {
        for (Legend legend : legends) {
            if(legend.selectItem(x, y)) {
                return true;
            }
        }
        return false;
    }

    public boolean selectTrace(int traceIndex) {
        if(selectedTraceIndex != traceIndex) {
            selectedTraceIndex = traceIndex;
            return true;
        }
        return false;
    }

    public int getSelectedTraceIndex() {
        return selectedTraceIndex;
    }


    public int xAxisCount() {
        return xAxisList.size();
    }

    public int yAxisCount() {
        return yAxisList.size();
    }


    public void setXMinMax(int xAxisIndex, double min, double max) {
        xAxisList.get(xAxisIndex).setMinMax(min, max);
        updateTraceData(xAxisIndex);
        areasDirty = true;
    }

    public Range getXMinMax(int xAxisIndex) {
        return new Range(xAxisList.get(xAxisIndex).getMin(), xAxisList.get(xAxisIndex).getMax());
    }

    public Range getYMinMax(int yAxisIndex) {
        return new Range(yAxisList.get(yAxisIndex).getMin(), yAxisList.get(yAxisIndex).getMax());
    }

    public void setYMinMax(int yAxisIndex, double min, double max) {
        yAxisList.get(yAxisIndex).setMinMax(min, max);
        areasDirty = true;
    }


    public RangeInt getYStartEnd(int yAxisIndex) {
        return new RangeInt(yAxisList.get(yAxisIndex).getEnd(), yAxisList.get(yAxisIndex).getStart());
    }

    public int getXIndex(int x, int y) {
        if (fullArea.y + fullArea.height / 2 <= y) {
            for (int i = 0; i < traces.size(); i++) {
                if (getTraceXIndex(i) == 0) {
                    return 0; // bottom Axis
                }
            }
            return 1; // top Axis
        }
        if (fullArea.y <= y && y <= fullArea.y + fullArea.height / 2) {
            for (int i = 0; i < traces.size(); i++) {
                if (getTraceXIndex(i) == 1) {
                    return 1; // top Axis
                }
            }
            return 0; // bottom Axis
        }
        return -1;
    }

    /**
     * Find and return Y axis used by the traces belonging to the stack containing point (x, y)
     */
    public int getYIndex(int x, int y) {
        for (int stackIndex = 0; stackIndex < yAxisList.size() / 2; stackIndex++) {
            if (yAxisList.get(2 * stackIndex).getEnd() <= y && yAxisList.get(2 * stackIndex).getStart() >= y) {
                int stackLeftYAxisIndex = 2 * stackIndex;
                int stackRightYAxisIndex = 2 * stackIndex + 1;
                if (fullArea.x <= x && x <= fullArea.x + fullArea.width / 2) { // left half
                    for (int i = 0; i < traces.size(); i++) {
                        if (getTraceYIndex(i) == stackLeftYAxisIndex) { // if leftAxis is used by some trace
                            return stackLeftYAxisIndex;
                        }
                    }
                    return stackRightYAxisIndex;
                }
                if (fullArea.x + fullArea.width / 2 <= x && x <= fullArea.x + fullArea.width) { // right half
                    for (int i = 0; i < traces.size(); i++) {
                        if (getTraceYIndex(i) == stackRightYAxisIndex) { // if rightAxis is used by some trace
                            return stackRightYAxisIndex;
                        }
                    }
                    return stackLeftYAxisIndex;
                }
            }
        }
        return -1;
    }


    public void zoomY(int yAxisIndex, double zoomFactor) {
        Scale zoomedScale = yAxisList.get(yAxisIndex).zoom(zoomFactor);
        double zoomedMin = zoomedScale.getDomain()[0];
        double zoomedMax = zoomedScale.getDomain()[zoomedScale.getDomain().length - 1];
        setYMinMax(yAxisIndex, zoomedMin, zoomedMax);
    }

    public void zoomX(int xAxisIndex, double zoomFactor) {
        Scale zoomedScale =  xAxisList.get(xAxisIndex).zoom(zoomFactor);
        double zoomedMin = zoomedScale.getDomain()[0];
        double zoomedMax = zoomedScale.getDomain()[zoomedScale.getDomain().length - 1];
        setXMinMax(xAxisIndex, zoomedMin, zoomedMax);
    }

    public void translateY(int yAxisIndex, int translation) {
        Scale translatedScale =  yAxisList.get(yAxisIndex).translate(translation);
        double translatedMin = translatedScale.getDomain()[0];
        double translatedMax = translatedScale.getDomain()[translatedScale.getDomain().length - 1];
        setYMinMax(yAxisIndex, translatedMin, translatedMax);
    }

    public void translateX(int xAxisIndex, int translation) {
        Scale translatedScale = xAxisList.get(xAxisIndex).translate(translation);
        double translatedMin = translatedScale.getDomain()[0];
        double translatedMax = translatedScale.getDomain()[translatedScale.getDomain().length - 1];
        setXMinMax(xAxisIndex, translatedMin, translatedMax);
    }

    public void autoScaleX(int xAxisIndex) {
        Range tracesMinMax = null;
        for (int i = 0; i < traces.size(); i++) {
            if (getTraceXIndex(i) == xAxisIndex) {
                tracesMinMax = Range.join(tracesMinMax, traces.get(i).getXExtremes());
            }
        }
        if(tracesMinMax != null) {
            setXMinMax(xAxisIndex, tracesMinMax.getMin(), tracesMinMax.getMax());
        }

    }

    public void autoScaleY(int yAxisIndex) {
        Range tracesMinMax = null;
        for (int i = 0; i < traces.size(); i++) {
            if (getTraceYIndex(i) == yAxisIndex) {
                tracesMinMax = Range.join(tracesMinMax, traces.get(i).getYExtremes());
            }
        }
        if(tracesMinMax != null) {
            setYMinMax(yAxisIndex, tracesMinMax.getMin(), tracesMinMax.getMax());
        }
    }

    public int getTraceYIndex(int traceIndex) {
        return traces.get(traceIndex).getYAxisIndex();
    }

    public int getTraceXIndex(int traceIndex) {
        return traces.get(traceIndex).getXAxisIndex();
    }


    public boolean hoverOff() {
        if (hoverPointIndex >= 0) {
            hoverPointIndex = -1;
            return true;
        }
        return false;
    }

    public boolean hoverOn(int x, int y, int traceIndex) {
        if(!graphArea.contains(x, y)) {
            return false;
        }
        if(traceIndex >= 0) {
            hoverTraceIndex = traceIndex;
        } else {
            for (int stackIndex = 0; stackIndex < yAxisList.size() / 2; stackIndex++) {
                if (yAxisList.get(2 * stackIndex).getEnd() <= y && yAxisList.get(2 * stackIndex).getStart() >= y) {
                    for (int i = 0; i < traces.size(); i++) {
                        if (getTraceYIndex(i) == 2 * stackIndex || getTraceYIndex(i) == 2 * stackIndex + 1) {
                            hoverTraceIndex = i;
                            break;
                        }
                    }
                }
            }
        }

        if (hoverTraceIndex >= 0) {
            Trace trace = traces.get(hoverTraceIndex);
            Scale xScale = xAxisList.get(trace.getXAxisIndex()).getScale();
            Scale yScale = yAxisList.get(trace.getYAxisIndex()).getScale();

            int nearestIndex = (int)trace.findNearestData(x, y, xScale, yScale);
            if (hoverPointIndex != nearestIndex) {
                hoverPointIndex = nearestIndex;
                if (hoverPointIndex >= 0) {
                    TooltipInfo tooltipInfo = new TooltipInfo();
                    tooltipInfo.addItems(traces.get(hoverTraceIndex).getInfo(hoverPointIndex, xScale, yScale));
                    BPoint dataPosition = traces.get(hoverTraceIndex).getDataPosition(hoverPointIndex, xScale, yScale);
                    tooltip.setTooltipInfo(tooltipInfo);
                    tooltip.setXY(dataPosition.getX(), yAxisList.get(getTraceYIndex(hoverTraceIndex)).getEnd());
                    crosshair.setXY(dataPosition.getX(), dataPosition.getY());
                }
                return true;
            }
        }
        return false;
    }
}
