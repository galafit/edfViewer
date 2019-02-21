package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.graphics.TextStyle;
import com.sun.istack.internal.Nullable;


/**
 * Created by galafit on 19/8/17.
 */
public class TooltipConfig {
    private TextStyle textStyle = new TextStyle(TextStyle.DEFAULT, TextStyle.NORMAL, 12);
    private BColor color = BColor.BLACK;
    private BColor backgroundColor = new BColor(240, 235, 230);
    private BColor headerBackgroundColor = new BColor(220, 215, 215);
    private BColor borderColor = new BColor(200, 200, 200);
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
