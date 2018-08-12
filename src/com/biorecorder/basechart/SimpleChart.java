package com.biorecorder.basechart;

import com.biorecorder.basechart.button.StateListener;
import com.biorecorder.basechart.button.ToggleBtn;
import com.biorecorder.basechart.button.BtnGroup;
import com.biorecorder.basechart.config.SimpleChartConfig;
import com.biorecorder.basechart.config.traces.TraceConfig;
import com.biorecorder.basechart.scales.Scale;
import com.biorecorder.basechart.traces.Trace;
import com.biorecorder.basechart.data.Data;
import com.biorecorder.basechart.data.DataSeries;

import java.util.*;
import java.util.List;

/**
 * Created by hdablin on 24.03.17.
 */
public class SimpleChart {
    private List<Axis> xAxisList = new ArrayList<Axis>(2);
    private List<Axis> yAxisList = new ArrayList<Axis>();
    private List<Trace> traces = new ArrayList<Trace>();

    private List<Legend> legends = new ArrayList<Legend>();
    private Title title;
    private BRectangle fullArea;
    private BRectangle titleArea;
    private BRectangle chartArea;
    private BRectangle graphArea;
    private SimpleChartConfig chartConfig;
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

    public SimpleChart(SimpleChartConfig chartConfig, Data data, BRectangle area) {
        this(chartConfig, data, area, new DefaultTraceFactory());
    }


