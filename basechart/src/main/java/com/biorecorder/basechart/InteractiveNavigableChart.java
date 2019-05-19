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
        // AUTO SCALE both chart and navigator
        if(chart.isChartCurveSelected()) {
            // if some trace is selected we auto scale only axis belonging to that trace
            chart.autoScaleScrollExtent(chart.getChartSelectedCurveX());
            chart.autoScaleChartY(chart.getChartSelectedCurveStack(), chart.getChartSelectedCurveY());
        } else {
            // if no selected trace in chart we scale all x and y axis
            XAxisPosition[] xAxisPositions = chart.getChartXAxes();
            for (int i = 0; i < xAxisPositions.length; i++) {
                XAxisPosition xPosition = xAxisPositions[i];
                if(chart.hasScroll(xPosition)) {
                    chart.autoScaleScrollExtent(xPosition);
                }
            }
            for (int stack = 0; stack < chart.chartStackCount(); stack++) {
                YAxisPosition[] yAxisPositions = chart.getChartYAxes(stack);
                for (int i = 0; i < yAxisPositions.length; i++) {
                    chart.autoScaleChartY(stack, yAxisPositions[i]);
                }
            }
        }

        // do the same with navigator...
        if(chart.isNavigatorCurveSelected()) {
            // if some trace is selected we auto scale only axis belonging to that trace
            chart.autoScaleNavigatorY(chart.getNavigatorSelectedCurveStack(), chart.getNavigatorSelectedCurveY());
        } else {
            // if no selected trace in navigator we scale all  y axis

            for (int stack = 0; stack < chart.navigatorStackCount(); stack++) {
                YAxisPosition[] yAxisPositions = chart.getNavigatorYAxes(stack);
                for (int i = 0; i < yAxisPositions.length; i++) {
                    chart.autoScaleChartY(stack, yAxisPositions[i]);
                }
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
        if(chart.isChartCurveSelected()) {
            // if some trace is selected we auto scale only axis belonging to that trace
            return chart.zoomScrollExtent(chart.getChartSelectedCurveX(), scaleFactor);
        } else {
            boolean isChanged = false;
            XAxisPosition[] xAxisPositions = chart.getChartXAxes();
            for (int i = 0; i < xAxisPositions.length; i++) {
                XAxisPosition xPosition = xAxisPositions[i];
                if(chart.hasScroll(xPosition)) {
                    isChanged = chart.zoomScrollExtent(xPosition, scaleFactor) || isChanged;
                }
            }
            return isChanged;
        }
    }

    @Override
    public boolean onScaleY(BPoint startPoint, double scaleFactor) {
        if (startPoint == null || scaleFactor == 0 || scaleFactor == 1) {
            return false;
        }
        if(chart.isChartContains(startPoint)) {
            if(chart.isChartCurveSelected()) {
                chart.zoomChartY(chart.getChartSelectedCurveStack(), chart.getChartSelectedCurveY(), scaleFactor);
                return true;
            } else {
                int stack = chart.getChartStack(startPoint);
                if(stack >= 0) {
                    YAxisPosition yPosition = chart.getChartYAxis(stack, startPoint);
                    if(yPosition != null) {
                        chart.zoomChartY(stack, yPosition, scaleFactor);
                        return true;
                    }
                }
                return false;
            }
        } else {
            if(chart.isNavigatorCurveSelected()) {
                chart.zoomNavigatorY(chart.getNavigatorSelectedCurveStack(), chart.getNavigatorSelectedCurveY(), scaleFactor);
                return true;
            } else {
                int stack = chart.getNavigatorStack(startPoint);
                if(stack >= 0) {
                    YAxisPosition yPosition = chart.getNavigatorYAxis(stack, startPoint);
                    if(yPosition != null) {
                        chart.zoomNavigatorY(stack, yPosition, scaleFactor);
                        return true;
                    }
                }
                return false;
            }
        }
    }

    @Override
    public boolean onScrollX(BPoint startPoint, int dx) {
        if(dx == 0) {
            return false;
        }

        if(startPoint != null && !startPoint.equals(lastStartPoint)){
            lastStartPoint = startPoint;
            isScrollMoving = chart.isScrollContain(startPoint.getX(), startPoint.getY());
        }

        if(isScrollMoving) {
            chart.translateScrolls(-dx);
            return true;
        }

        if(startPoint == null || chart.isChartContains(startPoint)) {
            double scrollTranslation = 0;
            if(chart.isChartCurveSelected()) {
                XAxisPosition xPosition = chart.getChartSelectedCurveX();
                if(chart.hasScroll(xPosition)) {
                    scrollTranslation = chart.getScrollWidth(xPosition) / chart.getWidth();
                }
            } else {
                XAxisPosition[] xAxisPositions = chart.getChartXAxes();
                for (int i = 0; i < xAxisPositions.length; i++) {
                    XAxisPosition xPosition = xAxisPositions[i];
                    if(chart.hasScroll(xPosition)) {
                        double translation = chart.getScrollWidth(xPosition) / chart.getWidth();
                        scrollTranslation = Math.max(scrollTranslation, translation);
                    }
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

        if(chart.isChartContains(startPoint)) {
            if(chart.isChartCurveSelected()) {
                chart.translateChartY(chart.getChartSelectedCurveStack(), chart.getChartSelectedCurveY(), dy);
                return true;
            } else {
                int stack = chart.getChartStack(startPoint);
                if(stack >= 0) {
                    YAxisPosition yPosition = chart.getChartYAxis(stack, startPoint);
                    if(yPosition != null) {
                        chart.translateChartY(stack, yPosition, dy);
                        return true;
                    }
                }
                return false;
            }
        } else {
            if(chart.isNavigatorCurveSelected()) {
                chart.translateNavigatorY(chart.getNavigatorSelectedCurveStack(), chart.getNavigatorSelectedCurveY(), dy);
                return true;
            } else {
                int stack = chart.getNavigatorStack(startPoint);
                if(stack >= 0) {
                    YAxisPosition yPosition = chart.getNavigatorYAxis(stack, startPoint);
                    if(yPosition != null) {
                        chart.translateNavigatorY(stack, yPosition, dy);
                        return true;
                    }
                }
                return false;
            }
        }
    }


    @Override
    public void draw(BCanvas canvas) {
        chart.draw(canvas);

    }

    @Override
    public boolean update() {
        chart.appendData();
        return true;
    }
}
