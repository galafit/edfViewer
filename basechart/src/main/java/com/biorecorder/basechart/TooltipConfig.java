package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.BColor;
import com.sun.istack.internal.Nullable;


/**
 * Created by galafit on 19/8/17.
 */
public class TooltipConfig {
    private boolean isShared = true;
    private TextStyle textStyle = new TextStyle(TextStyle.DEFAULT, TextStyle.NORMAL, 12);
    private BColor color = BColor.BLACK_LIGHT;
    private BColor backgroundColor = BColor.BEIGE_WHITE;
    private BColor headerBackgroundColor = new BColor(220, 215, 215);
    private BColor borderColor = new BColor(150, 150, 150);
    private int borderWidth = 1;
    private Insets margin;

    public TooltipConfig() {
    }

    public TooltipConfig(TooltipConfig config) {
        textStyle = config.textStyle;
        color = config.color;
        backgroundColor = config.backgroundColor;
        headerBackgroundColor = config.headerBackgroundColor;
        borderColor = config.borderColor;
        borderWidth = config.borderWidth;
        margin = config.margin;
        isShared = config.isShared;
    }

    public boolean isShared() {
        return isShared;
    }

    public void setShared(boolean shared) {
        isShared = shared;
    }

    public BColor getColor() {
        return color;
    }

    public void setColor(BColor color) {
        this.color = color;
    }

    public TextStyle getTextStyle() {
        return textStyle;
    }

    public BColor getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(BColor backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public BColor getHeaderBackgroundColor() {
        return headerBackgroundColor;
    }

    public void setHeaderBackgroundColor(BColor headerBackgroundColor) {
        this.headerBackgroundColor = headerBackgroundColor;
    }

    public BColor getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(BColor borderColor) {
        this.borderColor = borderColor;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }

    public Insets getMargin() {
        if(margin != null) {
            return margin;
        }
        return new Insets((int)(getTextStyle().getSize() * 0.4),
                (int)(getTextStyle().getSize() * 0.8),
                (int)(getTextStyle().getSize() * 0.4),
                (int)(getTextStyle().getSize() * 0.8));
    }

    public void setMargin(@Nullable Insets margin) {
        this.margin = margin;
    }
}
