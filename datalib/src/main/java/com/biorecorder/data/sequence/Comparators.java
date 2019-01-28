package com.biorecorder.data.sequence;

/**
 * A class that defines "compare" methods for different types
 */
public class Comparators {
    public static int compareInt(int x, int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    public static int compareShort(short x, short y) {
        return x - y;
    }

    public static int compareLong(long x, long y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }

    public static int compareFloat(float f1, float f2) {
        if (f1 < f2)
            return -1;           // Neither val is NaN, thisVal is smaller
        if (f1 > f2)
            return 1;            // Neither val is NaN, thisVal is larger
        int thisBits = Float.floatToIntBits(f1);
        int anotherBits = Float.floatToIntBits(f2);
        return (thisBits == anotherBits ?  0 : // Values are equal
                (thisBits < anotherBits ? -1 : // (-0.0, 0.0) or (!NaN, NaN)
                        1));                          // (0.0, -0.0) or (NaN, !NaN)
    }

    public static int compareDouble(double d1, double d2) {
        if (d1 < d2)
            return -1;           // Neither val is NaN, thisVal is smaller
        if (d1 > d2)
            return 1;            // Neither val is NaN, thisVal is larger
        long thisBits = Double.doubleToLongBits(d1);
        long anotherBits = Double.doubleToLongBits(d2);
        return (thisBits == anotherBits ?  0 : // Values are equal
                (thisBits < anotherBits ? -1 : // (-0.0, 0.0) or (!NaN, NaN)
                        1));                          // (0.0, -0.0) or (NaN, !NaN)
    }
}
