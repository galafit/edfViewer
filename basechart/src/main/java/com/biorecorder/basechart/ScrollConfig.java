package com.biorecorder.basechart;


import com.biorecorder.basechart.graphics.BColor;

/**
 * Created by galafit on 1/10/17.
 */
public class ScrollConfig {
    private BColor color = BColor.RED;
    private int activeExtraSpace = 5; //px
    private int borderWidth = 2; // px

    public ScrollConfig() {
    }

    public ScrollConfig(ScrollConfig scrollConfig) {
        color = scrollConfig.color;
        activeExtraSpace = scrollConfig.activeExtraSpace;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }

    public void setColor(BColor color) {
        this.color = color;
    }

    public void setActiveExtraSpace(int activeExtraSpace) {
        this.activeExtraSpace = activeExtraSpace;
    }

    public BColor getColor() {
        return color;
    }

    public int getActiveExtraSpace() {
        return activeExtraSpace;
    }

}
