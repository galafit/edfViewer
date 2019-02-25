package com.biorecorder.basechart.graphics;

/**
 * Created by galafit on 29/12/17.
 */
public class BColor {
    public static final BColor STEEL_LIGHT = new BColor(240, 241, 245);
    public static final BColor STEEL_DARK = new BColor(20, 20, 25);
    public static final BColor BEIGE_INTENSE = new BColor(245, 226, 208);
    public static final BColor BEIGE = new BColor(160, 140, 110);
    public static final BColor BEIGE_LIGHT = new BColor(70, 65, 45);
    public static final BColor BEIGE_WHITE =  new BColor(245, 240, 238);
    public static final BColor WHITE_DARK = new BColor(250, 250, 250);
    public static final BColor BLACK_LIGHT = new BColor(8, 8, 10);
    public static final BColor BLACK = new BColor(0, 0, 0);
    public static final BColor WHITE = new BColor(255, 255, 255);
    public static final BColor RED = new BColor(255, 0, 0);
    public static final BColor GREEN = new BColor(0, 255, 0);
    public static final BColor BLUE = new BColor(0, 0, 255);
    public static final BColor CYAN = new BColor(0, 255, 255);
    public static final BColor YELLOW = new BColor(255, 255, 0);
    public static final BColor MAGENTA = new BColor(255, 0, 255);
    public static final BColor PINK = new BColor(255, 175, 175);
    public static final BColor GRAY = new BColor(100, 100, 100);
    public static final BColor GRAY_LIGHT = new BColor(210, 210, 210);

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
            throw new IllegalArgumentException("Color parameter outside of expected minMax:"
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
