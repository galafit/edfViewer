package com.biorecorder.data.utils;

/**
 * A class that defines "round" and "convert" methods for different types
 */
public class PrimitiveUtils {

    public static int longToInt(long l) {
        if(l > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if(l < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) l;
    }

    public static short longToShort(long l) {
        if(l > Short.MAX_VALUE) {
            return Short.MAX_VALUE;
        }
        if(l < Short.MIN_VALUE) {
            return Short.MIN_VALUE;
        }
        return (short) l;
    }

    public static byte longToByte(long l) {
        if(l > Byte.MAX_VALUE) {
            return Byte.MAX_VALUE;
        }
        if(l < Byte.MIN_VALUE) {
            return Byte.MIN_VALUE;
        }
        return (byte) l;
    }

    public static double roundDoubleToDouble(double d) {
        return d;
    }

    public static float roundDoubleToFloat(double d) {
        if(d > Float.MAX_VALUE) {
            return Float.MAX_VALUE;
        }
        if(d < -Float.MAX_VALUE) {
            return -Float.MAX_VALUE;
        }
        return (float) d;
    }

    public static long roundDoubleToLong(double d) {
        return Math.round(d);
    }

    public static int roundDoubleToInt(double d) {
        return longToInt(roundDoubleToLong(d));
    }

    public static short roundDoubleToShort(double d) {
        return longToShort(roundDoubleToLong(d));
    }

    public static byte roundDoubleToByte(double d) {
        return longToByte(roundDoubleToLong(d));
    }


    public static double doubleToDouble(double d) {
        return d;
    }

    public static float doubleToFloat(double d) {
        if(d > Float.MAX_VALUE) {
            return Float.MAX_VALUE;
        }
        if(d < -Float.MAX_VALUE) {
            return -Float.MAX_VALUE;
        }
        return (float) d;
    }

    public static long doubleToLong(double d) {return (long)(d);}

    public static int doubleToInt(double d) {
        return longToInt(doubleToLong(d));
    }

    public static short doubleToShort(double d) {
        return longToShort(doubleToLong(d));
    }

    public static byte doubleToByte(double d) {
        return longToByte(doubleToLong(d));
    }


    public static void main(String[] args) {

        double d = 4786777867867868654674678346734763478673478654478967.77;
        //d = Double.MIN_VALUE;
        System.out.printf("float  %f\n", (float)d);
        System.out.printf("long   %d\n", (long)d);
        System.out.printf("int    %d\n", (int)d);
        System.out.printf("short  %d\n", (short)d);
        System.out.printf("byte   %d\n", (byte)d);

        System.out.println();
        System.out.printf("float  %f\n", doubleToFloat(d));
        System.out.printf("long   %d\n", doubleToLong(d));
        System.out.printf("int    %d\n", doubleToInt(d));
        System.out.printf("short  %d\n", doubleToShort(d));
        System.out.printf("byte   %d\n", doubleToByte(d));

        d = -4786777867867868654674678346734763478673478654478967.77;
        System.out.println();
        System.out.printf("float  %f\n", doubleToFloat(d));
        System.out.printf("long   %d\n", doubleToLong(d));
        System.out.printf("int    %d\n", doubleToInt(d));
        System.out.printf("short  %d\n", doubleToShort(d));
        System.out.printf("byte   %d\n", doubleToByte(d));
    }

}
