package com.biorecorder.basechart;

/**
 * Created by galafit on 15/2/19.
 */
public class NearestPoint {
    private final TraceCurvePoint curvePoint;
    private final int distanceSq; // distance square

    public NearestPoint(TraceCurvePoint curvePoint, int distanceSq) {
        this.curvePoint = curvePoint;
        this.distanceSq = distanceSq;
    }

    public TraceCurvePoint getCurvePoint() {
        return curvePoint;
    }

    public int getDistanceSq() {
        return distanceSq;
    }

    public int getPointIndex() {
        return curvePoint.getPointIndex();
    }

    public int getCurveNumber() {
        return curvePoint.getCurveNumber();
    }

    public Trace getTrace() {
        return curvePoint.getTrace();
    }
}
