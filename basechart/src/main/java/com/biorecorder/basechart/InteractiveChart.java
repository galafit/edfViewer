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
        int selectedTraceIndex = chart.getSelectedTraceIndex();
        if(selectedTraceIndex >= 0) {
            chart.autoScaleX(chart.getTraceXIndex(selectedTraceIndex));
            chart.autoScaleY(chart.getTraceYIndex(selectedTraceIndex));
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
        int selectedTraceIndex = chart.getSelectedTraceIndex();
        if(selectedTraceIndex < 0) {
            selectedTraceIndex = 0;
        }
       return chart.hoverOn(x, y, selectedTraceIndex);
    }

    @Override
    public boolean onScaleX(BPoint startPoint, double scaleFactor) {
        if(scaleFactor == 0 || scaleFactor == 1) {
            return false;
        }
        if(chart.traceCount() == 0) {
            return false;
        }
        int selectedTraceIndex = chart.getSelectedTraceIndex();
        if(selectedTraceIndex < 0) {
            selectedTraceIndex = 0;
        }

        chart.zoomX(chart.getTraceXIndex(selectedTraceIndex), scaleFactor);
        return true;
    }

    @Override
    public boolean onScaleY(BPoint startPoint, double scaleFactor) {
        if(scaleFactor == 0 || scaleFactor == 1) {
            return false;
        }

        if(chart.traceCount() == 0) {
            return false;
        }
        int selectedTraceIndex = chart.getSelectedTraceIndex();
        if(selectedTraceIndex < 0) {
            selectedTraceIndex = 0;
        }

        chart.zoomY(chart.getTraceYIndex(selectedTraceIndex), scaleFactor);
        return true;
    }

    @Override
    public boolean onScrollX(BPoint startPoint, int dx) {
        if(dx == 0) {
            return false;
        }

        if(chart.traceCount() == 0) {
            return false;
        }
        int selectedTraceIndex = chart.getSelectedTraceIndex();
        if(selectedTraceIndex < 0) {
            selectedTraceIndex = 0;
        }

        chart.translateX(chart.getTraceXIndex(selectedTraceIndex), dx);
        return true;
    }

    @Override
    public boolean onScrollY(BPoint startPoint, int dy) {
        if(dy == 0) {
            return false;
        }

        if(chart.traceCount() == 0) {
            return false;
        }
        int selectedTraceIndex = chart.getSelectedTraceIndex();
        if(selectedTraceIndex < 0) {
            selectedTraceIndex = 0;
        }

        chart.translateY(chart.getTraceYIndex(selectedTraceIndex), dy);
        return true;
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
