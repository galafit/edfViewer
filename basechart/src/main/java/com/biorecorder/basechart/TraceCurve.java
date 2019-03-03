package com.biorecorder.basechart;


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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TraceCurve) {
            TraceCurve r = (TraceCurve) obj;
            return ((trace == r.trace) &&
                    (curveNumber == r.curveNumber));

        }
        return super.equals(obj);

    }

    /**
     * https://www.mkyong.com/java/java-how-to-overrides-equals-and-hashcode/
     * https://medium.com/codelog/overriding-hashcode-method-effective-java-notes-723c1fedf51c
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + curveNumber;
        result = 31 * result + trace.hashCode();
        return result;
    }

}
