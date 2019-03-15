package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.scales.Scale;


/**
 * Created by galafit on 16/9/17.
 */
public abstract class Trace {
    protected Scale xScale;
    protected Scale[] yScales;
    protected TraceDataManager dataManager;
    protected final int curveCount;
    protected final String[] curveNames;

    public Trace(ChartData data) {
        dataManager = new TraceDataManager(data);
        curveCount = curveCount(data);
        curveNames = new String[curveCount];
        initiateCurveNames(data);
    }

    public void addDataAppendListener(DataAppendListener listener) {
        dataManager.addDataAppendListener(listener);
    }

    public final String getCurveName(int curveNumber) {
        return curveNames[curveNumber];
    }

    public final void setCurveName(int curveNumber, String name) {
        curveNames[curveNumber] = name;
    }

    protected final void checkCurveNumber(int curveNumber) {
        if(curveNumber >= curveCount) {
            String errMsg = "Curve = " + curveNumber + " Number of curves: " + curveCount;
            throw new IllegalArgumentException(errMsg);
        }
    }
    
    private ChartData getData() {
        return dataManager.getData(xScale, getMarkSize());
    }

    public Range getFullXMinMax() {
       return dataManager.getFullXMinMax();
    }

    public double getBestExtent(int drawingAreaWidth) {
        return dataManager.getBestExtent(drawingAreaWidth, getMarkSize());
    }

    public final int curveCount() {
        return curveCount;
    }

    public final void draw(BCanvas canvas) {
        draw(canvas, getData());
    }

    public NamedValue xValue(int dataIndex) {
        double xValue = getData().getValue(dataIndex, 0);
        return new NamedValue("x: ", xValue, xScale.formatDomainValue(xValue));
    }

    public int xPosition(int dataIndex) {
        double xValue = getData().getValue(dataIndex, 0);
        return (int)xScale.scale(xValue);
    }

    public final NamedValue[] curveValues(int curveNumber, int dataIndex) {
        return curveValues(curveNumber, dataIndex, getData());
    }

    public final int curveYPosition(int curveNumber, int dataIndex) {
        return curveYPosition(curveNumber, dataIndex, getData());
    }

    public final Range curveYMinMax(int curveNumber) {
       return curveYMinMax(curveNumber, getData());
    }

    public void setDataProcessingConfig(DataProcessingConfig dataProcessingConfig) {
        dataManager.setConfig(dataProcessingConfig);
    }

    public void setXScale(Scale xScale) {
        this.xScale = xScale;
    }

    public void setYScales(Scale... yScales) {
        this.yScales = yScales;
    }

    public Scale getXScale() {
        return xScale;
    }

    public Scale[] getYScales() {
        return yScales;
    }

    public final Scale getYScale(int curveNumber) {
        if(curveNumber < yScales.length - 1) {
            return yScales[curveNumber];
        }
        return yScales[yScales.length - 1];
    }

    public abstract int getMarkSize();

    public NearestPoint nearest(int x, int y, int curveNumber1) {
        double xValue =  xScale.invert(x);
        int pointIndex = dataManager.nearest(xValue);
        int dx = xPosition(pointIndex) - x;
        int dx2 = dx * dx;
        int distanceMin = 0;
        int curveNumber = 0;

        int startCurve = 0;
        int curveCount = curveCount();
        if(curveNumber1 >= 0) {
            startCurve = curveNumber1;
            curveCount = 1;
        }

        for (int i = startCurve; i < startCurve + curveCount; i++) {
            int dy = curveYPosition(i, pointIndex) - y;
            int distance = dx2 + dy * dy;
            if(distanceMin == 0 || distanceMin > distance) {
                curveNumber = i;
                distanceMin = distance;
            }
        }
        return new NearestPoint(new TraceCurvePoint(this, curveNumber, pointIndex), distanceMin);
    }

    public abstract void setCurveColor(int curveNumber, BColor color);

    public abstract BColor getCurveColor(int curveNumber);

    protected abstract void initiateCurveNames(ChartData data);
    protected abstract int curveYPosition(int curveNumber, int dataIndex, ChartData data);
    protected abstract NamedValue[] curveValues(int curveNumber, int dataIndex, ChartData data);
    protected abstract int curveCount(ChartData data);
    protected abstract Range curveYMinMax(int curveNumber, ChartData data);
    protected abstract void draw(BCanvas canvas, ChartData data);

}
