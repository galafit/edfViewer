package com.biorecorder.basechart;


import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.graphics.BPath;
import com.biorecorder.basechart.scales.Scale;

/**
 * Created by galafit on 11/10/17.
 */
public class LineTrace extends Trace {
    private LineTraceConfig traceConfig;

    public LineTrace(ChartData data) {
        this(data, new LineTraceConfig());
    }

    public LineTrace(ChartData data, LineTraceConfig config) {
        super(data);
        traceConfig = config;
        for (int i = 0; i < curveCount; i++) {
            curveNames[i] = data.getColumnName(i + 1);
        }
        for (int i = 0; i < curveColors.length; i++) {
            curveColors[i] = traceConfig.getColor();
        }
    }

    @Override
    public int getMarkSize() {
        return traceConfig.getMarkSize();
    }

    @Override
    protected int curveYPosition(int curveNumber, int dataIndex, ChartData data) {
        return (int) getYScale(curveNumber).scale(data.value(dataIndex, curveNumber + 1));
    }

    @Override
    protected int curveCount(ChartData data) {
        return data.columnCount() - 1;
    }

    @Override
    protected NamedValue[] curveValues(int curveNumber, int dataIndex, ChartData data) {
        XYViewer xyData = new XYViewer(data, curveNumber);
        Scale yScale = getYScale(curveNumber);
        double yValue = xyData.getY(dataIndex);
        NamedValue[] curveValues = {new NamedValue("", yValue, yScale.formatDomainValue(yValue))};
        return curveValues;
    }

    @Override
    protected Range curveYMinMax(int curveNumber, ChartData data) {
        return data.columnMinMax(curveNumber + 1);
    }

    private BColor getLineColor(int curveNumber) {
        return getCurveColor(curveNumber);
    }

    private BColor getMarkColor(int curveNumber) {
        return getCurveColor(curveNumber);
    }

    private BColor getFillColor(int curveNumber) {
        return new BColor(getCurveColor(curveNumber).getRed(), getCurveColor(curveNumber).getGreen(), getCurveColor(curveNumber).getBlue(), 110);
    }

    @Override
    protected void draw(BCanvas canvas, ChartData data) {
        for (int i = 0; i < curveCount(); i++) {
            drawCurve(canvas, i, data);
        }
    }

    private void drawCurve(BCanvas canvas, int curveNumber, ChartData data) {
        XYViewer xyData = new XYViewer(data, curveNumber);
        if (xyData.size() == 0) {
            return;
        }
        Scale yScale = getYScale(curveNumber);

        BPath path = null;
        canvas.setStroke(traceConfig.getLineStroke());
        BColor lineColor = getLineColor(curveNumber);
        BColor markColor = getMarkColor(curveNumber);
        if(traceConfig.getMode() == LineTraceConfig.LINEAR) {
            path = drawLinearPath(canvas, xyData, yScale, lineColor, markColor);
        }
        if(traceConfig.getMode() == LineTraceConfig.STEP) {
            path = drawStepPath(canvas, xyData, yScale, lineColor, markColor);
        }
        if(traceConfig.getMode() == LineTraceConfig.VERTICAL_LINES) {
            path = drawVerticalLinesPath(canvas, xyData, yScale, lineColor, markColor);
        }


        if(path != null && traceConfig.isFilled()) {
            int x_0 = (int) xScale.scale(xyData.getX(0));
            int x_last = (int) xScale.scale(xyData.getX(xyData.size() - 1));
            path.lineTo(x_last, (int)yScale.getRange()[0]);
            path.lineTo(x_0, (int)yScale.getRange()[0]);
            path.close();
            canvas.setColor(getFillColor(curveNumber));
            canvas.fillPath(path);
        }
    }

    private BPath drawLinearPath(BCanvas canvas, XYViewer xyData, Scale yScale, BColor lineColor, BColor markColor) {
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

    private BPath drawStepPath(BCanvas canvas, XYViewer xyData, Scale yScale, BColor lineColor, BColor markColor) {
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

    private BPath drawVerticalLinesPath(BCanvas canvas, XYViewer xyData, Scale yScale, BColor lineColor, BColor markColor) {
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
