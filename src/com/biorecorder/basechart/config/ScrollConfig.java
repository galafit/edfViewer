package com.biorecorder.basechart.config;


import com.biorecorder.basechart.BColor;

/**
 * Created by galafit on 1/10/17.
 */
public class ScrollConfig {
    private BColor scrollColor = BColor.RED;
    private int scrollMinWidth = 10; //px

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
