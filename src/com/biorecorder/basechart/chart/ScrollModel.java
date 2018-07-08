package com.biorecorder.basechart.chart;

/**
 * https://docs.google.com/document/d/1x4MSKJopdGXbtrOhlEc4gD2hA0fKTB2f4ps3F2z4Dgw/edit
 * com.biorecorder.basechart.chart.painters.ScrollPainter_old model is described in Domain (Data) coordinate.
 * To draw the scroll we  need to translateScrolls all model parameters to screen coordinate.
 */
public class ScrollModel {

    private double value = 0; // viewportPosition
    private double min = 0;

    private double max = 1;
    private double extent = 1; // viewportWidth


    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getExtent() {
        return extent;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public void setExtent(double extent) {
        this.extent = extent;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double newValue) {
        if (newValue > getMax() - getExtent()) {
            newValue = getMax() - getExtent();
        }
        if (newValue < getMin()){
            newValue = getMin();
        }
        value = newValue;
    }
}
