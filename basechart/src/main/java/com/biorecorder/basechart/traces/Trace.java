package com.biorecorder.basechart.traces;

import com.biorecorder.basechart.*;
import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.graphics.BPoint;
import com.biorecorder.basechart.scales.Scale;


/**
 * Created by galafit on 16/9/17.
 */
public abstract class Trace {
    protected Scale xScale;
    protected Scale yScale;
    protected String name;

    boolean isIncreasingChecked = false;
    private ChartData data;
    private int[] sorter;

    public void setData(ChartData data) {
        this.data = data;
        isIncreasingChecked = false;
    }

    public boolean isDataSet() {
        if(data != null) {
            return true;
        }
        return false;
    }

    public void removeData() {
        data = null;
    }

    public void setScales(Scale xScale, Scale yScale) {
        this.xScale = xScale;
        this.yScale = yScale;
    }

    public Scale getXScale() {
        return xScale;
    }

    public Scale getYScale() {
        return yScale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int nearest(int x, int y) {
        // "lazy" sorting solo when tooltips are called
        if (!isIncreasingChecked) {
            if (!data.isColumnIncreasing(0)) {
                sorter = data.sortedIndices(0);
                System.out.println("sort");
            }
            isIncreasingChecked = true;
        }
        double xValue = xScale.invert(x);

        int nearest = data.bisect(0, xValue, sorter);

        if (nearest >= data.rowCount()) {
            nearest = data.rowCount() - 1;
        }

        int nearest_prev = nearest;
        if (nearest > 0){
            nearest_prev = nearest - 1;
        }

        if(sorter != null) {
            nearest = sorter[nearest];
            nearest_prev = sorter[nearest_prev];
        }
        if(Math.abs(data.getValue(nearest_prev, 0) - xValue) < Math.abs(data.getValue(nearest, 0) - xValue)) {
            nearest = nearest_prev;
        }

        return nearest;
    }

    public abstract int getMarkSize();

    public abstract BColor getMainColor();

    public abstract void setMainColor(BColor color);

    public abstract BRange getYExtremes();

    public abstract BPoint getDataPosition(int dataIndex);

    public abstract InfoItem[] getInfo(int dataIndex);

    public abstract void draw(BCanvas canvas);
}
