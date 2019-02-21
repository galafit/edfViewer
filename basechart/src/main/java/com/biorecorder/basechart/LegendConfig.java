package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.graphics.HorizontalAlign;
import com.biorecorder.basechart.graphics.TextStyle;
import com.biorecorder.basechart.graphics.VerticalAlign;
import com.sun.istack.internal.Nullable;


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
    private Insets padding;
    private Insets margin;
    private boolean isAttachedToStacks = true;

    public LegendConfig() {
    }

    public LegendConfig(LegendConfig legendConfig) {
        backgroundColor = legendConfig.backgroundColor;
        textStyle = legendConfig.textStyle;
        padding = legendConfig.padding;
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

    public Insets getPadding() {
        if(padding != null) {
            return padding;
        }

        return new Insets(textStyle.getSize()/5);
    }

    public Insets getMargin() {
        if(margin != null) {
            return margin;
        }
        return new Insets(textStyle.getSize() / 3);

    }

    public void setMargin(Insets margin) {
        this.margin = margin;
    }

    public void setPadding(@Nullable Insets padding) {
        this.padding = padding;
    }
}
