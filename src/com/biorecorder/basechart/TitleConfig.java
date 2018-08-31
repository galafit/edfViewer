package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.graphics.TextStyle;

/**
 * Created by galafit on 31/8/18.
 */
public class TitleConfig {
    private TextStyle textStyle = new TextStyle(TextStyle.DEFAULT, TextStyle.BOLD, 14);
    private BColor textColor = BColor.BLACK;
    private Margin margin = new Margin(0, (int)(textStyle.getSize() * 0.5),
            0, (int)(textStyle.getSize() * 0.5));
   /* new Margin((int)(textStyle.getSize() * 0),
                (int)(textStyle.getSize() * 0.5),
                (int)(textStyle.getSize() * 0.5),
                (int)(textStyle.getSize() * 0.5));*/

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

    public Margin getMargin() {
        return margin;
    }

    public void setMargin(Margin margin) {
        this.margin = margin;
    }
}
