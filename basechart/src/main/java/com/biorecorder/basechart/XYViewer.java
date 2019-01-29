package com.biorecorder.basechart;

/**
 * Created by galafit on 2/11/17.
 */
public class XYViewer {
    ChartData data;

    public void setData(ChartData data) {
        this.data = data;
    }

    public int size() {
        return data.rowCount();
    }

    public double getX(int index) {
        return data.getValue(index, 0);
    }

    public double getY(int index) {
        return data.getValue(index, 1);
    }

    public BRange getYRange() {
        return data.getColumnMinMax(1);
    }

    public BRange getXRange() {
        return data.getColumnMinMax(0);
    }

    public long nearest(double xValue) {
        return data.nearest(0, xValue);
    }

}
