package com.biorecorder.basechart.config;

import com.biorecorder.basechart.BColor;
import com.biorecorder.basechart.Margin;
import com.biorecorder.basechart.TextStyle;


/**
 * Created by galafit on 19/8/17.
 */
public class TooltipConfig {
    private TextStyle textStyle = new TextStyle(TextStyle.DEFAULT, TextStyle.NORMAL, 12);
    private BColor color = BColor.BLACK;
    private BColor backgroundColor = new BColor(240, 235, 230);
    private BColor borderColor = new BColor(200, 200, 200);
    private int borderWidth = 1;
    private Margin margin = new Margin((int)(getTextStyle().getSize() * 0.4),
            (int)(getTextStyle().getSize() * 0.8),
            (int)(getTextStyle().getSize() * 0.4),
            (int)(getTextStyle().getSize() * 0.8));

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

    public Margin getMargin() {
        return margin;
    }

    public void setMargin(Margin margin) {
        this.margin = margin;
    }
}
