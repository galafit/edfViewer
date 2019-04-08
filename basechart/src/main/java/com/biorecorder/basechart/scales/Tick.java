package com.biorecorder.basechart.scales;

import com.biorecorder.data.utils.NormalizedNumber;

/**
 * Created by galafit on 5/9/17.
 */
public class Tick {
    private NormalizedNumber value;
    private String label;

    public Tick(NormalizedNumber tickValue, String tickLabel) {
        this.value = tickValue;
        this.label = tickLabel;
    }

    public NormalizedNumber getTickValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }
}