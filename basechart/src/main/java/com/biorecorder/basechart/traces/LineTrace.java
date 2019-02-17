package com.biorecorder.basechart.traces;


import com.biorecorder.basechart.*;
import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BColor;
import com.biorecorder.basechart.graphics.BPath;
import com.biorecorder.basechart.scales.Scale;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by galafit on 11/10/17.
 */
public class LineTrace extends Trace {
     private LineTraceConfig traceConfig;

    public LineTrace(ChartData data) {
        super(data);
        traceConfig = new LineTraceConfig();
    }

    @Override
    public int getMarkSize() {
        return traceConfig.getMarkSize();
    }

    @Override
    public void setCurveColor(int curveNumber, BColor color) {
        BColor[] colors = new BColor[curveCount()];
        for (int i = 0; i < curveCount(); i++) {
            if(i == curveNumber) {
                colors[i] = color;
            } else {
                colors[i] = getCurveColor(i);
            }
        }
        traceConfig.setCurveColors(colors);
    }

    @Override
    public BColor getCurveColor(int curveNumber) {
        BColor[] colors = traceConfig.getCurveColors();
        if(colors != null) {
            return colors[curveNumber % colors.length];
        }
        return null;
    }

    BColor getLineColor(int curveNumber) {
        return getCurveColor(curveNumber);
    }

    BColor getMarkColor(int curveNumber) {
        return getCurveColor(curveNumber);
    }

    public BColor getFillColor(int curveNumber) {
        return new BColor(getCurveColor(curveNumber).getRed(), getCurveColor(curveNumber).getGreen(), getCurveColor(curveNumber).getBlue(), 110);
    }


    @Override
    protected int curveCount(ChartData data) {
        return data.columnCount() - 1;
    }

    @Override
    public TooltipItem[] info(int curveNumber, int dataIndex, ChartData data) {
        if (dataIndex == -1){
            return new TooltipItem[0];
        }
        String curveName = getCurveName(curveNumber);
        BColor curveColor = getCurveColor(curveNumber);
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
        return columnName;
    }

    @Override
    public void setCurveName(int curveNumber, String name) {
        dataManager.setColumnName(curveNumber + 1, name);
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
