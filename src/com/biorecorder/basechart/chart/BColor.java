package com.biorecorder.basechart.chart;

/**
 * Created by galafit on 29/12/17.
 */
public class BColor {
    public static final BColor BLACK = new BColor(0, 0, 0);
    public static final BColor WHITE = new BColor(255, 255, 255);
    public static final BColor RED = new BColor(255, 0, 0);
    public static final BColor GREEN = new BColor(0, 255, 0);
    public static final BColor BLUE = new BColor(0, 0, 255);
    public static final BColor CYAN = new BColor(0, 255, 255);
    public static final BColor YELLOW = new BColor(255, 255, 0);
    public static final BColor MAGENTA = new BColor(255, 0, 255);
    public static final BColor PINK = new BColor(255, 175, 175);
    public static final BColor GRAY = new BColor(128, 128, 128);
    public static final BColor LIGHT_GRAY = new BColor(192, 192, 192);
    public static final BColor DARK_GRAY = new BColor(64, 64, 64);

    private int r;
    private int g;
    private int b;
    private int a;

    public BColor(int r, int g, int b, int a) {
        boolean rangeError = false;
        String badComponentString = "";
        if (a < 0 || a > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Alpha";
        }
        if (r < 0 || r > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Red";
        }
        if (b < 0 || b > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Blue";
        }
        if (g < 0 || g > 255) {
            rangeError = true;
            badComponentString = badComponentString + " Green";
        }
        if (rangeError == true) {
            throw new IllegalArgumentException("Color parameter outside of expected range:"
                            + badComponentString);
        }
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public BColor(int r, int g, int b) {
       this(r, g, b, 255);
    }

    public int getRed() {
        return r;
    }

    public int getGreen() {
        return g;
    }

    public int getBlue() {
        return b;
    }

    public int getAlpha() {
        return a;
    }
}
