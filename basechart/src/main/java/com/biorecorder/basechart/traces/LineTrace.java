package com.biorecorder.basechart.traces;


import com.biorecorder.basechart.*;
import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.graphics.BPath;
import com.biorecorder.basechart.graphics.BRectangle;
import com.biorecorder.basechart.scales.Scale;

/**
 * Created by galafit on 11/10/17.
 */
public class LineTrace implements TracePainter {
    private LineTraceConfig traceConfig;

    public LineTrace() {
        this(new LineTraceConfig());
    }
    
    public LineTrace(LineTraceConfig config) {
        traceConfig = config;
    }

    @Override
    public TraceType traceType() {
        return TraceType.LINE;
    }

    @Override
    public GroupApproximation[] groupApproximations() {
        GroupApproximation[] approximations = {GroupApproximation.OPEN, GroupApproximation.AVERAGE};
        return approximations;
    }

    @Override
    public String curveName(ChartData data, int curve) {
       return data.getColumnName(curve + 1);
    }

    @Override
    public int markWidth() {
        return traceConfig.getMarkSize();
    }

    @Override
    public BRectangle curvePointHoverArea(ChartData data, int dataIndex, int curve, Scale xScale, Scale yScale) {
        int x = (int) xScale.scale(data.value(dataIndex, 0));
        int y = (int) yScale.scale(data.value(dataIndex, curve + 1));
        return new BRectangle(x, y, 0, 0);
    }

    @Override
    public Range curveYMinMax(ChartData data, int curve) {
        return data.columnMinMax(curve + 1);
    }

    @Override
    public Range xMinMax(ChartData data) {
        return data.columnMinMax(0);
    }

    @Override
    public NamedValue[] curvePointValues(ChartData data, int dataIndex, int curve, Scale xScale, Scale yScale) {
        NamedValue[] curveValues = {new NamedValue("",  xScale.formatDomainValue(data.value(dataIndex, 0))),
                new NamedValue("",  yScale.formatDomainValue(data.value(dataIndex, curve + 1)))};
        return curveValues;
    }


    @Override
    public int curveCount(ChartData data) {
        return data.columnCount() - 1;
    }


    private BColor getFillColor(BColor color) {
        return new BColor(color.getRed(), color.getGreen(), color.getBlue(), 110);
    }

    @Override
    public void drawCurve(BCanvas canvas, ChartData data, int curve, BColor curveColor, int curveCount, boolean isSplit, Scale xScale, Scale yScale) {
        XYViewer xyData = new XYViewer(data, curve);
        if (xyData.size() == 0) {
            return;
        }

        BPath path = null;
        canvas.setStroke(traceConfig.getLineStroke());
        BColor lineColor = curveColor;
        BColor markColor = curveColor;
        if(traceConfig.getMode() == LineTraceConfig.LINEAR) {
            path = drawLinearPath(canvas, xyData, xScale, yScale, lineColor, markColor);
        }
        if(traceConfig.getMode() == LineTraceConfig.STEP) {
            path = drawStepPath(canvas, xyData, xScale, yScale, lineColor, markColor);
        }
        if(traceConfig.getMode() == LineTraceConfig.VERTICAL_LINES) {
            path = drawVerticalLinesPath(canvas, xyData, xScale, yScale, lineColor, markColor);
        }

        if(path != null && traceConfig.isFilled()) {
            int x_0 = (int) xScale.scale(xyData.getX(0));
            int x_last = (int) xScale.scale(xyData.getX(xyData.size() - 1));
            path.lineTo(x_last, (int)yScale.getRange()[0]);
            path.lineTo(x_0, (int)yScale.getRange()[0]);
            path.close();
            canvas.setColor(getFillColor(curveColor));
            canvas.fillPath(path);
        }
    }

    private BPath drawLinearPath(BCanvas canvas, XYViewer xyData, Scale xScale, Scale yScale, BColor lineColor, BColor markColor) {
        BPath path = canvas.getEmptyPath();
        int x = (int) xScale.scale(xyData.getX(0));
        int y = (int) yScale.scale(xyData.getY(0));
        path.moveTo(x, y);
        canvas.setColor(markColor);
        int pointRadius = traceConfig.getMarkSize() / 2;
        canvas.fillOval(x - pointRadius, y - pointRadius, 2 * pointRadius,2 * pointRadius);
        for (int i = 1; i < xyData.size(); i++) {
            x = (int) xScale.scale(xyData.getX(i));
            y = (int) yScale.scale(xyData.getY(i));
            path.lineTo(x, y);
            canvas.fillOval(x - pointRadius,y - pointRadius, 2 * pointRadius,2 * pointRadius);
        }
        canvas.setColor(lineColor);
        canvas.drawPath(path);
        return path;
    }

    private BPath drawStepPath(BCanvas canvas, XYViewer xyData, Scale xScale,  Scale yScale, BColor lineColor, BColor markColor) {
        BPath path = canvas.getEmptyPath();
        int x = (int) xScale.scale(xyData.getX(0));
        int y = (int) yScale.scale(xyData.getY(0));
        path.moveTo(x, y);
        canvas.setColor(markColor);
        int pointRadius = traceConfig.getMarkSize()/ 2;
        canvas.fillOval(x - pointRadius, y - pointRadius, 2 * pointRadius,2 * pointRadius);
        for (int i = 1; i < xyData.size(); i++) {
            x = (int) xScale.scale(xyData.getX(i));
            path.lineTo(x, y);
            y = (int) yScale.scale(xyData.getY(i));
            path.lineTo(x, y);
            canvas.fillOval(x - pointRadius,y - pointRadius, 2 * pointRadius,2 * pointRadius);
        }
        canvas.setColor(lineColor);
        canvas.drawPath(path);
        return path;
    }

    private BPath drawVerticalLinesPath(BCanvas canvas, XYViewer xyData, Scale xScale,  Scale yScale, BColor lineColor, BColor markColor) {
        int x = (int) xScale.scale(xyData.getX(0));
        int y = (int) yScale.scale(xyData.getY(0));
        int pointRadius = traceConfig.getMarkSize() / 2;
        canvas.fillOval(x - pointRadius, y - pointRadius, 2 * pointRadius,2 * pointRadius);
        VerticalLine vLine = new VerticalLine(y);
        for (int i = 1; i < xyData.size(); i++) {
            int x_prev = x;
            x = (int) xScale.scale(xyData.getX(i));
            // draw horizontal lines to avoid line breaking
            canvas.setColor(lineColor);
            if(x > x_prev + 1) {
                vLine.setNewBounds(y);
                canvas.drawLine(x_prev, y, x, y);
            }
            y = (int) yScale.scale(xyData.getY(i));
            vLine.setNewBounds(y);
            // draw vertical line
            canvas.drawLine(x, vLine.min, x, vLine.max);
            canvas.setColor(markColor);
            canvas.fillOval(x - pointRadius,y - pointRadius, 2 * pointRadius,2 * pointRadius);
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
