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
    private int xAxisIndex;
    private int yAxisIndex;
    private String name;

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

    public void setAxes(int xAxisIndex, int yAxisIndex) {
        this.xAxisIndex = xAxisIndex;
        this.yAxisIndex = yAxisIndex;
    }

    public int getXAxisIndex() {
        return xAxisIndex;
    }

    public int getYAxisIndex() {
        return yAxisIndex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int nearest(int x, int y, Scale xScale, Scale yScale) {
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

    public abstract BPoint getDataPosition(int dataIndex, Scale xScale, Scale yScale);

    public abstract InfoItem[] getInfo(int dataIndex, Scale xScale, Scale yScale);

    public abstract void draw(BCanvas canvas, Scale xScale, Scale yScale);
}
