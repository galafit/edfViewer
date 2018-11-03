package com.biorecorder;

import java.math.BigDecimal;

/**
 * Created by galafit on 19/10/18.
 */
public class Test {
    public static void main(String[] args) {
        double value1 = 5.345;
        float value2 = 5.345f;
        float value3 = new Double(value1).floatValue();
        System.out.println(Double.doubleToLongBits(value3)+" doubleBits  floatBits "+ Double.doubleToLongBits(value2)); // Display the string.

        double dNan = Double.NaN;
        float fNan = Float.NaN;
        float fNan1 = new Double(dNan).floatValue();
        System.out.println(Float.floatToIntBits(fNan)+" fl1  fl2 "+ Float.floatToIntBits(fNan1)); // Display the string.


        testDoubleToFloat(2974815.78);
       // testDoubleToFloat(-2974815.78);


        float f1 = 2974815.78f;
        double d = f1;
        testDoubleToFloat(d);
    }

    static void testDoubleToFloat(double d) {
        float f = (float) d;

        System.out.println();
        System.out.println(String.format("double %.10f\t%s", d, Long.toBinaryString(Double.doubleToRawLongBits(d))));
        System.out.println(String.format("float  %.10f\t   %s", f, Integer.toBinaryString(Float.floatToRawIntBits(f))));
    }

    static void testDoubleToFloat1(double d) {

        String dString = String.format("%.10f", d);
        System.out.println("dString  " + dString);
        BigDecimal bd = new BigDecimal(dString);
        float f = bd.floatValue();

        System.out.println();
        System.out.println(String.format("double %.10f\t%s", d, Long.toBinaryString(Double.doubleToRawLongBits(d))));
        System.out.println(String.format("float  %.10f\t   %s", f, Integer.toBinaryString(Float.floatToRawIntBits(f))));
    }

}
