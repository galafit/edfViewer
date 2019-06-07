package com.biorecorder.basechart;

import com.biorecorder.basechart.axis.AxisWrapper;
import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.graphics.BRectangle;
import com.biorecorder.basechart.scales.Scale;
import com.biorecorder.data.sequence.StringSequence;
import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.List;


/**
 * TODO implement XY search nearest point (QuadTree)
 * убрать двойку в формированиее тултипов
 *
 * удаление неиспользуемых скролов в чарте с навигацией
 *
 * метод isInverted в Trace
 * задавать где то DataProcessingConfig
 */
class DataPainter {
    private int xIndex;
    private int yStartIndex;
    private GroupApproximation[] defaultApproximations = {GroupApproximation.OPEN, GroupApproximation.AVERAGE};

    private boolean isSplit;
    private DataManager dataManager;
    private Trace tracePainter;
    private NearestSearchType nearestSearchType;
    private int traceCount;
    private String[] traceNames;
    private BColor[] traceColors;
    private boolean[] tracesVisibleMask;
    private int hiddenTraceCount = 0;

    DataPainter(ChartData data1, Trace tracePainter, boolean isSplit, DataProcessingConfig dataProcessingConfig1, int xIndex, int yStartIndex) {
        ChartData data = data1.view(0);
        DataProcessingConfig dataProcessingConfig = new DataProcessingConfig(dataProcessingConfig1);

        if (tracePainter.traceType() == TraceType.LINE) {
            nearestSearchType = NearestSearchType.X;
        } else {
            nearestSearchType = NearestSearchType.XY;
        }
        this.isSplit = isSplit;
        this.xIndex = xIndex;
        this.yStartIndex = yStartIndex;
        traceCount = tracePainter.traceCount(data);
        traceNames = new String[traceCount];
        traceColors = new BColor[traceCount];
        tracesVisibleMask = new boolean[traceCount];
        for (int trace = 0; trace < traceCount; trace++) {
            traceNames[trace] = tracePainter.traceName(data, trace);
            tracesVisibleMask[trace] = true;
        }

        // set approximations
        GroupApproximation[] approximations = tracePainter.groupApproximations();
        if (approximations == null || approximations.length > 0) {
            approximations = defaultApproximations;
        }
        if (approximations.length < data.columnCount()) {
            // we need it in the case of RANGE and OHLC approximations
            // if data is already grouped and so is multi-dimensional
            List<GroupApproximation> deployedApproximations = new ArrayList<>();
            for (int i = 0; i < approximations.length; i++) {
                GroupApproximation[] deployedApprox_i = approximations[i].getAsArray();
                for (GroupApproximation approx : deployedApprox_i) {
                    deployedApproximations.add(approx);
                }
            }
        }
        for (int column = 0; column < data.columnCount(); column++) {
            if (data.getColumnGroupApproximation(column) == null) {
                data.setColumnGroupApproximation(column, approximations[Math.min(column, approximations.length - 1)]);
            }
        }
        this.tracePainter = tracePainter;
        dataManager = new DataManager(data, dataProcessingConfig);
    }

    void hideTrace(int trace) {
        tracesVisibleMask[trace] = false;
        hiddenTraceCount++;
    }

    boolean isSplit() {
        return isSplit;
    }

    int getYStartIndex() {
        return yStartIndex;
    }

    void setYStartIndex(int yScaleStartIndex) {
        this.yStartIndex = yScaleStartIndex;
    }

    int getXIndex() {
        return xIndex;
    }

    private ChartData getData(Scale xScale) {
        if(tracePainter.traceType() == TraceType.DIMENSIONAL2) {
            dataManager.getData();
        }
        return dataManager.getProcessedData(xScale, tracePainter.markWidth());
    }

    private void checkTraceNumber(int traceNumber) {
        if (traceNumber >= traceCount()) {
            String errMsg = "Trace = " + traceNumber + " Number of traces: " + traceCount();
            throw new IllegalArgumentException(errMsg);
        }
    }

