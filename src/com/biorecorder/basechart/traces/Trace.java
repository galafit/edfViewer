package com.biorecorder.basechart.traces;

import com.biorecorder.basechart.*;
import com.biorecorder.basechart.data.DataSeries;


/**
 * Created by galafit on 16/9/17.
 */
public abstract class Trace {
    private Axis xAxis;
    private Axis yAxis;
    private String name;
    private DataSeries data;

    public void setData(DataSeries data) {
        this.data = data;
    }

    public DataSeries getData() {
        return data;
    }

    public Axis getXAxis() {
        return xAxis;
    }

    public void setXAxis(Axis xAxis) {
        this.xAxis = xAxis;
    }

    public Axis getYAxis() {
        return yAxis;
    }

    public void setYAxis(Axis yAxis) {
        this.yAxis = yAxis;
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

    public abstract BColor getColor();

    public abstract int getMarkSize();

    public abstract Range getYExtremes();

    public abstract BPoint getDataPosition(int dataIndex);

    public abstract InfoItem[] getInfo(int dataIndex);

    public abstract void draw(BCanvas canvas);
}
