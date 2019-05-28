package com.biorecorder.basechart;

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
 * колонки сразу с именами в апи
 * обнуление аггрегации
 * перенести формиров тултипов в трейс
 * убрать traces
 * убрать двойку в формированиее тултипов
 * x, y оси переименовать в h, v
 *
 * оси - два признака visible и unused
 * во всех действиях проверять что если ось unused то ничего с ней  не делать
 * а инвизибл должна идти как обычная ось
 *
 * брать в дата менеджере квадрат из экстеншн в случае нон-лайн трес
 * full data min max  делать в трейсе ?
 *
 * переделать групповые баттоны чтобы выбранный трейс определялся автоматом без слушателя
 */
class DataPainter {
    private int xIndex;
    private int yStartIndex;

    private boolean isSplit;
    private DataManager dataManager;
    private Trace tracePainter;
    private NearestSearchType nearestSearchType;
    private int traceCount;
    private String[] traceNames;
    private BColor[] traceColors;
    private boolean[] visibleTraces;
    private int hiddenTraceCount = 0;

    DataPainter(ChartData data1, Trace tracePainter, boolean isSplit, DataProcessingConfig dataProcessingConfig1, int xIndex, int yStartIndex) {
        ChartData data = data1.view(0);
        DataProcessingConfig dataProcessingConfig = new DataProcessingConfig(dataProcessingConfig1);

        if (tracePainter.traceType() == TraceType.LINE) {
            nearestSearchType = NearestSearchType.X;
        } else {
            disableDataProcessing(dataProcessingConfig);
            nearestSearchType = NearestSearchType.XY;
        }
        this.isSplit = isSplit;
        this.xIndex = xIndex;
        this.yStartIndex = yStartIndex;
        traceCount = tracePainter.traceCount(data);
        traceNames = new String[traceCount];
        traceColors = new BColor[traceCount];
        visibleTraces = new boolean[traceCount];
        for (int trace = 0; trace < traceCount; trace++) {
            traceNames[trace] = tracePainter.traceName(data, trace);
            visibleTraces[trace] = true;
        }

        // set approximations
        GroupApproximation[] approximations = tracePainter.groupApproximations();
        if (approximations != null && approximations.length > 0) {
            if (approximations.length < data.columnCount()) {
                // we need it in the case of RANGE and OHLC approximations
                // when data is already grouped and so is multi-dimensional
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
        } else {
            disableDataProcessing(dataProcessingConfig);
        }
        this.tracePainter = tracePainter;
        dataManager = new DataManager(data, dataProcessingConfig);
    }

    void hideTrace(int trace) {
        visibleTraces[trace] = false;
        hiddenTraceCount++;
    }

    private static void disableDataProcessing(DataProcessingConfig dataProcessingConfig) {
        dataProcessingConfig.setGroupingEnabled(false);
        dataProcessingConfig.setCropEnabled(false);
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
        return dataManager.getData(xScale, tracePainter.markWidth());
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

    Range getFullXMinMax(Scale xScale) {
        return dataManager.getFullXMinMax(xScale);
    }

    double getBestExtent(int drawingAreaWidth) {
        return dataManager.getBestExtent(drawingAreaWidth, tracePainter.markWidth());
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
        return tracePainter.traceYMinMax(getData(xScale), trace);
    }

    Range xMinMax(Scale xScale) {
        return tracePainter.xMinMax(getData(xScale));
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
            if(visibleTraces[trace]) {
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
        if(visibleTraces[trace]) {
            tracePainter.drawTrace(canvas, getData(xScale), trace, getTraceColor(trace), traceCount(), isSplit, xScale, yScale);
        }
    }

    NamedValue xValue(int dataIndex, Scale xScale) {
        double xValue = getData(xScale).value(dataIndex, 0);
        return new NamedValue("x: ", xScale.formatDomainValue(xValue));
    }

}
