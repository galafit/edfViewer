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
    protected String name;
    protected TraceDataManager dataManager;
    protected BColor[] curvesColors;
    private final int curveCount;

    public Trace(ChartData data) {
        dataManager = new TraceDataManager(data, new DataProcessingConfig());
        curveCount = curveCount(data);
    }


    public final NearestCurvePoint nearest(int x, int y, int curveNumber) {
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

    public final BPoint dataPosition(int curveNumber, int dataIndex) {
        return dataPosition(curveNumber, dataIndex, dataManager.getData(xScale, getMarkSize()));
    }

    public final InfoItem[] info(int curveNumber, int dataIndex) {
        return info(curveNumber, dataIndex, dataManager.getData(xScale, getMarkSize()));
    }

    public final BRange yMinMax(int curveNumber) {
       return yMinMax(curveNumber, dataManager.getData(xScale, getMarkSize()));
    }

    public void setDataProcessingConfig(DataProcessingConfig dataProcessingConfig) {
        dataManager.setConfig(dataProcessingConfig);
    }

    public void setCurvesColors(BColor... colors) {
        curvesColors = colors;
    }

    public BColor[] getCurvesColors() {
        return curvesColors;
    }

    public BColor getCurveMainColor(int curveNumber) {
        return curvesColors[curveNumber % curvesColors.length];
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

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setXScale(Scale xScale) {
        this.xScale = xScale;
    }

    public void setYScales(Scale... yScales) {
        this.yScales = yScales;
    }
    protected abstract int curveCount(ChartData data);
    protected abstract BRange yMinMax(int curveNumber, ChartData data);
    protected abstract BPoint dataPosition(int curveNumber, int dataIndex, ChartData data);
    protected abstract NearestCurvePoint nearest(int x, int y, int curveNumber, ChartData data);
    protected abstract InfoItem[] info(int curveNumber, int dataIndex, ChartData data);

    public abstract int getMarkSize();

    public abstract String getCurveName(int curveNumber);

    protected abstract void draw(BCanvas canvas, ChartData data);

}
