package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BPoint;
import com.biorecorder.basechart.graphics.BRectangle;

/**
 * Created by galafit on 23/9/18.
 */
public class InteractiveNavigableChart implements InteractiveDrawable {
    private final NavigableChart chart;
    private BPoint lastStartPoint;
    private boolean isScrollMoving;

    public InteractiveNavigableChart(NavigableChart chart) {
        this.chart = chart;
    }

    @Override
    public void onResize(BRectangle area) {
        chart.setArea(area);
    }

    @Override
    public boolean onTap(int x, int y) {
        if(chart.selectTrace(x, y)) {
            return true;
        } else {
            if(chart.isNavigatorContains(new BPoint(x, y)))  {
                return chart.setScrollsPosition(x, y);
            }
            return false;
        }
    }

    @Override
    public boolean onDoubleTap(int x, int y) {
        // AUTO SCALE both chart and preview

        int xIndex = chart.getChartXIndex(null);
        int yIndex = chart.getChartYIndex(null);
        // if some trace is selected we auto scale only axis belonging to that trace
        if (xIndex >= 0 && yIndex >= 0) {
            chart.autoScaleScrollExtent(xIndex);
            chart.autoScaleChartY(yIndex);
        } else { // if no selected trace in chart we scale all x and y axis
            for (int i = 0; i < chart.chartXAxisCount(); i++) {
                chart.autoScaleScrollExtent(i);
            }
            for (int i = 0; i < chart.chartYAxisCount(); i++) {
                chart.autoScaleChartY(i);
            }
        }

        // do the same with preview...
        int previewYIndex = chart.getNavigatorYIndex(null);
        if (previewYIndex >= 0) {
            chart.autoScaleNavigatorY(previewYIndex);
        } else {
            for (int i = 0; i < chart.navigatorYAxisCount(); i++) {
                chart.autoScaleNavigatorY(i);
            }
        }
        return true;
    }

    @Override
    public boolean onTapUp(int x, int y) {
        isScrollMoving = false;
        return chart.hoverOff();
    }

    @Override
    public boolean onLongPress(int x, int y) {
        return chart.hoverOn(x, y);
    }

    @Override
    public boolean onScaleX(BPoint startPoint, double scaleFactor) {
        if (scaleFactor == 0 || scaleFactor == 1) {
            return false;
        }

        int xIndex = chart.getChartXIndex(startPoint);
        if(xIndex >= 0) {
            chart.zoomScrollExtent(xIndex, scaleFactor);
        } else {
            for (int i = 0; i < chart.chartXAxisCount(); i++) {
                chart.zoomScrollExtent(i, scaleFactor);
            }
        }
        return true;
    }

    @Override
    public boolean onScaleY(BPoint startPoint, double scaleFactor) {
        if (startPoint == null || scaleFactor == 0 || scaleFactor == 1) {
            return false;
        }
        int yAxis = chart.getChartYIndex(startPoint);
        if(yAxis >= 0) {
            chart.zoomChartY(yAxis, scaleFactor);
            return true;
        }

        yAxis = chart.getNavigatorYIndex(startPoint);
        if(yAxis >= 0) {
            chart.zoomNavigatorY(yAxis, scaleFactor);
            return true;
        }
        return false;
    }

    @Override
    public boolean onScrollX(BPoint startPoint, int dx) {
        if(dx == 0) {
            return false;
        }

        if(startPoint != null && !startPoint.equals(lastStartPoint)){
            lastStartPoint = startPoint;
            isScrollMoving = chart.isScrollContains(startPoint.getX(), startPoint.getY());
        }

        if(isScrollMoving) {
            chart.translateScrolls(-dx);
            return true;
        }

        if(startPoint == null || chart.isChartContains(startPoint)) {
            double scrollTranslation = 0;
            int xIndex = chart.getChartXIndex(startPoint);
            if(xIndex >= 0) {
                scrollTranslation = chart.getScrollWidth(xIndex) / chart.getArea().width;
            } else {
                for (int i = 0; i < chart.chartXAxisCount(); i++) {
                    double translation = chart.getScrollWidth(i) / chart.getArea().width;
                    scrollTranslation = Math.max(scrollTranslation, translation);
                }
            }
            chart.translateScrolls(dx * scrollTranslation);
            return true;
        }

        return false;
    }

    @Override
    public boolean onScrollY(BPoint startPoint, int dy) {
        if(dy == 0 || startPoint == null) {
            return false;
        }

        int yAxis = chart.getChartYIndex(startPoint);
        if(yAxis >= 0) {
            chart.translateChartY(yAxis, dy);
            return true;
        }

        yAxis = chart.getNavigatorYIndex(startPoint);
        if(yAxis >= 0) {
            chart.translateNavigatorY(yAxis, dy);
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
        chart.update();
        return true;
    }
}
