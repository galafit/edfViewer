package com.biorecorder.basechart.traces;

import com.biorecorder.basechart.*;
import com.biorecorder.data.DataSeries;
import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.graphics.BPoint;
import com.biorecorder.basechart.scales.Scale;

/**
 * Created by galafit on 28/1/18.
 */
public class BooleanTrace extends Trace {
    BooleanTraceConfig traceConfig;
    XYViewer xyData;

    public BooleanTrace(BooleanTraceConfig traceConfig) {
        this.traceConfig = traceConfig;
    }

    @Override
    public void setData(DataSeries dataSet) {
        super.setData(dataSet);
        xyData = new XYViewer();
        xyData.setData(dataSet);
    }

    @Override
    public BColor getMainColor() {
        return traceConfig.getColor();
     }

    @Override
    public int getMarkSize() {
        return traceConfig.getMarkSize();
    }

    @Override
    public void setMainColor(BColor color) {
        traceConfig.setColor(color);
    }


    @Override
    public InfoItem[] getInfo(int dataIndex, Scale xScale, Scale yScale){
        if (dataIndex == -1){
            return new InfoItem[0];
        }
        InfoItem[] infoItems = new InfoItem[2];
        infoItems[0] = new InfoItem(getName(), "", getMainColor());
        infoItems[1] = new InfoItem("X: ", xScale.formatDomainValue(xyData.getX(dataIndex)), null);
        return infoItems;
    }

    @Override
    public Range getYExtremes() {
        return null;
    }


    @Override
    public BPoint getDataPosition(int dataIndex, Scale xScale, Scale yScale) {
        if(xyData.getY(dataIndex) > 0) {
            return new BPoint((int)xScale.scale(xyData.getX(dataIndex)), (int)yScale.getRange()[1]);
        }
        return new BPoint((int)yScale.scale(xyData.getX(dataIndex)), (int)yScale.getRange()[1]);
    }

    @Override
    public void draw(BCanvas canvas, Scale xScale, Scale yScale) {
        double[] yRange = yScale.getRange();
        int yStart = (int)yRange[0];
        int yEnd = (int) yRange[yRange.length - 1];
        if (xyData == null || xyData.size() == 0) {
            return;
        }
        BColor color = getMainColor();
        BColor resultantColor  = new BColor(color.getRed(), color.getGreen(), color.getBlue(), 110);
        canvas.setColor(resultantColor);
        for (int i = 0; i < xyData.size() - 1; i++) {
            if(xyData.getY(i) > 0) {
                int x1 = (int)xScale.scale(xyData.getX(i));
                int x2 = (int)xScale.scale(xyData.getX(i + 1));
                int yAxisLength = Math.abs(yStart - yEnd);
                canvas.fillRect(x1, yEnd, x2 - x1, yAxisLength);
            }
        }
    }
}
