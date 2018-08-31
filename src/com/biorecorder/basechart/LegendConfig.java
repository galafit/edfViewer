package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.Margin;
import com.biorecorder.basechart.graphics.TextStyle;


/**
 * Created by galafit on 18/8/17.
 */
public class LegendConfig {

    private TextStyle textStyle = new TextStyle(TextStyle.DEFAULT, TextStyle.NORMAL, 12);

  //  private int borderWidth = 1;
 //   private BColor borderColor = BColor.LIGHT_GRAY;
    private BColor backgroundColor = BColor.WHITE;
    private BColor textColor = BColor.BLACK;
    private Margin margin = new Margin((int)(getTextStyle().getSize() * 0),
            (int)(getTextStyle().getSize() * 1),
            (int)(getTextStyle().getSize() * 0.5),
            (int)(getTextStyle().getSize() * 1));

    public BColor getTextColor() {
        return textColor;
    }

    public void setTextColor(BColor textColor) {
        this.textColor = textColor;
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

    public Margin getMargin() {
        return margin;
    }

    public void setMargin(Margin margin) {
        this.margin = margin;
    }
}
