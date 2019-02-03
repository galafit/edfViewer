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
    private ChartData sortedData;

    public void setData(ChartData data) {
        this.data = data;
        sortedData = null;
    }

    public boolean isDataSet() {
        if(data != null) {
            return true;
        }
        return false;
    }

    public void removeData() {
        data = null;
        sortedData = null;
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

    public BRange getXRange() {
        return data.getColumnMinMax(0);
    }

    public long nearest(int x, int y, Scale xScale, Scale yScale) {
        if(sortedData == null) {
            if(data.isColumnIncreasing(0)) {
                sortedData = data;
            } else {
                sortedData = data.sort(0);
            }
        }

        return sortedData.bisect(0, xScale.invert(x));
    }

    public abstract int getMarkSize();

    public abstract BColor getMainColor();

    public abstract void setMainColor(BColor color);

    public abstract BRange getYExtremes();

    public abstract BPoint getDataPosition(int dataIndex, Scale xScale, Scale yScale);

    public abstract InfoItem[] getInfo(int dataIndex, Scale xScale, Scale yScale);

    public abstract void draw(BCanvas canvas, Scale xScale, Scale yScale);
}
