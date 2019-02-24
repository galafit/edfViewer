package com.biorecorder.basechart.scales;

/**
 * Created by galafit on 24/2/19.
 */
public class TickValue {
    private final int digits;
    private final int power;

    public TickValue(int digits, int power) {
        this.digits = digits;
        this.power = power;
    }

    public int getDigits() {
        return digits;
    }

    public int getPower() {
        return power;
    }

    public double getValue() {
        return digits * Math.pow(10, power);
    }
}
