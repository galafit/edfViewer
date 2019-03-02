package com.biorecorder.basechart;

/**
 * Created by galafit on 2/3/19.
 */
public class CurveNumber {
    private final int traceNumber;
    private final int curveNumber;

    public CurveNumber(int traceNumber, int curveNumber) {
        this.traceNumber = traceNumber;
        this.curveNumber = curveNumber;
    }

    public int getTraceNumber() {
        return traceNumber;
    }

    public int getCurveNumber() {
        return curveNumber;
    }
}
