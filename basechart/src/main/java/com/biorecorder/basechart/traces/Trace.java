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
    private ChartData data;

    public void setData(ChartData data) {
        this.data = data;
    }

    public ChartData getData() {
        return data;
    }

    public int getXAxisIndex() {
        return xAxisIndex;
    }

    public void setXAxisIndex(int xAxisIndex) {
        this.xAxisIndex = xAxisIndex;
    }

    public int getYAxisIndex() {
        return yAxisIndex;
    }

    public void setYAxisIndex(int yAxisIndex) {
        this.yAxisIndex = yAxisIndex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BRange getXRange() {
        return data.getColumnRange(0);
    }

    public long nearest(int x, int y, Scale xScale, Scale yScale) {
        return data.nearest(0, xScale.invert(x));

    }

    public abstract int getMarkSize();

    public abstract BColor getMainColor();

    public abstract void setMainColor(BColor color);

    public abstract BRange getYExtremes();

    public abstract BPoint getDataPosition(int dataIndex, Scale xScale, Scale yScale);

    public abstract InfoItem[] getInfo(int dataIndex, Scale xScale, Scale yScale);

    public abstract void draw(BCanvas canvas, Scale xScale, Scale yScale);
}
