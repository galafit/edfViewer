package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BPoint;
import com.biorecorder.basechart.graphics.BRectangle;
import com.sun.istack.internal.Nullable;

/**
 * Created by galafit on 21/9/18.
 */
public class InteractiveChart implements InteractiveDrawable {
    private  final Chart chart;

    public InteractiveChart(Chart chart) {
        this.chart = chart;
    }

    @Override
    public void onResize(BRectangle area) {
        chart.setArea(area);
    }

    @Override
    public boolean onTap(int x, int y) {
        return chart.selectCurve(x, y);
    }

    @Override
    public boolean onDoubleTap(int x, int y) {
        if(chart.isCurveSelected()) {
            chart.autoScaleX(chart.getSelectedCurveX());
            chart.autoScaleY(chart.getSelectedCurveStack(), chart.getSelectedCurveY());
        } else {
            XAxisPosition[] xAxisPositions = chart.getXAxes();
            for (int i = 0; i < xAxisPositions.length; i++) {
                chart.autoScaleX(xAxisPositions[i]);
            }
            for (int stack = 0; stack < chart.stackCount(); stack++) {
                YAxisPosition[] yAxisPositions = chart.getYAxes(stack);
                for (int i = 0; i < yAxisPositions.length; i++) {
                    chart.autoScaleY(stack, yAxisPositions[i]);
                }
            }
        }

        return true;
    }

    @Override
    public boolean onTapUp(int x, int y) {
        return chart.hoverOff();
    }

    @Override
    public boolean onLongPress(int x, int y) {
        if(chart.traceCount() == 0) {
            return false;
        }

       return chart.hoverOn(x, y);
    }

    @Override
    public boolean onScaleX(@Nullable BPoint startPoint, double scaleFactor) {
        if(scaleFactor == 0 || scaleFactor == 1) {
            return false;
        }

        XAxisPosition[] xAxisPositions = new XAxisPosition[0];
        if(chart.isCurveSelected()) {
            XAxisPosition[] xAxisPositions1 = {chart.getSelectedCurveX()};
            xAxisPositions = xAxisPositions1;
        } else if(startPoint != null) {
            XAxisPosition xPosition = chart.getXAxis(startPoint);
            if(xPosition != null) {
                XAxisPosition[] xAxisPositions1 = {xPosition};
                xAxisPositions = xAxisPositions1;
            }
        } else {
           xAxisPositions = chart.getXAxes();
        }

        if(xAxisPositions.length > 0) {
            for (int i = 0; i < xAxisPositions.length; i++) {
                chart.zoomX(xAxisPositions[i], scaleFactor);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onScaleY(@Nullable BPoint startPoint, double scaleFactor) {
        if(scaleFactor == 0 || scaleFactor == 1) {
            return false;
        }

        if(chart.isCurveSelected()) {
            chart.zoomY(chart.getSelectedCurveStack(), chart.getSelectedCurveY(), scaleFactor);
            return true;
        }

        if(startPoint != null) {
            int stack = chart.getStack(startPoint);
            if(stack >= 0) {
                YAxisPosition yAxisPosition = chart.getYAxis(stack, startPoint);
                if(yAxisPosition != null) {
                    chart.zoomY(stack, yAxisPosition, scaleFactor);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onScrollX(@Nullable BPoint startPoint, int dx) {
        if(dx == 0) {
            return false;
        }

        XAxisPosition[] xAxisPositions = new XAxisPosition[0];
        if(chart.isCurveSelected()) {
            XAxisPosition[] xAxisPositions1 = {chart.getSelectedCurveX()};
            xAxisPositions = xAxisPositions1;
        } else if(startPoint != null) {
            XAxisPosition xPosition = chart.getXAxis(startPoint);
            if(xPosition != null) {
                XAxisPosition[] xAxisPositions1 = {xPosition};
                xAxisPositions = xAxisPositions1;
            }
        } else {
            xAxisPositions = chart.getXAxes();
        }

        if(xAxisPositions.length > 0) {
            for (int i = 0; i < xAxisPositions.length; i++) {
                chart.translateX(xAxisPositions[i], dx);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onScrollY(@Nullable BPoint startPoint, int dy) {
        if(dy == 0) {
            return false;
        }
        if(chart.isCurveSelected()) {
            chart.translateY(chart.getSelectedCurveStack(), chart.getSelectedCurveY(), dy);
            return true;
        }

        if(startPoint != null) {
            int stack = chart.getStack(startPoint);
            if(stack >= 0) {
                YAxisPosition yAxisPosition = chart.getYAxis(stack, startPoint);
                if(yAxisPosition != null) {
                    chart.translateY(stack, yAxisPosition, dy);
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public void draw(BCanvas canvas) {
        chart.draw(canvas);

    }

    @Override
    public boolean update() {
       return false;
    }
}
