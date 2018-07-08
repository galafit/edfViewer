package com.biorecorder.basechart.chart.scales;

import java.text.MessageFormat;

/**
 * Scientific Notation, Exponential Notation (Экспоненциальная запись, Нормализованная запись)
 */
public class NormalizedNumber {
    private double mantissa;
    private int power;

    public NormalizedNumber(double mantissa, int power) {
        if(mantissa != 0 && (mantissa >= 10 || mantissa < 1)) {
            String errMsg = MessageFormat.format("Invalid mantissa: {0}. In Scientific Notation expected 1<= mantissa < 10.", mantissa);
            throw new IllegalArgumentException(errMsg);
        }
        this.mantissa = mantissa;
        this.power = power;
    }

    public NormalizedNumber(double value) {
        power = calculatePower(value);
        mantissa = value/Math.pow(10,power);
    }

    private int calculatePower(double value){
        if (value == 0) {return 0;}

        double power = Math.log10(Math.abs(value));
        int powerInt = (int) power;
        if ((power < 0) && (power != powerInt)) {
            powerInt = powerInt - 1;
        }
        return powerInt;
    }

    public double getMantissa() {
        return mantissa;
    }

    public int getPower() {
        return power;
    }

    public double getValue() {
        return mantissa * Math.pow(10, power);
    }

  
    public int getNumberOfSignificantDigits() { 
        if(mantissa == 0) {
            return 1;
        }
        int maxMantissaSize = 16;
        for(int size = 0; size <= maxMantissaSize; size++) {
            int firstDigits = (int) (mantissa * Math.pow(10, size));
            if (mantissa * Math.pow(10, size) == firstDigits) {
                return size + 1;
            }
        }
        return maxMantissaSize;
    }

    public int getPowerOfLastSignificantDigit() {
        if(mantissa == 0) {
            return 0;
        }
        return  power - getNumberOfSignificantDigits() + 1;
    }


    /**
     * Unit tests {@code NormalizedNumber}.
     *
     * String.format: https://www.dotnetperls.com/format-java
     */
    public static void main(String[] args) {
        String format = "%1$10s :  mantissa = %2$9s  power = %3$3s  PowerOfLastSignificantDigit = %4$2s";
        double value = 100;
        NormalizedNumber number = new NormalizedNumber(value);
        System.out.println(String.format(format, value, number.getMantissa(), number.getPower(), number.getPowerOfLastSignificantDigit()));

        value = 0.03234234;
        number = new NormalizedNumber(value);
        System.out.println(String.format(format, value, number.getMantissa(), number.getPower(), number.getPowerOfLastSignificantDigit()));

        value = -7000.15;
        number = new NormalizedNumber(value);
        System.out.println(String.format(format, value, number.getMantissa(), number.getPower(), number.getPowerOfLastSignificantDigit()));

        value = -20;
        number = new NormalizedNumber(value);
        System.out.println(String.format(format, value, number.getMantissa(), number.getPower(), number.getPowerOfLastSignificantDigit()));

        value = 0;
        number = new NormalizedNumber(value);
        System.out.println(String.format(format, value, number.getMantissa(), number.getPower(), number.getPowerOfLastSignificantDigit()));

        value = 100000001.0000001;
        number = new NormalizedNumber(value);
        System.out.println(String.format(format, value, number.getMantissa(), number.getPower(), number.getPowerOfLastSignificantDigit()));

    }
}
