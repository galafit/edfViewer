package com.biorecorder.basechart;

import com.biorecorder.basechart.traces.Trace;

/**
 * Created by galafit on 14/2/19.
 */
public class TraceCurve {
    private final Trace trace;
    private final int curveNumber;

    public TraceCurve(Trace trace, int curveNumber) {
        this.trace = trace;
        this.curveNumber = curveNumber;
    }

    public Trace getTrace() {
        return trace;
    }

    public int getCurveNumber() {
        return curveNumber;
    }
}
