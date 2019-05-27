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
 * Created by galafit on 16/9/17.
 */
class Trace {
    private int xIndex;
    private int yStartIndex;

    private boolean isSplit;
    private TraceDataManager dataManager;
    private TracePainter tracePainter;
    private NearestSearchType nearestSearchType;
    private int curveCount;
    private String[] curveNames;
    private BColor[] curveColors;

    Trace(ChartData data, TracePainter tracePainter, boolean isSplit, DataProcessingConfig dataProcessingConfig, int xIndex, int yStartIndex) {
        if (tracePainter.traceType() == TraceType.LINE) {
            nearestSearchType = NearestSearchType.X;
        } else {
            disableDataProcessing(dataProcessingConfig);
            nearestSearchType = NearestSearchType.XY;
        }
        this.isSplit = isSplit;
        this.xIndex = xIndex;
        this.yStartIndex = yStartIndex;
        curveCount = tracePainter.curveCount(data);
        curveNames = new String[curveCount];
        curveColors = new BColor[curveCount];
        for (int curve = 0; curve < curveCount; curve++) {
            curveNames[curve] = tracePainter.curveName(data, curve);
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
        dataManager = new TraceDataManager(data, dataProcessingConfig);
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

    int getYIndex(int curve) {
        checkCurveNumber(curve);
        if (isSplit) {
            return yStartIndex + curve * 2;
        }
        return yStartIndex;
    }


    private ChartData getData(Scale xScale) {
        return dataManager.getData(xScale, tracePainter.markWidth());
    }

    private void checkCurveNumber(int curveNumber) {
        if (curveNumber >= curveCount) {
            String errMsg = "Curve = " + curveNumber + " Number of curves: " + curveCount;
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

    NamedValue[] curveValues(int dataIndex, int curve, Scale xScale, Scale yScale) {
        checkCurveNumber(curve);
        return tracePainter.curvePointValues(getData(xScale), dataIndex, curve, xScale, yScale);
    }

    BRectangle curvePointHoverArea(int dataIndex, int curve, Scale xScale, Scale yScale) {
        checkCurveNumber(curve);
        return tracePainter.curvePointHoverArea(getData(xScale), dataIndex, curve, xScale, yScale);
    }

    Range curveYMinMax(int curve, Scale xScale, Scale yScale) {
        checkCurveNumber(curve);
        return tracePainter.curveYMinMax(getData(xScale), curve);
    }

    Range xMinMax(Scale xScale) {
        return tracePainter.xMinMax(getData(xScale));
    }

    int distanceSqw(int pointIndex, int curve, int x, int y, Scale xScale, Scale yScale) {
        checkCurveNumber(curve);
        BRectangle hoverRect = tracePainter.curvePointHoverArea(getData(xScale), pointIndex, curve, xScale, yScale);
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

    int curveCount() {
        return curveCount;
    }


    @Nullable
    NearestCurvePoint nearest(int x, int y, int curve, Scale xScale, Scale yScale) {
        double xValue = xScale.invert(x);
        int pointIndex = dataManager.nearest(xValue);
        if (pointIndex < 0) {
            return null;
        }
        int distance = distanceSqw(pointIndex, curve, x, y, xScale, yScale);
        if (distance >= 0) {
            return new NearestCurvePoint(new TraceCurvePoint(this, curve, pointIndex), distance);
        } else {
            return null;
        }
    }


    @Nullable
    NearestCurvePoint nearest(int x, int y, Scale xScale, Scale[] yScales) {
        double xValue = xScale.invert(x);
        int pointIndex = dataManager.nearest(xValue);
        if (pointIndex < 0) {
            return null;
        }
        int minDistance = -1;
        int closestCurve = -1;
        for (int curve = 0; curve < curveCount; curve++) {
            int distance = distanceSqw(pointIndex, curve, x, y, xScale, yScales[curve]);
            if (distance == 0) {
                return new NearestCurvePoint(new TraceCurvePoint(this, curve, pointIndex), 0);
            } else if (distance > 0) {
                if (minDistance < 0 || minDistance > distance) {
                    minDistance = distance;
                    closestCurve = curve;
                }
            }
        }
        if (closestCurve >= 0) {
            return new NearestCurvePoint(new TraceCurvePoint(this, closestCurve, pointIndex), minDistance);
        }
        return null;
    }

    NamedValue xValue(int dataIndex, Scale xScale) {
        double xValue = getData(xScale).value(dataIndex, 0);
        return new NamedValue("x: ", xScale.formatDomainValue(xValue));
    }

    void setCurveName(int curve, String name) {
        curveNames[curve] = name;
    }

    void setCurveColor(int curve, BColor color) {
        checkCurveNumber(curve);
        curveColors[curve] = color;
    }

    BColor getCurveColor(int curve) {
        checkCurveNumber(curve);
        return curveColors[curve];
    }

    String getCurveName(int curve) {
        checkCurveNumber(curve);
        return curveNames[curve];
    }

    void drawCurve(BCanvas canvas, int curve, Scale xScale, Scale yScale) {
        tracePainter.drawCurve(canvas, getData(xScale), curve, getCurveColor(curve), curveCount, isSplit, xScale, yScale);
    }
}
