package com.biorecorder.basechart.config;

import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.Margin;
import com.biorecorder.basechart.graphics.TextStyle;


/**
 * Created by galafit on 18/8/17.
 */
public class LegendConfig {
    public static final int TOP_LEFT = 0;
    public static final int TOP_RIGHT = 1;

    private boolean isVisible = true;
    private TextStyle textStyle = new TextStyle(TextStyle.DEFAULT, TextStyle.NORMAL, 12);
    private int position = TOP_LEFT;

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

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        isVisible = isVisible;
    }

    public TextStyle getTextStyle() {
        return textStyle;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
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
