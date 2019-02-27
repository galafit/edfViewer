package com.biorecorder.basechart.axis;

import com.biorecorder.basechart.Range;
import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BRectangle;
import com.biorecorder.basechart.scales.Scale;

/**
 * This class do axis rounding and add isVisible property.
 * <p>
 * Implement axis rounding when methods:
 * drawAxis or drawGrid or getWidth is invoked !!!
 */
public class AxisWrapper {
    private Axis axis;
    private boolean isVisible = false;
    private boolean isRoundingEnabled = false;
    // need this field to implement smooth zooming and translate when minMaxRounding enabled
    private Range rowMinMax; // without rounding
    private boolean roundingDirty = true;


    public AxisWrapper(Axis axis) {
        this.axis = axis;
        rowMinMax = new Range(axis.getMin(), axis.getMax());
    }

    private void setRoundingDirty() {
        roundingDirty = true;
        axis.setMinMax(rowMinMax);
    }

    public void setRoundingAccuracyPct(int roundingAccuracyPct) {
        axis.setRoundingAccuracyPct(roundingAccuracyPct);
        setRoundingDirty();
    }

    private boolean isDirty() {
        if (isRoundingEnabled && roundingDirty) {
            return true;
        }
        return false;
    }

    public double length() {
        return axis.length();
    }

    public void setRoundingEnabled(boolean isRoundingEnabled) {
        if(this.isRoundingEnabled != isRoundingEnabled) {
            this.isRoundingEnabled = isRoundingEnabled;
            setRoundingDirty();
        }
    }

    public boolean isRoundingEnabled() {
        return isRoundingEnabled;
    }

    public void setScale(Scale scale) {
        axis.setScale(scale);
    }

    public double scale(double value) {
        return axis.scale(value);
    }

    public double invert(float value) {
        return axis.invert(value);
    }

    public String formatValue(double value) {
        return axis.formatDomainValue(value);
    }

    public boolean isTickLabelOutside() {
        return axis.isTickLabelOutside();
    }

    public void setTitle(String title) {
        axis.setTitle(title);
    }

    public void setTickInterval(double tickInterval) {
        axis.setTickInterval(tickInterval);
        setRoundingDirty();
    }

    public void setMinorTickIntervalCount(int minorTickIntervalCount) {
        axis.setMinorTickIntervalCount(minorTickIntervalCount);
    }

    public void setTickFormatInfo(TickFormatInfo tickFormatInfo) {
        axis.setTickFormatInfo(tickFormatInfo);
        setRoundingDirty();
    }

    public Scale getScale() {
        return axis.getScale();
    }


    public void setConfig(AxisConfig config) {
        axis.setConfig(config);
        setRoundingDirty();
    }


    public Scale zoom(double zoomFactor) {
        // to have smooth zooming we do it on row domain values instead of rounded ones !!!
        setRoundingDirty();
        return axis.zoom(zoomFactor);
    }


    public Scale translate(int translation) {
        // to have smooth translating we do it on row domain values instead of rounded ones !!!
        setRoundingDirty();
        Scale scale = axis.translate(translation);
        return scale;
    }

    /**
     * return true if axis min or max actually will be changed
     */
    public boolean setMinMax(double min, double max) {
        if (rowMinMax.getMin() != min || rowMinMax.getMax() != max) {
            rowMinMax = new Range(min, max);
            setRoundingDirty();
            return true;
        }
        return false;
    }

    /**
     * return true if axis start or end actually changed
     */
    public boolean setStartEnd(double start, double end) {
        if (axis.getStart() != start || axis.getEnd() != end) {
            setRoundingDirty();
            axis.setStartEnd(start, end);
            return true;
        }
        return false;
    }

    public double getMin() {
        return axis.getMin();
    }

    public double getMax() {
        return axis.getMax();
    }

    public double getStart() {
        return axis.getStart();
    }

    public double getEnd() {
        return axis.getEnd();
    }

    public boolean isVisible() {
        return isVisible;
    }

    /**
     * this method DO AXIS ROUNDING
     */
    public int getWidth(BCanvas canvas) {
        if (isVisible) {
            if (isDirty()) {
                axis.roundMinMax(canvas);
                roundingDirty = false;
            }
            return axis.getWidth(canvas);
        }
        return 0;
    }

    /**
     * this method DO AXIS ROUNDING
     */
    public void drawGrid(BCanvas canvas, BRectangle area) {
        if (isDirty()) {
            axis.roundMinMax(canvas);
            roundingDirty = false;
        }
        axis.drawGrid(canvas, area);
    }

    /**
     * this method DO AXIS ROUNDING
     */
    public void drawAxis(BCanvas canvas, BRectangle area) {
        if(isVisible) {
            if (isDirty()) {
                axis.roundMinMax(canvas);
                roundingDirty = false;
            }
            axis.drawAxis(canvas, area);
        }
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

}