package com.biorecorder.data.sequence;

/**
 * A class that defines "compare" methods for different types
 */
public class PrimitiveUtils {
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

    public static double doubleToDouble(double d) {
        return d;
    }

    public static float doubleToFloat(double d) {
        return (float) d;
    }

    public static long doubleToLong(double d) {
        return (long)d;
    }

    public static int doubleToInt(double d) {
        return (int) d;
    }

    public static short doubleToShort(double d) {
        int i = (int) d;
        if(i > Short.MAX_VALUE) {
            return Short.MAX_VALUE;
        }
        if(i < Short.MIN_VALUE) {
            return Short.MIN_VALUE;
        }
        return (short) i;
    }

    public static void main(String[] args) {

        double d = 4786777867867868654674678346734763478673478654478967.77;
        System.out.printf("float  %f\n", (float)d);
        System.out.printf("long   %d\n", (long)d);
        System.out.printf("int    %d\n", (int)d);
        System.out.printf("short  %d\n", (short)d);

        System.out.println();
        System.out.printf("float  %f\n", doubleToFloat(d));
        System.out.printf("long   %d\n", doubleToLong(d));
        System.out.printf("int    %d\n", doubleToInt(d));
        System.out.printf("short  %d\n", doubleToShort(d));

        d = -4786777867867868654674678346734763478673478654478967.77;

        System.out.println();
        System.out.printf("float  %f\n", doubleToFloat(d));
        System.out.printf("long   %d\n", doubleToLong(d));
        System.out.printf("int    %d\n", doubleToInt(d));
        System.out.printf("short  %d\n", doubleToShort(d));


    }

}
