package com.biorecorder.basechart;

import com.biorecorder.basechart.traces.Trace;

/**
 * Created by galafit on 14/2/19.
 */
public class NearestCurvePoint {
    private Trace trace;
    private int pointIndex;
    private int curveNumber;
    private double distance;

    public NearestCurvePoint(Trace trace, int curveNumber, int pointIndex, double distance) {
        this.trace = trace;
        this.pointIndex = pointIndex;
        this.curveNumber = curveNumber;
        this.distance = distance;
    }

    public int getPointIndex() {
        return pointIndex;
    }

    public int getCurveNumber() {
        return curveNumber;
    }

    public double getDistance() {
        return distance;
    }

    public Trace getTrace() {
        return trace;
    }
}
