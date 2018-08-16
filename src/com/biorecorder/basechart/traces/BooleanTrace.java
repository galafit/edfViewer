package com.biorecorder.basechart.traces;

import com.biorecorder.basechart.*;
import com.biorecorder.basechart.config.traces.BooleanTraceConfig;
import com.biorecorder.basechart.data.DataSeries;
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
    public BColor getColor() {
        return traceConfig.getColor();
     }


    @Override
    public InfoItem[] getInfo(int dataIndex){
        if (dataIndex == -1){
            return new InfoItem[0];
        }
        InfoItem[] infoItems = new InfoItem[2];
        infoItems[0] = new InfoItem(getName(), "", getColor());
        infoItems[1] = new InfoItem("X: ", String.valueOf(xyData.getX(dataIndex)), null);
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
     /*   if (xyData == null || xyData.size() == 0) {
            return;
        }
        BColor color = getColor();
        BColor resultantColor  = new BColor(color.getRed(), color.getGreen(), color.getBlue(), 110);
        canvas.setColor(resultantColor);
        for (int i = 0; i < xyData.size() - 1; i++) {
           if(xyData.getY(i) > 0) {
               int x1 = (int)getXAxis().scale(xyData.getX(i));
               int x2 = (int)getXAxis().scale(xyData.getX(i + 1));
               int yAxisLength = getYAxis().getStart() - getYAxis().getEnd();
               canvas.fillRect(x1, getYAxis().getEnd(), x2 - x1, yAxisLength);
           }
        }*/
    }
}
