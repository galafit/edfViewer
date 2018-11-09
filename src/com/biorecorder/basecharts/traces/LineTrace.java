package com.biorecorder.basecharts.traces;


import com.biorecorder.basecharts.*;
import com.biorecorder.data.frame.DataSeries;
import com.biorecorder.basecharts.graphics.BCanvas;
import com.biorecorder.basecharts.graphics.BColor;
import com.biorecorder.basecharts.graphics.BPath;
import com.biorecorder.basecharts.graphics.BPoint;
import com.biorecorder.basecharts.scales.Scale;

/**
 * Created by galafit on 11/10/17.
 */
public class LineTrace extends Trace {
    LineTraceConfig traceConfig;
    XYViewer xyData;

    public LineTrace() {
        traceConfig = new LineTraceConfig();
    }

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
    public InfoItem[] getInfo(int dataIndex, Scale xScale, Scale yScale){
        if (dataIndex == -1){
            return new InfoItem[0];
        }
        InfoItem[] infoItems = new InfoItem[3];
        infoItems[0] = new InfoItem(getName(), "", getMainColor());
       // infoItems[1] = new InfoItem("X: ", String.valueOf(xyData.getX(dataIndex)), null);
       // infoItems[2] = new InfoItem("Y: ", String.valueOf(xyData.getY(dataIndex)), null);
        infoItems[1] = new InfoItem("X: ", xScale.formatDomainValue(xyData.getX(dataIndex)), null);
        infoItems[2] = new InfoItem("Y: ", yScale.formatDomainValue(xyData.getY(dataIndex)), null);

        return infoItems;
    }

    @Override
    public Range getYExtremes() {
        return xyData.getYExtremes();
    }


    @Override
    public BPoint getDataPosition(int dataIndex, Scale xScale, Scale yScale) {
        return new BPoint((int)xScale.scale(xyData.getX(dataIndex)), (int)yScale.scale(xyData.getY(dataIndex)));
    }

    @Override
    public void draw(BCanvas canvas, Scale xScale, Scale yScale) {
        if (xyData == null || xyData.size() == 0) {
            return;
        }

        BPath path = null;
        if(traceConfig.getMode() == LineTraceConfig.LINEAR) {
            path = drawLinearPath(canvas, xScale, yScale);
        }
        if(traceConfig.getMode() == LineTraceConfig.STEP) {
            path = drawStepPath(canvas, xScale, yScale);
        }
        if(traceConfig.getMode() == LineTraceConfig.VERTICAL_LINES) {
            path = drawVerticalLinesPath(canvas, xScale, yScale);
        }

        if(path != null && traceConfig.isFilled()) {
            int x_0 = (int) xScale.scale(xyData.getX(0));
            int x_last = (int) xScale.scale(xyData.getX(xyData.size() - 1));
            path.lineTo(x_last, (int)yScale.getRange()[0]);
            path.lineTo(x_0, (int)yScale.getRange()[0]);
            path.close();
            canvas.setColor(getFillColor());
            canvas.fillPath(path);
        }
    }

    private BPath drawLinearPath(BCanvas canvas, Scale xScale, Scale yScale) {
        BPath path = canvas.getEmptyPath();
        int x = (int) xScale.scale(xyData.getX(0));
        int y = (int) yScale.scale(xyData.getY(0));
        path.moveTo(x, y);
        canvas.setColor(getMarkColor());
        int pointRadius = traceConfig.getMarkSize() / 2;
        canvas.drawOval(x - pointRadius, y - pointRadius, 2 * pointRadius,2 * pointRadius);
        for (int i = 1; i < xyData.size(); i++) {
            x = (int) xScale.scale(xyData.getX(i));
            y = (int) yScale.scale(xyData.getY(i));
            path.lineTo(x, y);
            canvas.drawOval(x - pointRadius,y - pointRadius, 2 * pointRadius,2 * pointRadius);
        }
        canvas.setColor(getLineColor());
        canvas.setStroke(traceConfig.getLineStroke());
        canvas.drawPath(path);
        return path;
    }

    private BPath drawStepPath(BCanvas canvas, Scale xScale, Scale yScale) {
        BPath path = canvas.getEmptyPath();
        int x = (int) xScale.scale(xyData.getX(0));
        int y = (int) yScale.scale(xyData.getY(0));
        path.moveTo(x, y);
        canvas.setColor(getMarkColor());
        int pointRadius = traceConfig.getMarkSize()/ 2;
        canvas.drawOval(x - pointRadius, y - pointRadius, 2 * pointRadius,2 * pointRadius);
        for (int i = 1; i < xyData.size(); i++) {
            x = (int) xScale.scale(xyData.getX(i));
            path.lineTo(x, y);
            y = (int) yScale.scale(xyData.getY(i));
            path.lineTo(x, y);
            canvas.drawOval(x - pointRadius,y - pointRadius, 2 * pointRadius,2 * pointRadius);
        }
        canvas.setColor(getLineColor());
        canvas.setStroke(traceConfig.getLineStroke());
        canvas.drawPath(path);
        return path;
    }

    private BPath drawVerticalLinesPath(BCanvas canvas, Scale xScale, Scale yScale) {
        int x = (int) xScale.scale(xyData.getX(0));
        int y = (int) yScale.scale(xyData.getY(0));
        canvas.setColor(getMarkColor());
        int pointRadius = traceConfig.getMarkSize() / 2;
        canvas.drawOval(x - pointRadius, y - pointRadius, 2 * pointRadius,2 * pointRadius);
        VerticalLine vLine = new VerticalLine(y);
        for (int i = 1; i < xyData.size(); i++) {
            int x_prev = x;
            x = (int) xScale.scale(xyData.getX(i));
            // draw horizontal lines to avoid line breaking
            if(x > x_prev + 1) {
                vLine.setNewBounds(y);
                canvas.drawLine(x_prev, y, x, y);
            }
            y = (int) yScale.scale(xyData.getY(i));
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
