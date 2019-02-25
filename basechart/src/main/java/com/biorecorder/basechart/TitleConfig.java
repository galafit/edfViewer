package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.BColor;
import com.sun.istack.internal.Nullable;

/**
 * Created by galafit on 31/8/18.
 */
public class TitleConfig {
    private TextStyle textStyle = new TextStyle(TextStyle.DEFAULT, TextStyle.NORMAL, 14);
    private BColor textColor = BColor.BLACK_LIGHT;
    private Insets margin;

    public TitleConfig() {
    }

    public TitleConfig(TitleConfig config) {
        textStyle = config.textStyle;
        textColor = config.textColor;
        margin = config.margin;
    }

    public TextStyle getTextStyle() {
        return textStyle;
    }

    public void setTextStyle(TextStyle textStyle) {
        this.textStyle = textStyle;
    }

    public BColor getTextColor() {
        return textColor;
    }

    public void setTextColor(BColor textColor) {
        this.textColor = textColor;
    }

    public Insets getMargin() {
        if(margin != null) {
            return margin;
        }
        return new Insets(textStyle.getSize() / 2);
    }

    public void setMargin(@Nullable Insets margin) {
        this.margin = margin;
    }
}