    public SimpleChart(SimpleChartConfig chartConfig, Data data, BRectangle area, TraceFactory traceFactory) {
        this.chartConfig = chartConfig;
        this.fullArea = area;

        dataManager = new DataManager(data, chartConfig.getDataProcessingConfig());
        for (int i = 0; i < chartConfig.traceCount(); i++) {
            dataManager.addTrace(chartConfig.getTraceDataConfig(i), chartConfig.getTraceConfig(i).getMarkSize());
        }

        // create x axis
        for (int xIndex = 0; xIndex < chartConfig.xAxisCount(); xIndex++) {
            Range xExtremes = null;
            Double min = chartConfig.getXMin(xIndex);
            Double max = chartConfig.getXMax(xIndex);
            if (min != null && max != null) {
                xExtremes = new Range(min, max);
            } else if (min != null && max == null) {
                xExtremes = new Range(min, min);
            } else if (min == null && max != null) {
                xExtremes = new Range(max, max);
            }

            for (int i = 0; i < chartConfig.traceCount(); i++) {
                if(chartConfig.getTraceXIndex(i) == xIndex) {
                    Range traceXExtremes = calculateInitialXExtremes(min, max,
                            dataManager.getOriginalTraceData(i),
                            chartConfig.getTraceConfig(i).getMarkSize(),
                            chartConfig.isTracesNaturalDrawingEnabled());
                    if(chartConfig.isTracesNaturalDrawingEnabled()) {
                        xExtremes = Range.min(xExtremes, traceXExtremes);
                    } else {
                        xExtremes = Range.join(xExtremes, traceXExtremes);
                    }
                }
            }

            Axis xAxis = new Axis(chartConfig.getXConfig(xIndex));
            if(xExtremes != null) {
                xAxis.setMinMax(xExtremes.getMin(), xExtremes.getMax());
            }
            xAxisList.add(xAxis);
        }

        // create traces
        for (int i = 0; i < chartConfig.traceCount(); i++) {
            TraceConfig traceConfig = chartConfig.getTraceConfig(i);
            Trace trace = traceFactory.getTrace(traceConfig);
            Axis traceXAxis = xAxisList.get(chartConfig.getTraceXIndex(i));
            trace.setData( dataManager.getProcessedTraceData(i, traceXAxis.getMin(), traceXAxis.getMax()));
            trace.setName(chartConfig.getTraceName(i));
            traces.add(trace);
        }

        // create y axis
        for (int yIndex = 0; yIndex < chartConfig.getYAxisCount(); yIndex++) {
            Range yExtremes = null;
            for (int i = 0; i < chartConfig.traceCount(); i++) {
                if(chartConfig.getTraceYIndex(i) == yIndex) {
                    Range traceYExtremes = calculateInitialYExtremes(chartConfig.getYMin(yIndex),
                            chartConfig.getYMax(yIndex),
                            traces.get(i));
                    yExtremes = Range.join(yExtremes, traceYExtremes);
                }
            }
            Axis yAxis = new Axis(chartConfig.getYConfig(yIndex));
            if(yExtremes != null) {
                yAxis.setMinMax(yExtremes.getMin(), yExtremes.getMax());
            }
            yAxisList.add(yAxis);
        }

        // set traces x and y axes
        for (int i = 0; i < traces.size(); i++) {
            traces.get(i).setXAxis(xAxisList.get(chartConfig.getTraceXIndex(i)));
            traces.get(i).setYAxis(yAxisList.get(chartConfig.getTraceYIndex(i)));
        }

        // title
        title = new Title(chartConfig.getTitle(), chartConfig.getTitleTextStyle(), chartConfig.getTitleColor());

        // legend buttons for every panel (y stack)
        BtnGroup buttonGroup = new BtnGroup();
        for (int i = 0; i < yAxisList.size() / 2; i++) {
            legends.add(new Legend(chartConfig.getLegendConfig(), buttonGroup));
        }

        for (int i = 0; i < traces.size(); i++) {
            int stackIndex = getTraceYIndex(i) / 2;
            ToggleBtn legendButton = new ToggleBtn(traces.get(i).getColor(), traces.get(i).getName());
            final int traceIndex = i;
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
            legends.get(stackIndex).add(legendButton);
        }
        // tooltips
        tooltip = new Tooltip(chartConfig.getTooltipConfig());
        crosshair = new Crosshair(chartConfig.getCrosshairConfig());

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

    Margin getMargin(BCanvas canvas) {
        if (margin == null) {
            calculateMarginsAndAreas(canvas, chartConfig.getMargin());
        }
        return margin;
    }

    BRectangle getGraphArea(BCanvas canvas) {
        if (graphArea == null) {
            calculateMarginsAndAreas(canvas, chartConfig.getMargin());
        }
        return graphArea;
    }

    void setMargin(BCanvas canvas, Margin margin) {
        calculateMarginsAndAreas(canvas, margin);
    }

    Scale getXAxisScale(int xAxisIndex) {
        return xAxisList.get(xAxisIndex).getScale();
    }


    void calculateMarginsAndAreas(BCanvas canvas, Margin margin) {
        int titleHeight = title.getTitleHeight(canvas, fullArea.width);
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
            top = titleHeight + xAxisList.get(1).getThickness(canvas);

        }
        if (bottom < 0) {
            bottom = xAxisList.get(0).getThickness(canvas);
        }

        // set YAxis ranges
        BRectangle paintingArea = new BRectangle(fullArea.x, fullArea.y + top, fullArea.width, fullArea.height - top - bottom);
        for (int i = 0; i < yAxisList.size(); i++) {
            RangeInt yRange = chartConfig.getYStartEnd(i, paintingArea);
            yAxisList.get(i).setStartEnd(yRange.getEnd(), yRange.getStart());
        }
        if (left < 0) {
            for (int i = 0; i < yAxisList.size() / 2; i++) {
                left = Math.max(left, yAxisList.get(i * 2).getThickness(canvas));
            }
        }
        if (right < 0) {
            for (int i = 0; i < yAxisList.size() / 2; i++) {
                right = Math.max(right, yAxisList.get(i * 2 + 1).getThickness(canvas));
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
        areasDirty = false;
    }

    private void updateTraceData(int xAxisIndex) {
        Axis xAxis = xAxisList.get(xAxisIndex);
        for (int i = 0; i < traces.size(); i++) {
            if(traces.get(i).getXAxis() == xAxis) {
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
       Axis xAxis = xAxisList.get(xAxisIndex);
        for (Trace trace : traces) {
           if(trace.getXAxis() == xAxis) {
               return true;
           }
        }
        return false;
    }


    public void draw(BCanvas canvas) {
        if (areasDirty) {
            calculateMarginsAndAreas(canvas, chartConfig.getMargin());
        }

        canvas.setColor(chartConfig.getMarginColor());
        canvas.fillRect(fullArea.x, fullArea.y, fullArea.width, fullArea.height);

        canvas.setColor(chartConfig.getBackground());
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
            xAxisList.get(i * 2).drawAxis(canvas, bottomPosition, margin.bottom());
            xAxisList.get(i * 2 + 1).drawAxis(canvas, topPosition, margin.top() - titleArea.height);
        }

        for (int i = 0; i < yAxisList.size() / 2; i++) {
            yAxisList.get(i * 2).drawAxis(canvas, leftPosition, margin.left());
            yAxisList.get(i * 2 + 1).drawAxis(canvas, rightPosition, margin.right());
        }

        canvas.save();
        canvas.setClip(graphArea.x, graphArea.y, graphArea.width, graphArea.height);

        for (Trace trace : traces) {
            trace.draw(canvas);
        }
        canvas.restore();

        title.draw(canvas, titleArea);
        for (Legend legend : legends) {
            legend.draw(canvas);
        }

        if (hoverTraceIndex >= 0 && hoverPointIndex >= 0) {
            crosshair.draw(canvas, graphArea);
            tooltip.draw(canvas, fullArea);
        }
    }

    /**
     * =======================Base methods to interact==========================
     **/

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
            if(legend.toggle(x, y)) {
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
    }

    public Range getXMinMax(int xAxisIndex) {
        return new Range(xAxisList.get(xAxisIndex).getMin(), xAxisList.get(xAxisIndex).getMax());
    }

    public Range getYMinMax(int yAxisIndex) {
        return new Range(yAxisList.get(yAxisIndex).getMin(), yAxisList.get(yAxisIndex).getMax());
    }

    public void setYMinMax(int yAxisIndex, double min, double max) {
        yAxisList.get(yAxisIndex).setMinMax(min, max);
        margin = null;
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


    public void zoomY(int yAxisIndex, float zoomFactor) {
        Range newAxisRange = yAxisList.get(yAxisIndex).zoom(zoomFactor);
        yAxisList.get(yAxisIndex).setMinMax(newAxisRange.getMin(), newAxisRange.getMax());
    }

    public void zoomX(int xAxisIndex, float zoomFactor) {
        Range newAxisRange = xAxisList.get(xAxisIndex).zoom(zoomFactor);
        xAxisList.get(xAxisIndex).setMinMax(newAxisRange.getMin(), newAxisRange.getMax());
    }

    public void translateY(int yAxisIndex, int translation) {
        Range newAxisRange =  yAxisList.get(yAxisIndex).translate(translation);
        yAxisList.get(yAxisIndex).setMinMax(newAxisRange.getMin(), newAxisRange.getMax());
    }

    public void translateX(int xAxisIndex, int translation) {
        Range newAxisRange = xAxisList.get(xAxisIndex).translate(translation);
        xAxisList.get(xAxisIndex).setMinMax(newAxisRange.getMin(), newAxisRange.getMax());
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
        return chartConfig.getTraceYIndex(traceIndex);
    }

    public int getTraceXIndex(int traceIndex) {
        return chartConfig.getTraceXIndex(traceIndex);
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
            double xValue = traces.get(hoverTraceIndex).getXAxis().invert(x);
            int nearestIndex = (int)traces.get(hoverTraceIndex).getData().findNearestData(xValue);
            if (hoverPointIndex != nearestIndex) {
                hoverPointIndex = nearestIndex;
                if (hoverPointIndex >= 0) {
                    TooltipInfo tooltipInfo = new TooltipInfo();
                    tooltipInfo.addItems(traces.get(hoverTraceIndex).getInfo(hoverPointIndex));
                    BPoint dataPosition = traces.get(hoverTraceIndex).getDataPosition(hoverPointIndex);
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