    void appendData() {
        dataManager.appendData();
    }

    StringSequence getLabelsIfXColumnIsString() {
        return dataManager.getLabelsIfXColumnIsString();
    }

    Range getFullXMinMax() {
        return tracePainter.argumentMinMax(dataManager.getData());
    }

    double getBestExtent(int drawingAreaWidth) {
        return dataManager.getBestExtent(drawingAreaWidth, tracePainter.markWidth(), tracePainter.traceType());
    }

    NamedValue[] traceValues(int dataIndex, int trace, Scale xScale, Scale yScale) {
        checkTraceNumber(trace);
        return tracePainter.tracePointValues(getData(xScale), dataIndex, trace, xScale, yScale);
    }

    BRectangle tracePointHoverArea(int dataIndex, int trace, Scale xScale, Scale yScale) {
        checkTraceNumber(trace);
        return tracePainter.tracePointHoverArea(getData(xScale), dataIndex, trace, xScale, yScale);
    }

    Range traceYMinMax(int trace, Scale xScale, Scale yScale) {
        checkTraceNumber(trace);
        return tracePainter.traceMinMax(getData(xScale), trace);
    }

    Range xMinMax(Scale xScale) {
        return tracePainter.argumentMinMax(getData(xScale));
    }

    int distanceSqw(int pointIndex, int trace, int x, int y, Scale xScale, Scale yScale) {
        checkTraceNumber(trace);
        BRectangle hoverRect = tracePainter.tracePointHoverArea(getData(xScale), pointIndex, trace, xScale, yScale);
        if (hoverRect.width > 0 && hoverRect.height > 0) {
            if (hoverRect.contains(x, y)) {
                return 0;
            } else {
                return -1;
            }
        } else if (hoverRect.width > 0) {
            if (hoverRect.containsX(x)) {
                return 0;
            } else {
                return -1;
            }
        } else if (hoverRect.height > 0) {
            if (hoverRect.containsY(y)) {
                return 0;
            } else {
                return -1;
            }
        }

        int dy = hoverRect.y - y;
        int dx = hoverRect.x - x;
        return dy * dy + dx * dx;
    }

    int traceCount() {
        return traceCount - hiddenTraceCount;
    }


    @Nullable
    NearestTracePoint nearest(int x, int y, int trace, Scale xScale, Scale yScale) {
        double xValue = xScale.invert(x);
        int pointIndex = dataManager.nearest(xValue);
        if (pointIndex < 0) {
            return null;
        }
        int distance = distanceSqw(pointIndex, trace, x, y, xScale, yScale);
        if (distance >= 0) {
            return new NearestTracePoint(new DataPainterTracePoint(this, trace, pointIndex), distance);
        } else {
            return null;
        }
    }


    @Nullable
    NearestTracePoint nearest(int x, int y, Scale xScale, Scale[] yScales) {
        double xValue = xScale.invert(x);
        int pointIndex = dataManager.nearest(xValue);
        if (pointIndex < 0) {
            return null;
        }
        int minDistance = -1;
        int closestTrace = -1;
        for (int trace = 0; trace < traceCount; trace++) {
            if(tracesVisibleMask[trace]) {
                int distance = distanceSqw(pointIndex, trace, x, y, xScale, yScales[trace]);
                if (distance == 0) {
                    return new NearestTracePoint(new DataPainterTracePoint(this, trace, pointIndex), 0);
                } else if (distance > 0) {
                    if (minDistance < 0 || minDistance > distance) {
                        minDistance = distance;
                        closestTrace = trace;
                    }
                }
            }
        }
        if (closestTrace >= 0) {
            return new NearestTracePoint(new DataPainterTracePoint(this, closestTrace, pointIndex), minDistance);
        }
        return null;
    }

