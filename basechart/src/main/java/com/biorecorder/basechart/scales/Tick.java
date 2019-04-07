package com.biorecorder.basechart.scales;

import com.biorecorder.data.utils.NormalizedNumber;

/**
 * Created by galafit on 5/9/17.
 */
public class Tick {
    private NormalizedNumber value;
    private String label;

    public Tick(NormalizedNumber value, String label) {
        this.value = value;
        this.label = label;
    }

    public NormalizedNumber getTickValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }
}