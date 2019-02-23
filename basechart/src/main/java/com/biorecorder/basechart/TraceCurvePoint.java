package com.biorecorder.basechart;

/**
 * Created by galafit on 15/2/19.
 */
public class TraceCurvePoint extends TraceCurve {
    protected final int pointIndex;

    public TraceCurvePoint(Trace trace, int curveNumber, int pointIndex) {
        super(trace, curveNumber);
        this.pointIndex = pointIndex;
    }

    public int getPointIndex() {
        return pointIndex;
    }
}
