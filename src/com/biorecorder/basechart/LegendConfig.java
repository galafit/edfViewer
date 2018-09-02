package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.graphics.TextStyle;


/**
 * Created by galafit on 18/8/17.
 */
public class LegendConfig {

    private TextStyle textStyle = new TextStyle(TextStyle.DEFAULT, TextStyle.NORMAL, 12);

  //  private int borderWidth = 1;
 //   private BColor borderColor = BColor.LIGHT_GRAY;
    private BColor backgroundColor = BColor.WHITE;
    private Margin itemMargin = new Margin((int)(textStyle.getSize() * 0.2),
            (int)(textStyle.getSize() * 0.2),
            (int)(textStyle.getSize() * 0.2),
            (int)(textStyle.getSize() * 0.2));

    public LegendConfig() {
    }

    public LegendConfig(LegendConfig legendConfig) {
        backgroundColor = legendConfig.backgroundColor;
        textStyle = legendConfig.textStyle;
        itemMargin = legendConfig.itemMargin;
    }

    public TextStyle getTextStyle() {
        return textStyle;
    }

    public void setTextStyle(TextStyle textStyle) {
        this.textStyle = textStyle;
    }

    public BColor getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(BColor backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Margin getItemMargin() {
        return itemMargin;
    }

    public void setItemMargin(Margin itemMargin) {
        this.itemMargin = itemMargin;
    }
}