    public List<Crosshair> createCrosshairs(int hoverPointIndex, int hoverTrace, boolean isMultiTrace, AxisWrapper xAxis, AxisWrapper[] yAxes) {
        List<Crosshair> crosshairs =  new ArrayList<>();

        int xPosition;
        int traceStart;
        int traceEnd;
        if (isMultiTrace) { // all trace traces
            traceStart = 0;
            traceEnd = traceCount() - 1;
        } else { // only hover trace
            traceStart = hoverTrace;
            traceEnd = hoverTrace;
        }

        BRectangle hoverAreaStart = tracePointHoverArea(hoverPointIndex, traceStart, xAxis.getScale(), yAxes[0].getScale());
        BRectangle hoverAreaEnd = tracePointHoverArea(hoverPointIndex, traceEnd, xAxis.getScale(), yAxes[yAxes.length - 1].getScale());

        xPosition = (hoverAreaEnd.x + hoverAreaEnd.width + hoverAreaStart.x) / 2;
        crosshairs.add(new Crosshair(xAxis, xPosition));

        for (int trace = traceStart; trace <= traceEnd; trace++) {
            AxisWrapper yAxis = yAxes[trace];
            BRectangle traceArea = tracePointHoverArea(hoverPointIndex, trace, xAxis.getScale(), yAxis.getScale());
            crosshairs.add(new Crosshair(yAxis,traceArea.y));
        }
        return crosshairs;
    }

    public Tooltip createTooltip(TooltipConfig tooltipConfig, int hoverPointIndex, int hoverTrace, boolean isMultiTrace, Scale xScale, Scale[] yScales) {
        int tooltipYPosition = 0;
        NamedValue xValue = xValue(hoverPointIndex, xScale);

        int xPosition;
        int traceStart;
        int traceEnd;
        if (isMultiTrace) { // all trace traces
            traceStart = 0;
            traceEnd = traceCount() - 1;
        } else { // only hover trace
            traceStart = hoverTrace;
            traceEnd = hoverTrace;
        }

        BRectangle hoverAreaStart = tracePointHoverArea(hoverPointIndex, traceStart, xScale, yScales[0]);
        BRectangle hoverAreaEnd = tracePointHoverArea(hoverPointIndex, traceEnd, xScale, yScales[yScales.length - 1]);

        xPosition = (hoverAreaEnd.x + hoverAreaEnd.width + hoverAreaStart.x) / 2;
        Tooltip tooltip = new Tooltip(tooltipConfig, xPosition, tooltipYPosition);
        tooltip.setHeader(null, null, xValue.getValue());

        for (int trace = traceStart; trace <= traceEnd; trace++) {
            Scale yScale = yScales[trace];
            NamedValue[] traceValues = traceValues(hoverPointIndex, trace, xScale, yScale);
            if (traceValues.length == 2) {
                tooltip.addLine(getTraceColor(trace), getTraceName(trace), traceValues[1].getValue());
            } else {
                tooltip.addLine(getTraceColor(trace), getTraceName(trace), "");
                for (NamedValue traceValue : traceValues) {
                    tooltip.addLine(null, traceValue.getValueName(), traceValue.getValue());
                }
            }
        }
        return tooltip;
    }

    void setTraceName(int trace, String name) {
        traceNames[trace] = name;
    }

    void setTraceColor(int trace, BColor color) {
        checkTraceNumber(trace);
        traceColors[trace] = color;
    }

    BColor getTraceColor(int trace) {
        checkTraceNumber(trace);
        return traceColors[trace];
    }

    String getTraceName(int trace) {
        checkTraceNumber(trace);
        return traceNames[trace];
    }

    void drawTrace(BCanvas canvas, int trace, Scale xScale, Scale yScale) {
        if(tracesVisibleMask[trace]) {
            tracePainter.drawTrace(canvas, getData(xScale), trace, getTraceColor(trace), traceCount(), isSplit, xScale, yScale);
        }
    }

    NamedValue xValue(int dataIndex, Scale xScale) {
        double xValue = getData(xScale).value(dataIndex, 0);
        return new NamedValue("x: ", xScale.formatDomainValue(xValue));
    }

}
