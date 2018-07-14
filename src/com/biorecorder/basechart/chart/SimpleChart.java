package com.biorecorder.basechart.chart;

import com.biorecorder.basechart.chart.button.ToggleBtn;
import com.biorecorder.basechart.chart.button.BtnGroup;
import com.biorecorder.basechart.chart.button.StateListener;
import com.biorecorder.basechart.chart.config.SimpleChartConfig;
import com.biorecorder.basechart.chart.config.traces.TraceConfig;
import com.biorecorder.basechart.chart.scales.Scale;
import com.biorecorder.basechart.chart.traces.Trace;
import com.biorecorder.basechart.data.DataSeries;
import com.biorecorder.basechart.data.GroupedDataSeries;
import com.biorecorder.basechart.data.grouping.GroupingType;

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
    /** Dirty com.biorecorder.basechart.data in js - the com.biorecorder.basechart.data, that have been changed recently and
     ** DOM haven't been re-rendered according to this changes yet.
     ** So dirty checking is diff between next state and current state.
     **/
    private boolean isDirty = true;


    private Crosshair crosshair;
    private Tooltip tooltip;

    private int selectedTraceIndex = -1;
    private int hoverPointIndex = -1;
    private int hoverTraceIndex = -1;

    private List<DataSeries> data;

    public SimpleChart(SimpleChartConfig chartConfig, List<DataSeries> data, BRectangle area) {
        this(chartConfig, data, area, new DefaultTraceFactory());
    }


    public SimpleChart(SimpleChartConfig chartConfig, List<DataSeries> data, BRectangle area, TraceFactory traceFactory) {
        this.data = data;
        this.chartConfig = chartConfig;
        this.fullArea = area;
        for (int i = 0; i < chartConfig.getXAxisCount(); i++) {
            xAxisList.add(new Axis(chartConfig.getXConfig(i)));
        }
        for (int i = 0; i < chartConfig.getYAxisCount(); i++) {
            yAxisList.add(new Axis(chartConfig.getYConfig(i)));
        }

        // set min and max for x axis
        for (int i = 0; i < xAxisList.size(); i++) {
           // initialScaleX(i);
        }

        for (int i = 0; i < chartConfig.getTraceCount(); i++) {
            TraceConfig traceConfig = chartConfig.getTraceConfig(i);
            Trace trace = traceFactory.getTrace(traceConfig);

            DataSeries traceData = getTraceData(i);
            int traceXIndex = chartConfig.getTraceXIndex(i);
          /*  Range xMinMax = chartConfig.getXMinMax(traceXIndex);
            if(xMinMax != null) {
                traceData = cropTraceData(traceData, xMinMax);
            }*/
            traceData = groupTraceData(traceData, new Range(xAxisList.get(traceXIndex).getMin(), xAxisList.get(traceXIndex).getMax()));
            trace.setData(traceData);
            trace.setXAxis(xAxisList.get(chartConfig.getTraceXIndex(i)));
            trace.setYAxis(yAxisList.get(chartConfig.getTraceYIndex(i)));
            trace.setName(chartConfig.getTraceName(i));
            traces.add(trace);
        }
        tooltip = new Tooltip(chartConfig.getTooltipConfig());
        crosshair = new Crosshair(chartConfig.getCrosshairConfig());
        title = new Title(chartConfig.getTitle(), chartConfig.getTitleTextStyle(), chartConfig.getTitleColor());

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

        // set min and max for y axis
        for (int i = 0; i < yAxisList.size(); i++) {
          //  initialScaleY(i);
        }
    }

    private Range initialXRange(int xAxisIndex) {
        Double min = chartConfig.getXMin(xAxisIndex);
        Double max = chartConfig.getXMax(xAxisIndex);

        if(min != null && max != null) {
            return new Range(min, max);
        }

        Range tracesMinMax = null;
        for (int i = 0; i < chartConfig.getTraceCount(); i++) {
            if (chartConfig.getTraceXIndex(i) == xAxisIndex) {
                DataSeries traceData = getTraceData(i);
                tracesMinMax = Range.max(tracesMinMax, traceData.getXExtremes());
            }
        }

        if(tracesMinMax != null) {
            if(min == null && max != null) {
                min = tracesMinMax.getStart();
                if(min >= max) {
                    min = max - 1;
                }

            }
            if(max == null && min != null) {
                max = tracesMinMax.getEnd();
                if(min >= max) {
                    max = min + 1;
                }
            }
            if(max == null && min == null) {
                min = tracesMinMax.getStart();
                max = tracesMinMax.getEnd();
                if(min == max) {
                    max = min + 1;
                }
            }
            return new Range(min, max);
        } else {
            if(min == null && max != null) {
                return new Range(max - 1, max);
            }
            if(min != null && max == null) {
                return new Range(min, min + 1);
            }
            return new Range(0, 1);
        }
    }

    private Range initialYRange(int yAxisIndex) {
        Double min = chartConfig.getYMin(yAxisIndex);
        Double max = chartConfig.getYMax(yAxisIndex);

        if(min != null && max != null) {
            return new Range(min, max);
        }

        Range tracesMinMax = null;
        for (int i = 0; i < traces.size(); i++) {
            Trace trace = traces.get(i);
            if (trace.getYAxis() == yAxisList.get(yAxisIndex)) {
                tracesMinMax = Range.max(tracesMinMax, trace.getYExtremes());
            }
        }

        if(tracesMinMax != null) {
            if(min == null && max != null) {
                min = tracesMinMax.getStart();
                if(min >= max) {
                    min = max - 1;
                }

            }
            if(max == null && min != null) {
                max = tracesMinMax.getEnd();
                if(min >= max) {
                    max = min + 1;
                }
            }
            if(max == null && min == null) {
                min = tracesMinMax.getStart();
                max = tracesMinMax.getEnd();
                if(min == max) {
                    max = min + 1;
                }
            }
            return new Range(min, max);
        } else {
            if(min == null && max != null) {
                return new Range(max - 1, max);
            }
            if(min != null && max == null) {
                return new Range(min, min + 1);
            }
            return new Range(0, 1);
        }
    }


    private DataSeries getTraceData(int traceNumber) {
        return data.get(chartConfig.getTraceDataIndex(traceNumber));
    }

    private DataSeries cropTraceData(DataSeries traceData, Range xAxisRange) {
        if(chartConfig.isDataCropEnable()) {
           // traceData = traceData.getSubset(xAxisRange.getStart(), xAxisRange.getEnd(), 0);
        }
        return traceData;
    }

    private DataSeries groupTraceData(DataSeries traceData, Range xAxisRange) {
        if(chartConfig.isGroupingEnable()) {
            double groupingInterval = xAxisRange.length() * chartConfig.getMinPixelsPerDataPoint() / fullArea.width;
            int numberOfDataPointsToGroup = (int) (groupingInterval / traceData.getAverageDataInterval());
            traceData = new GroupedDataSeries(traceData, GroupingType.AVG, numberOfDataPointsToGroup);
        }
        return traceData;
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
        isDirty = false;
    }


    public void draw(BCanvas canvas) {
        if (isDirty) {
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
        isDirty = true;
    }

    public int getTraceCounter() {
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


    public int getXAxisCounter() {
        return xAxisList.size();
    }

    public int getYAxisCounter() {
        return yAxisList.size();
    }

    public void setXMinMax(int xAxisIndex, Range minMax) {
        xAxisList.get(xAxisIndex).setMinMax(minMax);
    }

    public Range getXMinMax(int xAxisIndex) {
        return new Range(xAxisList.get(xAxisIndex).getMin(), xAxisList.get(xAxisIndex).getMax());
    }

    public Range getYMinMax(int yAxisIndex) {
        return new Range(yAxisList.get(yAxisIndex).getMin(), yAxisList.get(yAxisIndex).getMax());
    }

    public void setYMinMax(int yAxisIndex, Range minMax) {
        yAxisList.get(yAxisIndex).setMinMax(minMax);
        margin = null;
        isDirty = true;
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
        yAxisList.get(yAxisIndex).zoom(zoomFactor);
        margin = null;
        isDirty = true;
    }

    public void zoomX(int xAxisIndex, float zoomFactor) {
        xAxisList.get(xAxisIndex).zoom(zoomFactor);
    }

    public void translateY(int yAxisIndex, int translation) {
        yAxisList.get(yAxisIndex).translate(translation);
        margin = null;
        isDirty = true;
    }

    public void translateX(int xAxisIndex, int translation) {
        xAxisList.get(xAxisIndex).translate(translation);
    }

    public void autoScaleX(int xAxisIndex) {
        Range tracesMinMax = null;
        for (int i = 0; i < traces.size(); i++) {
            if (getTraceXIndex(i) == xAxisIndex) {
                tracesMinMax = Range.max(tracesMinMax, traces.get(i).getXExtremes());
            }
        }
        xAxisList.get(xAxisIndex).setMinMax(tracesMinMax);
    }

    public void autoScaleY(int yAxisIndex) {
        Range tracesMinMax = null;
        for (int i = 0; i < traces.size(); i++) {
            if (getTraceYIndex(i) == yAxisIndex) {
                tracesMinMax = Range.max(tracesMinMax, traces.get(i).getYExtremes());
            }
        }
        yAxisList.get(yAxisIndex).setMinMax(tracesMinMax);
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
