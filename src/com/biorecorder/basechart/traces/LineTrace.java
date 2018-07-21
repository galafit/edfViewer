package com.biorecorder.basechart.traces;


import com.biorecorder.basechart.*;
import com.biorecorder.basechart.config.traces.LineTraceConfig;
import com.biorecorder.basechart.data.DataSeries;

/**
 * Created by galafit on 11/10/17.
 */
public class LineTrace extends Trace {
    LineTraceConfig traceConfig;
    XYViewer xyData;

    public LineTrace(LineTraceConfig traceConfig) {
        this.traceConfig = traceConfig;
    }

    @Override
    public void setData(DataSeries dataSeries) {
        super.setData(dataSeries);
        xyData = new XYViewer();
        xyData.setData(dataSeries);
    }

    @Override
    public BColor getColor() {
        return traceConfig.getColor();
    }

    @Override
    public int getMarkSize() {
        return traceConfig.getMarkSize();
    }

    BColor getLineColor() {
        return traceConfig.getColor();
    }

    BColor getMarkColor() {
        return traceConfig.getColor();
    }

    public BColor getFillColor() {
        return new BColor(getLineColor().getRed(), getLineColor().getGreen(), getLineColor().getBlue(), 110);
    }

    @Override
    public InfoItem[] getInfo(int dataIndex){
        if (dataIndex == -1){
            return new InfoItem[0];
        }
        InfoItem[] infoItems = new InfoItem[3];
        infoItems[0] = new InfoItem(getName(), "", getColor());
        //infoItems[1] = new InfoItem("X: ", String.valueOf(xyData.getxValue(dataIndex)), null);
        //infoItems[2] = new InfoItem("Y: ", String.valueOf(xyData.getyValues(dataIndex)), null);
        infoItems[1] = new InfoItem("X: ", getXAxis().formatDomainValue(xyData.getX(dataIndex)), null);
        infoItems[2] = new InfoItem("Y: ", getYAxis().formatDomainValue(xyData.getY(dataIndex)), null);

        return infoItems;
    }

    @Override
    public Range getYExtremes() {
        return xyData.getYExtremes();
    }


    @Override
    public BPoint getDataPosition(int dataIndex) {
        return new BPoint((int)getXAxis().scale(xyData.getX(dataIndex)), (int)getYAxis().scale(xyData.getY(dataIndex)));
    }

    @Override
    public void draw(BCanvas canvas) {
        if (xyData == null || xyData.size() == 0) {
            return;
        }

        BPath path = null;
        if(traceConfig.getMode() == LineTraceConfig.LINEAR) {
            path = drawLinearPath(canvas);
        }
        if(traceConfig.getMode() == LineTraceConfig.STEP) {
            path = drawStepPath(canvas);
        }
        if(traceConfig.getMode() == LineTraceConfig.VERTICAL_LINES) {
            path = drawVerticalLinesPath(canvas);
        }

        if(path != null && traceConfig.isFilled()) {
            int x_0 = (int) getXAxis().scale(xyData.getX(0));
            int x_last = (int) getXAxis().scale(xyData.getX(xyData.size() - 1));
            path.lineTo(x_last, getYAxis().getStart());
            path.lineTo(x_0, getYAxis().getStart());
            path.close();
            canvas.setColor(getFillColor());
            canvas.fillPath(path);
        }
    }

    private BPath drawLinearPath(BCanvas canvas) {
        BPath path = new BPath();
        int x = (int) getXAxis().scale(xyData.getX(0));
        int y = (int) getYAxis().scale(xyData.getY(0));
        path.moveTo(x, y);
        canvas.setColor(getMarkColor());
        int pointRadius = traceConfig.getMarkSize() / 2;
        canvas.drawOval(x - pointRadius, y - pointRadius, 2 * pointRadius,2 * pointRadius);
        for (int i = 1; i < xyData.size(); i++) {
            x = (int) getXAxis().scale(xyData.getX(i));
            y = (int) getYAxis().scale(xyData.getY(i));
            path.lineTo(x, y);
            canvas.drawOval(x - pointRadius,y - pointRadius, 2 * pointRadius,2 * pointRadius);
        }
        canvas.setColor(getLineColor());
        canvas.setStroke(traceConfig.getLineStroke());
        canvas.drawPath(path);
        return path;
    }

    private BPath drawStepPath(BCanvas canvas) {
        BPath path = new BPath();
        int x = (int) getXAxis().scale(xyData.getX(0));
        int y = (int) getYAxis().scale(xyData.getY(0));
        path.moveTo(x, y);
        canvas.setColor(getMarkColor());
        int pointRadius = traceConfig.getMarkSize()/ 2;
        canvas.drawOval(x - pointRadius, y - pointRadius, 2 * pointRadius,2 * pointRadius);
        for (int i = 1; i < xyData.size(); i++) {
            x = (int) getXAxis().scale(xyData.getX(i));
            path.lineTo(x, y);
            y = (int) getYAxis().scale(xyData.getY(i));
            path.lineTo(x, y);
            canvas.drawOval(x - pointRadius,y - pointRadius, 2 * pointRadius,2 * pointRadius);
        }
        canvas.setColor(getLineColor());
        canvas.setStroke(traceConfig.getLineStroke());
        canvas.drawPath(path);
        return path;
    }

    private BPath drawVerticalLinesPath(BCanvas canvas) {
        int x = (int) getXAxis().scale(xyData.getX(0));
        int y = (int) getYAxis().scale(xyData.getY(0));
        canvas.setColor(getMarkColor());
        int pointRadius = traceConfig.getMarkSize() / 2;
        canvas.drawOval(x - pointRadius, y - pointRadius, 2 * pointRadius,2 * pointRadius);
        VerticalLine vLine = new VerticalLine(y);
        for (int i = 1; i < xyData.size(); i++) {
            int x_prev = x;
            x = (int) getXAxis().scale(xyData.getX(i));
            // draw horizontal lines to avoid line breaking
            if(x > x_prev + 1) {
                vLine.setNewBounds(y);
                canvas.drawLine(x_prev, y, x, y);
            }
            y = (int) getYAxis().scale(xyData.getY(i));
            vLine.setNewBounds(y);
            // draw vertical line
            canvas.drawLine(x, vLine.min, x, vLine.max);
            canvas.drawOval(x - pointRadius,y - pointRadius, 2 * pointRadius,2 * pointRadius);
        }
        return null;
    }

    class VerticalLine {
        int max;
        int min;

        public VerticalLine(int y) {
            min = y;
            max = y;
        }

        void setNewBounds(int y) {
            if (y >= min && y <= max) {
                min = max = y;
            } else if (y > max) {
                min = max + 1;
                max = y;
            } else if (y < min) {
                max = min - 1;
                min = y;
            }
        }
    }
}
