package com.biorecorder.basechart;

import java.awt.*;

/**
 * Created by galafit on 14/9/17.
 */
public class BStroke {
    public static final String SOLID = "Solid";
    public static final String DASH_LONG = "DashLong";
    public static final String DASH_SHORT = "DashShort";
    public static final String DASH_DOT = "DashDot";
    public static final String DOT = "Dot";

    private int width = 1;
    private String style = SOLID;

    public BStroke(int width, String style) {
        this.width = width;
        this.style = style;
    }

    public BStroke(int width) {
        this.width = width;
    }

    public BStroke(BStroke stroke) {
        width = stroke.width;
        style = stroke.style;
    }

    public BStroke() {
    }

    public Stroke getStroke() {
        return new BasicStroke(getWidth());
    }

    public int getWidth() {
        return width;
    }

    public String getStyle() {
        return style;
    }
}
