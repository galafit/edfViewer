package com.biorecorder.data.frame;

/**
 * Created by galafit on 1/7/18.
 */
public class DataPoint {
    private double xValue;
    private double[] yValues;
    private String label;

    public double getXValue() {
        return xValue;
    }

    public void setXValue(double xValue) {
        this.xValue = xValue;
    }

    public double[] getYValues() {
        return yValues;
    }

    public void setYValues(double[] yValues) {
        this.yValues = yValues;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
