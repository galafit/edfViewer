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

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof TraceCurvePoint)) {
            return false;
        }

        TraceCurvePoint tcp = (TraceCurvePoint) o;
        return ((trace == tcp.trace) &&
                (curve == tcp.curve) && (pointIndex == tcp.pointIndex));
    }
}
