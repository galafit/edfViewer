package com.biorecorder.basecharts;

import com.biorecorder.basecharts.graphics.BCanvas;
import com.biorecorder.basecharts.graphics.BPoint;
import com.biorecorder.basecharts.graphics.BRectangle;

/**
 * Created by galafit on 23/9/18.
 */
public class InteractiveScrollableChart implements InteractiveDrawable {
    private final ScrollableChart chart;
    private BPoint currentPoint;
    private boolean isScrollbarMoved;


    public InteractiveScrollableChart(ScrollableChart chart) {
        this.chart = chart;
    }

    @Override
    public void onResize(BRectangle area) {
        chart.setArea(area);
    }

    @Override
    public boolean onTap(int x, int y) {
        if (chart.chartContains(x, y)) {
            return chart.selectChartTrace(x, y);
        } else {
            return chart.selectPreviewTrace(x, y);
        }
    }

    @Override
    public boolean onDoubleTap(int x, int y) {
        chart.autoScale();
        return true;
    }

    @Override
    public boolean onTapUp(int x, int y) {
        return chart.hoverOff();
    }

    @Override
    public boolean onLongPress(int x, int y) {
        if(chart.chartContains(x, y)) {
            if(chart.chartTraceCount() == 0) {
                return false;
            }
            int selectedTraceIndex = chart.getChartSelectedTraceIndex();
            if(selectedTraceIndex < 0) {
                selectedTraceIndex = 0;
            }
            return chart.chartHoverOn(x, y, selectedTraceIndex);
        } else {
            if(chart.previewTraceCount() == 0) {
                return false;
            }
            int selectedTraceIndex = chart.getPreviewSelectedTraceIndex();
            if(selectedTraceIndex < 0) {
                selectedTraceIndex = 0;
            }
            return chart.previewHoverOn(x, y, selectedTraceIndex);
        }
    }

    @Override
    public boolean onScaleX(BPoint startPoint, double scaleFactor) {
        if (scaleFactor == 0 || scaleFactor == 1) {
            return false;
        }

        if(startPoint == null || chart.chartContains(startPoint.getX(), startPoint.getY())) {
            int selectedTraceIndex = chart.getChartSelectedTraceIndex();
            if (selectedTraceIndex >= 0) {
                chart.zoomScrollExtent(chart.getChartTraceXIndex(selectedTraceIndex), scaleFactor);
            } else {
                for (int i = 0; i < chart.chartXAxisCount(); i++) {
                    chart.zoomScrollExtent(i, scaleFactor);
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean onScaleY(BPoint startPoint, double scaleFactor) {
        if (startPoint == null || scaleFactor == 0 || scaleFactor == 1) {
            return false;
        }
        if (chart.chartContains(startPoint.getX(), startPoint.getY())) {
            if(chart.chartTraceCount() == 0) {
                return false;
            }
            int selectedTraceIndex = chart.getChartSelectedTraceIndex();
            if (selectedTraceIndex < 0) {
                selectedTraceIndex = 0;
            }
            chart.zoomChartY(chart.getChartTraceYIndex(selectedTraceIndex), scaleFactor);
            return true;
        } else {
            if(chart.previewTraceCount() == 0) {
                return false;
            }
            int selectedTraceIndex = chart.getPreviewSelectedTraceIndex();
            if (selectedTraceIndex < 0) {
                selectedTraceIndex = 0;
            }
            chart.zoomPreviewY(chart.getPreviewTraceYIndex(selectedTraceIndex), scaleFactor);
            return true;
        }
    }

    @Override
    public boolean onScrollX(BPoint startPoint, int dx) {
        if(dx == 0) {
            return false;
        }

        if(startPoint == null || chart.chartContains(startPoint.getX(), startPoint.getY())) {
            double scrollTranslation = 0;
            int selectedTraceIndex = chart.getChartSelectedTraceIndex();
            if(selectedTraceIndex >= 0) {
                int xIndex = chart.getChartTraceXIndex(selectedTraceIndex);
                scrollTranslation = chart.getScrollExtent(xIndex) / chart.getXMinMax().length();
            } else {
                for (int i = 0; i < chart.chartXAxisCount(); i++) {
                    double translation = chart.getScrollExtent(i) / chart.getXMinMax().length();
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

        if (chart.chartContains(startPoint.getX(), startPoint.getY())) {
            if(chart.chartTraceCount() == 0) {
                return false;
            }
            int selectedTraceIndex = chart.getChartSelectedTraceIndex();
            if (selectedTraceIndex < 0) {
                selectedTraceIndex = 0;
            }

            chart.translateChartY(chart.getChartTraceYIndex(selectedTraceIndex), dy);
            return true;
        } else {
            if(chart.previewTraceCount() == 0) {
                return false;
            }
            int selectedTraceIndex = chart.getPreviewSelectedTraceIndex();
            if (selectedTraceIndex  < 0) {
                selectedTraceIndex = 0;
            }

            chart.translatePreviewY(chart.getPreviewTraceYIndex(selectedTraceIndex), dy);
            return true;
        }
    }

    /**
     * Used to move scrollbar only
     */
    public boolean onDrag(BPoint startPoint, int dx, int dy) {
        if(startPoint != null && chart.previewContains(startPoint.getX(), startPoint.getY())) {
            if(!startPoint.equals(currentPoint)){
                currentPoint = startPoint;
                isScrollbarMoved = chart.isPointInsideScroll(startPoint.getX(), startPoint.getY());
            }

            if(isScrollbarMoved) {
                chart.translateScrolls(dx);
                return true;
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
        chart.update();
        return true;
    }
}