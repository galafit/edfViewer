package com.biorecorder.basecharts.traces;

import com.biorecorder.basecharts.*;
import com.biorecorder.data.frame.DataSeries;
import com.biorecorder.basecharts.graphics.BCanvas;
import com.biorecorder.basecharts.graphics.BColor;
import com.biorecorder.basecharts.graphics.BPoint;
import com.biorecorder.basecharts.scales.Scale;


/**
 * Created by galafit on 16/9/17.
 */
public abstract class Trace {
    private int xAxisIndex;
    private int yAxisIndex;
    private String name;
    private DataSeries data;

    public void setData(DataSeries data) {
        this.data = data;
    }

    public DataSeries getData() {
        return data;
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

    public Range getXExtremes() {
        return data.getXExtremes();
    }

    public long findNearestData(int x, int y, Scale xScale, Scale yScale) {
        return data.findNearestData(xScale.invert(x));

    }

    public abstract int getMarkSize();

    public abstract BColor getMainColor();

    public abstract void setMainColor(BColor color);

    public abstract Range getYExtremes();

    public abstract BPoint getDataPosition(int dataIndex, Scale xScale, Scale yScale);

    public abstract InfoItem[] getInfo(int dataIndex, Scale xScale, Scale yScale);

    public abstract void draw(BCanvas canvas, Scale xScale, Scale yScale);
}