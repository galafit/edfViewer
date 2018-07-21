package com.biorecorder.basechart.config;

import java.util.ArrayList;

/**
 * Created by galafit on 18/7/18.
 */
public class DataConfig {
    double xStartValue = 0;
    double xInterval = 1;
    int xColumnIndex = -1;
    ArrayList<Integer> yColumnIndexes = new ArrayList<>(1);

    public void setXColumnIndex(int xColumnIndex) {
        this.xColumnIndex = xColumnIndex;
    }

    public void setXStartAndInterval(double startValue, double interval) {
        xStartValue = startValue;
        xInterval = interval;
    }

    public void addYColumn(int yColumnIndex) {
        yColumnIndexes.add(yColumnIndex);
    }

    public int YColumnsCount() {
        return yColumnIndexes.size();
    }

    public int getYColumnIndex(int yColumnNumber) {
       return yColumnIndexes.get(yColumnNumber);
    }

    public int getXColumnIndex() {
        return xColumnIndex;
    }

    public double getXStartValue() {
        return xStartValue;
    }

    public double getXInterval() {
        return xInterval;
    }
}
