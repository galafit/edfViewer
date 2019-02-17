package com.biorecorder.basechart.traces;

import com.biorecorder.basechart.*;
import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.graphics.BPoint;
import com.biorecorder.basechart.scales.Scale;


/**
 * Created by galafit on 16/9/17.
 */
public abstract class Trace {
    protected Scale xScale;
    protected Scale[] yScales;
    protected TraceDataManager dataManager;
    private final int curveCount;

    public Trace(ChartData data) {
        dataManager = new TraceDataManager(data, new DataProcessingConfig());
        curveCount = curveCount(data);
    }

    public final NearestPoint nearest(int x, int y, int curveNumber) {
        return nearest(x, y, curveNumber, dataManager.getData(xScale, getMarkSize()));
    }

    public BRange getFullXMinMax() {
       return dataManager.getFullXMinMax();
    }

    public double getBestExtent(int drawingAreaWidth) {
        return dataManager.getBestExtent(drawingAreaWidth, getMarkSize());
    }

    public final int curveCount() {
        return curveCount;
    }

    public final void draw(BCanvas canvas) {
        draw(canvas, dataManager.getData(xScale, getMarkSize()));
    }

    public final BPoint curvePointPosition(int curveNumber, int dataIndex) {
        return curvePointPosition(curveNumber, dataIndex, dataManager.getData(xScale, getMarkSize()));
    }

    public final TooltipItem[] info(int curveNumber, int dataIndex) {
        return info(curveNumber, dataIndex, dataManager.getData(xScale, getMarkSize()));
    }

    public final BRange yMinMax(int curveNumber) {
       return yMinMax(curveNumber, dataManager.getData(xScale, getMarkSize()));
    }

    public void setDataProcessingConfig(DataProcessingConfig dataProcessingConfig) {
        dataManager.setConfig(dataProcessingConfig);
    }

    public Scale getXScale() {
        return xScale;
    }

    public int yScaleCount() {
        return yScales.length;
    }

    public final Scale getYScale(int curveNumber) {
        if(curveNumber < yScales.length - 1) {
            return yScales[curveNumber];
        }
        return yScales[yScales.length - 1];
    }

    public void setXScale(Scale xScale) {
        this.xScale = xScale;
    }

    public void setYScales(Scale... yScales) {
        this.yScales = yScales;
    }

    public abstract int getMarkSize();

    public abstract String getCurveName(int curveNumber);

    public abstract void setCurveName(int curveNumber, String name);

    protected  BPoint curvePointPosition(int curveNumber, int dataIndex, ChartData data) {
        return new BPoint((int)xScale.scale(data.getValue(dataIndex, 0)), (int)getYScale(curveNumber).scale(data.getValue(dataIndex, curveNumber + 1)));
    }

    protected NearestPoint nearest(int x, int y, int curveNumber1, ChartData data) {
        double xValue =  xScale.invert(x);
        int pointIndex = dataManager.nearest(xValue);

        int distanceMin = 0;
        int curveNumber = 0;

        int startCurve = 0;
        int curveCount = curveCount();
        if(curveNumber1 >= 0) {
            startCurve = curveNumber1;
            curveCount = 1;
        }

        for (int i = startCurve; i < startCurve + curveCount; i++) {
            BPoint pointPosition = curvePointPosition(i, pointIndex);
            int dy = pointPosition.getY() - y;
            int dx = pointPosition.getX() - x;
            int dx2 = dx * dx;
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


    protected abstract int curveCount(ChartData data);
    protected abstract BRange yMinMax(int curveNumber, ChartData data);
    protected abstract TooltipItem[] info(int curveNumber, int dataIndex, ChartData data);
    protected abstract void draw(BCanvas canvas, ChartData data);

}
