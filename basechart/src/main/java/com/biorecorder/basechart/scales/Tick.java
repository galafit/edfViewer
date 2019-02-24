package com.biorecorder.basechart.scales;

/**
 * Created by galafit on 5/9/17.
 */
public class Tick {
    private TickValue value;
    private String label;

    public Tick(TickValue value, String label) {
        this.value = value;
        this.label = label;
    }

    public TickValue getTickValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }
}