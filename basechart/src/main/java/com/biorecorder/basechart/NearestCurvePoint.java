package com.biorecorder.basechart;

/**
 * Created by galafit on 26/5/19.
 */
public class NearestCurvePoint {
    private TraceCurvePoint curvePoint;
    private int distanceSqw;

    public NearestCurvePoint(TraceCurvePoint curvePoint, int distanceSqw) {
        this.curvePoint = curvePoint;
        this.distanceSqw = distanceSqw;
    }

    public TraceCurvePoint getCurvePoint() {
        return curvePoint;
    }

    public int getDistanceSqw() {
        return distanceSqw;
    }
}
