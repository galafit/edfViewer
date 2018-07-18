package com.biorecorder.basechart.chart;

import com.biorecorder.basechart.chart.config.ScrollConfig;
import com.biorecorder.basechart.chart.scales.Scale;

import java.util.*;
import java.util.List;


/**
 * Created by galafit on 21/7/17.
 */
public class Scroll {
    private Scale scale;
    private ScrollConfig scrollConfig;
    private double value;
    private double extent;
    private List<ScrollListener> eventListeners = new ArrayList<ScrollListener>();


    public Scroll(double scrollExtent, ScrollConfig scrollConfig, Scale scale) {
        this.scale = scale;
        this.scrollConfig = scrollConfig;
        setExtent(scrollExtent);
        value = getMin();
    }

    public void addListener(ScrollListener listener) {
        eventListeners.add(listener);
    }

    private void fireListeners() {
        for (ScrollListener listener : eventListeners) {
            listener.onScrollChanged(value, extent);
        }
    }

    public void setExtent(double scrollExtent) {
        if (scrollExtent > getMax() - getMin() || scrollExtent <= 0) {
            scrollExtent = getMax() - getMin();
        }
        if(this.extent != scrollExtent) {
            this.extent = scrollExtent;
            checkBounds();
            fireListeners();
        }
    }


    private Range getScrollRange() {
        double scrollStart = scale.scale(value);
        double scrollEnd = scale.scale(value + extent);
        int scrollWidth = Math.max(scrollConfig.getScrollMinWidth(), (int) (scrollEnd - scrollStart));
        if (scrollStart + scrollConfig.getScrollMinWidth() > getEnd()) { // prevent that actually thin scroll moves outside screen
            scrollStart = getEnd() - scrollConfig.getScrollMinWidth();
        }
        return new Range(scrollStart, scrollStart + scrollWidth);
    }

    public double getExtent() {
        return extent;
    }

    public float getPosition() {
        return scale.scale(value);
    }

    /**
     * @return true if value was changed and false if newValue = current scroll value
     */
    public boolean setPosition(float x) {
        double value = scale.invert(x);
        return setValue(value);
    }

    public double getValue() {
        return value;
    }

    public float getWidth() {
        float scrollStart = scale.scale(value);
        float scrollEnd = scale.scale(value + extent);
        return scrollEnd - scrollStart;
    }


    /**
     * @return true if value was changed and false if newValue = current scroll value
     */
    public boolean setValue(double newValue) {
        double oldValue = value;
        value = newValue;
        checkBounds();
        if (value != oldValue) {
            fireListeners();
            return true;
        }
        return false;
    }

    private double getMin() {
        return scale.getDomain()[0];
    }

    private double getMax() {
        return scale.getDomain()[scale.getDomain().length - 1];
    }

    private float getStart() {
        return scale.getRange()[0];
    }

    private float getEnd() {
        return scale.getRange()[scale.getRange().length - 1];
    }

    private void checkBounds() {
        if (value + extent > getMax()) {
            value = getMax() - extent;
        }
        if (value < getMin()) {
            value = getMin();
        }
    }


    public boolean isPointInsideScroll(int x) {
        return getScrollRange().contains(x);
    }

    public void draw(BCanvas canvas, BRectangle area) {
        Range scrollRange = getScrollRange();
        double scrollMin = scrollRange.getMin();

        BColor scrollColor = scrollConfig.getScrollColor();
        BColor fillColor = new BColor(scrollColor.getRed(), scrollColor.getGreen(), scrollColor.getBlue(), 70);
        canvas.setColor(fillColor);
        canvas.fillRect((int) scrollMin, area.y + 1, (int) scrollRange.length(), area.height - 2);

        BColor borderColor = new BColor(scrollColor.getRed(), scrollColor.getGreen(), scrollColor.getBlue(), 130);
        canvas.setColor(borderColor);
        canvas.setStroke(new BStroke(1));
        canvas.drawRect((int) scrollMin, area.y + 1, (int) scrollRange.length() - 1, area.height - 2);
    }



}
