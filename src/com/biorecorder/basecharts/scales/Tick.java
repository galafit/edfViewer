package com.biorecorder.basecharts.scales;

/**
 * Created by galafit on 5/9/17.
 */
public class Tick {
    private double value;
    private String label;

    public Tick(double value, String label) {
        this.value = value;
        this.label = label;
    }

    public double getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }
}