package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BPoint;
import com.biorecorder.basechart.graphics.BRectangle;

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
        return chart.selectTrace(x, y);
    }

    @Override
    public boolean onDoubleTap(int x, int y) {
        int xIndex = chart.getXIndex(null);
        int yIndex = chart.getYIndex(null);

        if(xIndex >= 0 && yIndex >= 0) {
            chart.autoScaleX(xIndex);
            chart.autoScaleY(yIndex);
        } else {
            for (int i = 0; i < chart.xAxisCount(); i++) {
                chart.autoScaleX(i);
            }
            for (int i = 0; i < chart.yAxisCount(); i++) {
                chart.autoScaleY(i);
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
    public boolean onScaleX(BPoint startPoint, double scaleFactor) {
        if(scaleFactor == 0 || scaleFactor == 1) {
            return false;
        }
        if(chart.traceCount() == 0) {
            return false;
        }
        int xAxis = chart.getXIndex(startPoint);
        if(xAxis >= 0) {
            chart.zoomX(xAxis, scaleFactor);
            return true;
        }
        return false;
    }

    @Override
    public boolean onScaleY(BPoint startPoint, double scaleFactor) {
        if(scaleFactor == 0 || scaleFactor == 1) {
            return false;
        }

        if(chart.traceCount() == 0) {
            return false;
        }
        int yAxis = chart.getYIndex(startPoint);
        if(yAxis >= 0) {
            chart.zoomY(yAxis, scaleFactor);
            return true;
        }
        return false;
    }

    @Override
    public boolean onScrollX(BPoint startPoint, int dx) {
        if(dx == 0) {
            return false;
        }
        if(chart.traceCount() == 0) {
            return false;
        }
        int xAxis = chart.getXIndex(startPoint);
        if(xAxis >= 0) {
            chart.translateX(xAxis, dx);
            return true;
        }
        return false;
    }

    @Override
    public boolean onScrollY(BPoint startPoint, int dy) {
        if(dy == 0) {
            return false;
        }
        if(chart.traceCount() == 0) {
            return false;
        }
        int yAxis = chart.getYIndex(startPoint);
        if(yAxis >= 0) {
            chart.translateY(yAxis, dy);
            return true;
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
