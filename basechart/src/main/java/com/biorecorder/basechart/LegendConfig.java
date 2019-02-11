package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.graphics.HorizontalAlign;
import com.biorecorder.basechart.graphics.TextStyle;
import com.biorecorder.basechart.graphics.VerticalAlign;


/**
 * Created by galafit on 18/8/17.
 */
public class LegendConfig {
    private TextStyle textStyle = new TextStyle(TextStyle.DEFAULT, TextStyle.NORMAL, 12);
    private HorizontalAlign horizontalAlign = HorizontalAlign.LEFT;
    private VerticalAlign verticalAlign = VerticalAlign.TOP;

  //  private int borderWidth = 1;
 //   private BColor borderColor = BColor.LIGHT_GRAY;
    private BColor backgroundColor = BColor.WHITE;
    private Insets itemMargin = new Insets((int)(textStyle.getSize() * 0.2),
            (int)(textStyle.getSize() * 0.2),
            (int)(textStyle.getSize() * 0.2),
            (int)(textStyle.getSize() * 0.2));
    private boolean isAttachedToStacks = true;

    public LegendConfig() {
    }

    public LegendConfig(LegendConfig legendConfig) {
        backgroundColor = legendConfig.backgroundColor;
        textStyle = legendConfig.textStyle;
        itemMargin = legendConfig.itemMargin;
    }

    public boolean isAttachedToStacks() {
        return isAttachedToStacks;
    }

    public void setAttachedToStacks(boolean attachedToStacks) {
        isAttachedToStacks = attachedToStacks;
    }

    public HorizontalAlign getHorizontalAlign() {
        return horizontalAlign;
    }

    public void setHorizontalAlign(HorizontalAlign horizontalAlign) {
        this.horizontalAlign = horizontalAlign;
    }

    public VerticalAlign getVerticalAlign() {
        return verticalAlign;
    }

    public void setVerticalAlign(VerticalAlign verticalAlign) {
        this.verticalAlign = verticalAlign;
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

    public Insets getItemMargin() {
        return itemMargin;
    }

    public void setItemMargin(Insets itemMargin) {
        this.itemMargin = itemMargin;
    }
}
