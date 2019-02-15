package com.biorecorder.basechart.traces;


/**
 * Created by galafit on 14/2/19.
 */
public class TraceCurve {
    protected final Trace trace;
    protected final int curveNumber;

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
