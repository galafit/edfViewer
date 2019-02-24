package com.biorecorder.basechart.scales;

/**
 * Created by galafit on 24/2/19.
 */
public class TickValue {
    private final long digits;
    private final int power;

    public TickValue(long digits, int power) {
        this.digits = digits;
        this.power = power;
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

    public long getDigits() {
        return digits;
    }

    public int getPower() {
        return power;
    }

    public double getValue() {
        return digits * Math.pow(10, power);
    }
}
