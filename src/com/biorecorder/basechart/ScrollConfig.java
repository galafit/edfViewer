package com.biorecorder.basechart;


import com.biorecorder.basechart.graphics.BColor;

/**
 * Created by galafit on 1/10/17.
 */
public class ScrollConfig {
    private BColor scrollColor = BColor.RED;
    private int scrollMinWidth = 10; //px

    public ScrollConfig() {
    }

    public ScrollConfig(ScrollConfig scrollConfig) {
        scrollColor = scrollConfig.scrollColor;
        scrollMinWidth = scrollConfig.scrollMinWidth;
    }

    public void setScrollColor(BColor scrollColor) {
        this.scrollColor = scrollColor;
    }

    public void setScrollMinWidth(int scrollMinWidth) {
        this.scrollMinWidth = scrollMinWidth;
    }

    public BColor getScrollColor() {
        return scrollColor;
    }

    public int getScrollMinWidth() {
        return scrollMinWidth;
    }

}
