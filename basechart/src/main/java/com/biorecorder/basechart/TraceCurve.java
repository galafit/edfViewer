package com.biorecorder.basechart;


/**
 * Created by galafit on 14/2/19.
 */
public class TraceCurve {
    protected final Trace trace;
    protected final int curve;

    public TraceCurve(Trace trace, int curve) {
        this.trace = trace;
        this.curve = curve;
    }

    public Trace getTrace() {
        return trace;
    }

    public int getCurve() {
        return curve;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof TraceCurve)) {
            return false;
        }

        TraceCurve tc = (TraceCurve) o;
        return ((trace == tc.trace) &&
                (curve == tc.curve));
    }


    /**
     * https://www.mkyong.com/java/java-how-to-overrides-equals-and-hashcode/
     * https://medium.com/codelog/overriding-hashcode-method-effective-java-notes-723c1fedf51c
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + curve;
        result = 31 * result + trace.hashCode();
        return result;
    }

}
