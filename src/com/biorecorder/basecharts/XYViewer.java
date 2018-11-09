package com.biorecorder.basecharts;

import com.biorecorder.data.frame.DataSeries;

/**
 * Created by galafit on 2/11/17.
 */
public class XYViewer {
    DataSeries dataSeries;

    public void setData(DataSeries dataSeries) {
        this.dataSeries = dataSeries;
    }

    public int size() {
        return (int)dataSeries.size();
    }

    public double getX(int index) {
        return dataSeries.getXValue(index);
    }

    public double getY(int index) {
        return dataSeries.getYValue(0, index);
    }

    public Range getYExtremes() {
        return dataSeries.getYExtremes(0);
    }

    public Range getXExtremes() {
        return dataSeries.getXExtremes();
    }

    public long findNearest(double xValue) {
        return dataSeries.findNearestData(xValue);
    }

}
