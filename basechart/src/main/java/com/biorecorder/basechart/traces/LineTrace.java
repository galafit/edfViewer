package com.biorecorder.basechart.traces;


import com.biorecorder.basechart.*;
import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.graphics.BPath;
import com.biorecorder.basechart.graphics.BPoint;
import com.biorecorder.basechart.scales.Scale;

/**
 * Created by galafit on 11/10/17.
 */
public class LineTrace extends Trace {
    LineTraceConfig traceConfig;


    public LineTrace(ChartData data) {
        super(data);
        traceConfig = new LineTraceConfig();
    }

    @Override
    public int getMarkSize() {
        return traceConfig.getMarkSize();
    }

    BColor getLineColor(int curveNumber) {
        return curvesColors[curveNumber];
    }

    BColor getMarkColor(int curveNumber) {
        return curvesColors[curveNumber];
    }

    public BColor getFillColor(int curveNumber) {
        return new BColor(getLineColor(curveNumber).getRed(), getLineColor(curveNumber).getGreen(), getLineColor(curveNumber).getBlue(), 110);
    }

    @Override
    protected NearestPoint nearest(int x, int y, int curveNumber1, ChartData data) {
        double xValue =  xScale.invert(x);
        int pointIndex = dataManager.nearest(xValue);
        int pointX = (int) xScale.scale(data.getValue(pointIndex, 0));

        int distanceMin = 0;
        int curveNumber = 0;

        int startCurve = 0;
        int curveCount = curveCount();
        if(curveNumber1 >= 0) {
           startCurve = curveNumber1;
           curveCount = 1;
        }
        int dx = pointX - x;
        int dx2 = dx * dx;

        for (int i = startCurve; i < startCurve + curveCount; i++) {
            int pointY =  (int) getYScale(i).scale(data.getValue(pointIndex, i + 1));

            int dy = pointY - y;
            int distance = dx2 + dy * dy;
            if(distanceMin == 0 || distanceMin > distance) {
                curveNumber = i;
                distanceMin = distance;
            }
        }
        return new NearestPoint(new TraceCurvePoint(this, curveNumber, pointIndex), distanceMin);
    }

    @Override
    protected int curveCount(ChartData data) {
        return data.columnCount() - 1;
    }

    @Override
    protected BPoint dataPosition(int curveNumber, int dataIndex, ChartData data) {
        XYViewer xyData = new XYViewer(data, curveNumber);
        Scale yScale = getYScale(curveNumber);
        return new BPoint((int)xScale.scale(xyData.getX(dataIndex)), (int)yScale.scale(xyData.getY(dataIndex)));
    }


    @Override
    public TooltipItem[] info(int curveNumber, int dataIndex, ChartData data) {
        if (dataIndex == -1){
            return new TooltipItem[0];
        }
        String curveName = getCurveName(curveNumber);
        BColor curveColor = getCurveMainColor(curveNumber);
        XYViewer xyData = new XYViewer(data, curveNumber);
        Scale yScale = getYScale(curveNumber);

        TooltipItem[] infoItems = new TooltipItem[3];
        infoItems[0] = new TooltipItem(curveName, "", curveColor);
        // infoItems[1] = new InfoItem("X: ", String.valueOf(xyData.getX(dataIndex)), null);
        // infoItems[2] = new InfoItem("Y: ", String.valueOf(xyData.getY(dataIndex)), null);
        infoItems[1] = new TooltipItem("X: ", xScale.formatDomainValue(xyData.getX(dataIndex)), null);
        infoItems[2] = new TooltipItem("Y: ", yScale.formatDomainValue(xyData.getY(dataIndex)), null);

        return infoItems;
    }

    @Override
    protected BRange yMinMax(int curveNumber, ChartData data) {
        return data.getColumnMinMax(curveNumber + 1);
    }


    @Override
    public String getCurveName(int curveNumber) {
        String columnName = dataManager.getColumnName(curveNumber + 1);
        if(columnName != null && columnName.isEmpty()) {
            return columnName;
        }
        if(curveCount() == 1) {
            return name;
        }
        return name + "_curve" + curveNumber;
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
        canvas.setColor(getLineColor(curveNumber));
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
        canvas.drawOval(x - pointRadius, y - pointRadius, 2 * pointRadius,2 * pointRadius);
        for (int i = 1; i < xyData.size(); i++) {
            x = (int) xScale.scale(xyData.getX(i));
            y = (int) yScale.scale(xyData.getY(i));
            path.lineTo(x, y);
            canvas.drawOval(x - pointRadius,y - pointRadius, 2 * pointRadius,2 * pointRadius);
        }
        canvas.setColor(lineColor);
        canvas.setStroke(traceConfig.getLineStroke());
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
        canvas.drawOval(x - pointRadius, y - pointRadius, 2 * pointRadius,2 * pointRadius);
        for (int i = 1; i < xyData.size(); i++) {
            x = (int) xScale.scale(xyData.getX(i));
            path.lineTo(x, y);
            y = (int) yScale.scale(xyData.getY(i));
            path.lineTo(x, y);
            canvas.drawOval(x - pointRadius,y - pointRadius, 2 * pointRadius,2 * pointRadius);
        }
        canvas.setColor(lineColor);
        canvas.setStroke(traceConfig.getLineStroke());
        canvas.drawPath(path);
        return path;
    }

    private BPath drawVerticalLinesPath(BCanvas canvas, XYViewer xyData, Scale yScale, BColor lineColor, BColor markColor) {
        int x = (int) xScale.scale(xyData.getX(0));
        int y = (int) yScale.scale(xyData.getY(0));
        int pointRadius = traceConfig.getMarkSize() / 2;
        canvas.drawOval(x - pointRadius, y - pointRadius, 2 * pointRadius,2 * pointRadius);
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
