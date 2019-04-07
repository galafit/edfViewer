package com.biorecorder.data.utils;

/**
 * Created by galafit on 7/4/19.
 */
public class NormalizedNumber {
    private static final int MAX_MANTISSA_SIZE = 17;
    private long digits;
    private int power;


    public NormalizedNumber(long digits1, int power1) {
        this.digits = digits1;
        this.power = power1;
        if(digits != 0) {
            while(digits % 10 == 0) {
                digits /=  10;
                power++;
            }
        } else {
            power = 0;
        }
    }

    public NormalizedNumber(double value) {
        if(value == 0) {
            digits = 0;
            power = 0;
            return;
        }

        int fistDigitPower = calculateFirstDigitPower(value);
        double mantissa = value / Math.pow(10, fistDigitPower);

        for(int size = 0; size <= MAX_MANTISSA_SIZE; size++) {
            digits = (long) (mantissa * Math.pow(10, size));
            power = fistDigitPower - size;
            if (mantissa * Math.pow(10, size) == digits) {
                break;
            }
        }
    }


    private int calculateFirstDigitPower(double value){
        if (value == 0) {return 1;}

        double power = Math.log10(Math.abs(value));
        int powerInt = (int) power;
        if ((power < 0) && (power != powerInt)) {
            powerInt = powerInt - 1;
        }
        return powerInt;
    }

    public int compare(double d) {
        if(power > 0) {
            return Double.compare(digits * Math.pow(10, power), d);
        }
        if(power < 0) {
            return Double.compare(digits, d *  Math.pow(10, -power));
        }
        return Double.compare(digits, d);
    }

    public NormalizedNumber multiply(int number) {
        return new NormalizedNumber(digits * number, power);
    }


    private double mantissa(double value) {
        return value/Math.pow(10,calculateFirstDigitPower(value));
    }

    public long getDigits() {
        return digits;
    }

    public int getDigitsPower() {
        return power;
    }

    public double getMantissa() {
       return mantissa(digits);
    }

    public int getMantissaPower() {
       return calculateFirstDigitPower(digits)  + power;
    }

    public double getValue() {
        return digits * Math.pow(10, power);
    }

    /**
     * Unit tests {@code NormalizedNumber}.
     *
     * String.format: https://www.dotnetperls.com/format-java
     */
    public static void main(String[] args) {
        String format = "%1$10s :  mantissa = %2$9s  mantissaPower = %3$3s  power = %4$3s  digits = %5$2s";
        double value = 101;
        NormalizedNumber number = new NormalizedNumber(value);
        System.out.println(String.format(format, value, number.getMantissa(), number.getMantissaPower(), number.getDigitsPower(), number.getDigits()));

        value = 0.03234234;
        number = new NormalizedNumber(value);
        System.out.println(String.format(format, value, number.getMantissa(), number.getMantissaPower(), number.getDigitsPower(), number.getDigits()));

        value = -7000.15;
        number = new NormalizedNumber(value);
        System.out.println(String.format(format, value, number.getMantissa(), number.getMantissaPower(), number.getDigitsPower(), number.getDigits()));

        value = -20;
        number = new NormalizedNumber(value);
        System.out.println(String.format(format, value, number.getMantissa(), number.getMantissaPower(), number.getDigitsPower(), number.getDigits()));

        value = 0;
        number = new NormalizedNumber(value);
        System.out.println(String.format(format, value, number.getMantissa(), number.getMantissaPower(), number.getDigitsPower(), number.getDigits()));

        value = 100000001.0000001;
        number = new NormalizedNumber(value);
        System.out.println(String.format(format, value, number.getMantissa(), number.getMantissaPower(), number.getDigitsPower(), number.getDigits()));

    }
}
