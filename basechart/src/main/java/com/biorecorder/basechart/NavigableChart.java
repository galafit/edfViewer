package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.*;
import com.biorecorder.basechart.scales.Scale;
import com.biorecorder.basechart.scroll.Scroll;
import com.biorecorder.basechart.scroll.ScrollConfig;
import com.biorecorder.basechart.scroll.ScrollListener;
import com.sun.istack.internal.Nullable;

import java.util.*;


/**
 * Created by galafit on 3/10/17.
 */
public class NavigableChart {
    private Chart chart;
    private Chart navigator;
    private int previewXIndex = 0;
    private boolean isScrollDirty = true;

    private BRectangle fullArea;
    private BRectangle chartArea;
    private BRectangle navigatorArea;
    private Map<Integer, Scroll> scrolls = new Hashtable<Integer, Scroll>(2);
    private boolean scrollsAtTheEnd = true;

    private boolean autoScrollEnable = true;
    private boolean autoScaleEnableDuringScroll = true; // chart Y auto scale during scrolling
    private NavigableChartConfig config;

    public NavigableChart() {
        this(new NavigableChartConfig());
    }

    public NavigableChart(NavigableChartConfig config) {
        this.config = config;
        chart = new Chart(config.getChartConfig());
        DataProcessingConfig previewDataProcessingConfig = new DataProcessingConfig();
        previewDataProcessingConfig.setCropEnabled(false);
        navigator = new Chart(config.getNavigatorConfig(), previewDataProcessingConfig);

    }

    private void createScrolls() {
        updatePreviewMinMax();
        Range previewXRange = navigator.getXMinMax(previewXIndex);
        double xMin = previewXRange.getMin();
        for (int xIndex = 0; xIndex < chart.xAxisCount(); xIndex++) {
            if (scrolls.get(xIndex) == null  && chart.isXAxisVisible(xIndex)) {
                double xExtent = chart.getBestExtent(xIndex);
                Scroll scroll = new Scroll();
                scroll.setMinMax(previewXRange);
                scroll.setValue(xMin);
                scroll.setExtent(xExtent);
                double xMax = xMin + xExtent;
                chart.setXMinMax(xIndex, xMin, xMax);
                for (int i = 0; i < chart.yAxisCount(); i++) {
                    chart.autoScaleY(i);
                }
                scrolls.put(xIndex, scroll);
                final int scrollXIndex = xIndex;
                scroll.addListener(new ScrollListener() {
                    @Override
                    public void onScrollChanged(double scrollValue, double scrollExtent) {
                        Range xRange = new Range(scrollValue, scrollValue + scrollExtent);
                        chart.setXMinMax(scrollXIndex, xRange.getMin(), xRange.getMax());
                        scrollsAtTheEnd = isScrollAtTheEnd(scrollXIndex);
                        if (autoScaleEnableDuringScroll) {
                            autoScaleChartY();
                        }
                    }
                });
            }

            if (scrolls.get(xIndex) != null  && !chart.isXAxisVisible(xIndex)) {
                scrolls.remove(xIndex);
            }
        }
        if (autoScrollEnable) {
            //   scrollToEnd();
        }
    }


    private void autoScaleChartY() {
        for (int i = 0; i < chartYAxisCount(); i++) {
            autoScaleChartY(i);
        }
    }

    private void updatePreviewMinMax() {
        Range previewMinMax = navigator.getXMinMax(0);
        Range chartMinMax = chart.getAllTracesFullMinMax();
        for (int xAxisIndex = 0; xAxisIndex < chart.xAxisCount(); xAxisIndex++) {
            chartMinMax = Range.join(chartMinMax, chart.getXMinMax(xAxisIndex));
        }
        if (!previewMinMax.contains(chartMinMax)) {
            previewMinMax = Range.join(previewMinMax, chartMinMax);
            navigator.setXMinMax(0, previewMinMax.getMin(), previewMinMax.getMax());
        }
    }

    private boolean scrollToEnd() {
        Range dataMinMax = chart.getAllTracesFullMinMax();
        double minExtent = 0;

        for (Integer xAxisIndex : scrolls.keySet()) {
            double scrollExtent = scrolls.get(xAxisIndex).getExtent();
            minExtent = (minExtent == 0) ? scrollExtent : Math.min(minExtent, scrollExtent);
        }
        return setScrollsValue(dataMinMax.getMax() - minExtent);
    }


    private boolean isScrollAtTheEnd(int xAxisIndex) {
        Range dataRange = chart.getAllTracesFullMinMax();
        if (dataRange != null) {
            double dataMax = dataRange.getMax();
            double scrollEnd = scrolls.get(xAxisIndex).getValue() + scrolls.get(xAxisIndex).getExtent();
            if (dataMax - scrollEnd > 0) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public void draw(BCanvas canvas) {
        if(fullArea == null) {
            setArea(canvas.getBounds());
        }
        if(isScrollDirty) {
            createScrolls();
            isScrollDirty = false;
        }
        canvas.setColor(config.getBackgroundColor());
        canvas.fillRect(fullArea.x, fullArea.y, fullArea.width, fullArea.height);
        Insets chartMargin = chart.getMargin(canvas);
        Insets previewMargin = navigator.getMargin(canvas);
        if (chartMargin.left() != previewMargin.left() || chartMargin.right() != previewMargin.right()) {
            int left = Math.max(chartMargin.left(), previewMargin.left());
            int right = Math.max(chartMargin.right(), previewMargin.right());
            chartMargin = new Insets(chartMargin.top(), right, chartMargin.bottom(), left);
            previewMargin = new Insets(previewMargin.top(), right, previewMargin.bottom(), left);
            chart.setMargin(chartMargin);
            navigator.setMargin(previewMargin);
        }
        navigator.draw(canvas);
        chart.draw(canvas);
        for (Integer key : scrolls.keySet()) {
            drawScroll(canvas, scrolls.get(key));
        }


    }

    private void drawScroll(BCanvas canvas, Scroll scroll) {
        BRectangle area = navigator.getGraphArea(canvas);
        ScrollConfig scrollConfig = config.getScrollConfig();
        Range scrollRange = getScrollStartEnd(scroll);

        int borderWidth = scrollConfig.getBorderWidth();

        int scrollX = (int)scrollRange.getMin();
        int scrollY = area.y + borderWidth/2;
        int scrollHeight = area.height - borderWidth;
        int scrollWidth = Math.max(1, (int)scrollRange.length());


        canvas.setColor(scrollConfig.getFillColor());
        canvas.fillRect(scrollX, scrollY, scrollWidth, scrollHeight);

        canvas.setColor(scrollConfig.getColor());
        canvas.setStroke(new BStroke(borderWidth));
        canvas.drawRect(scrollX, scrollY, scrollWidth, scrollHeight);
    }


    private void calculateAndSetAreas() {
        int top = config.getSpacing().top();
        int bottom = config.getSpacing().bottom();
        int left = config.getSpacing().left();
        int right = config.getSpacing().right();
        int gap = config.getGap();

        int width = fullArea.width - left - right;
        int height = fullArea.height - top - bottom - gap;

        int navigatorHeight;
        if (navigator.traceCount() == 0) {
            navigatorHeight = config.getNavigatorHeightMin();
        } else {
            int chartWeight = chart.getStacksSumWeight();
            int navigatorWeight = navigator.getStacksSumWeight();
            navigatorHeight = height * navigatorWeight / (chartWeight + navigatorWeight);

        }

        int chartHeight = height - navigatorHeight;

        if (chartHeight <= 0 || navigatorHeight <= 0) {
           // TODO think...
        }

        chartArea = new BRectangle(fullArea.x + left, fullArea.y + top, width, chartHeight);
        navigatorArea = new BRectangle(fullArea.x + left, fullArea.y + fullArea.height - navigatorHeight, width, navigatorHeight);

        chart.setArea(chartArea);
        navigator.setArea(navigatorArea);
    }


    private Range getScrollStartEnd(Scroll scroll) {
        Scale scale = navigator.getXAxisScale(previewXIndex);
        double scrollStart = scale.scale(scroll.getValue());
        double scrollEnd = scale.scale(scroll.getValue() + scroll.getExtent());
        return new Range(scrollStart, scrollEnd);
    }


    private Range getScrollVisibleStartEnd(Scroll scroll) {
        ScrollConfig scrollConfig = config.getScrollConfig();
        Range scrollRange = getScrollStartEnd(scroll);
        return new Range(scrollRange.getMin() - scrollConfig.getExtraSpace(), scrollRange.getMax() + scrollConfig.getExtraSpace());
    }


    /**
     * =======================Base methods to interact==========================
     **/


    public void setArea(BRectangle area) {
        fullArea = area;
        calculateAndSetAreas();
    }

    public void update() {
        updatePreviewMinMax();
        for (Integer xAxis : scrolls.keySet()) {
            scrolls.get(xAxis).setMinMax(navigator.getXMinMax(previewXIndex));
        }

        if (autoScrollEnable && scrollsAtTheEnd) {
            scrollToEnd();
        }
    }

    public boolean hoverOff() {
        if (chart.hoverOff()) {
            return true;
        }
        if (navigator.hoverOff()) {
            return true;
        }
        return false;
    }

    public boolean hoverOn(int x, int y) {
        if(chart.hoverOn(x, y)) {
            return true;
        }
        if(navigator.hoverOn(x, y)) {
            return true;
        }

       return false;
    }


    public boolean isScrollContains(int x, int y) {
        if(!navigatorArea.contains(x, y)) {
            return false;
        }

        for (Integer key : scrolls.keySet()) {
            if (getScrollVisibleStartEnd(scrolls.get(key)).contains(x)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return true if scrollValue was changed and false if newValue = current scroll value
     */
    public boolean setScrollsValue(double newValue) {
        boolean scrollsMoved = false;
        for (Integer xAxisIndex : scrolls.keySet()) {
            scrollsMoved = scrolls.get(xAxisIndex).setValue(newValue) || scrollsMoved;
        }
        return scrollsMoved;
    }


    /**
     * @return true if scrollValue was changed and false if newValue = current scroll value
     */
    public boolean setScrollsPosition(double x, double y) {
        if (navigatorArea == null) {
            return false;
        }
        boolean scrollsMoved = false;
        for (Integer key : scrolls.keySet()) {
            Scale scale = navigator.getXAxisScale(previewXIndex);
            double value = scale.invert(x);
            scrollsMoved = scrolls.get(key).setValue(value) || scrollsMoved;
        }
        return scrollsMoved;
    }

    public boolean translateScrolls(double dx) {
        Double maxScrollsPosition = null;
        Scale scale = navigator.getXAxisScale(previewXIndex);
        for (Integer key : scrolls.keySet()) {
            double scrollPosition = scale.scale(scrolls.get(key).getValue());
            maxScrollsPosition = (maxScrollsPosition == null) ? scrollPosition : Math.max(maxScrollsPosition, scrollPosition);
        }
        return setScrollsPosition(maxScrollsPosition + dx, navigatorArea.y);
    }


    public void zoomScrollExtent(int xIndex, double zoomFactor) {
        Scroll scroll = scrolls.get(xIndex);
        if (scroll != null) {
            scroll.setExtent(scroll.getExtent() * zoomFactor);
        }
    }


    public double getScrollExtent(int xIndex) {
        Scroll scroll = scrolls.get(xIndex);
        if (scroll != null) {
            return scroll.getExtent();
        }
        return 0;
    }

    public void setScrollExtent(int xIndex, double extent) {
        Scroll scroll = scrolls.get(xIndex);
        if (scroll != null) {
            scroll.setExtent(extent);
        }
    }

    public void autoScaleScrollExtent(int xIndex) {
        Scroll scroll = scrolls.get(xIndex);
        if(scroll != null) {
            scroll.setExtent(chart.getBestExtent(xIndex));
        }
    }

    public boolean selectTrace(int x, int y) {
        if(chart.selectCurve(x, y)) {
            return true;
        } else {
            return navigator.selectCurve(x, y);
        }
    }


    public Range getXMinMax() {
        return navigator.getXMinMax(0);
    }

    /**
     * =======================Base methods to interact with chart ==========================
     **/

    public void addChartStack() {
        chart.addStack();
    }

    public void addChartStack(int weight) {
        chart.addStack(weight);
    }

    public void addChartTrace(Trace trace, boolean isSplit, boolean isXAxisOpposite, boolean isYAxisOpposite) {
        chart.addTrace(trace, isSplit, isXAxisOpposite, isYAxisOpposite);
        isScrollDirty = true;
    }

    public void addChartTrace(Trace trace, boolean isSplit) {
        chart.addTrace(trace, isSplit);
        isScrollDirty = true;
    }

    public void addChartTrace(int stackNumber, Trace trace, boolean isSplit) {
        chart.addTrace(stackNumber, trace, isSplit);
        isScrollDirty = true;
    }

    public void addChartTrace(int stackNumber, Trace trace, boolean isSplit, boolean isXAxisOpposite, boolean isYAxisOpposite) {
        chart.addTrace(stackNumber, trace, isSplit, isXAxisOpposite, isYAxisOpposite);
        isScrollDirty = true;
    }



    public void removeChartTrace(int traceNumber) {
        chart.removeTrace(traceNumber);
        isScrollDirty = true;
    }

    public int chartTraceCount() {
        return chart.traceCount();
    }

    public void setChartYMinMax(int yAxisIndex, double min, double max) {
        chart.setYMinMax(yAxisIndex, min, max);
    }

    public Range getChartYMinMax(int yAxisIndex) {
        return chart.getYMinMax(yAxisIndex);
    }

    public int getChartYIndex(@Nullable BPoint point) {
        return chart.getYIndex(point);
    }

    public int getChartXIndex(@Nullable BPoint point) {
        return chart.getXIndex(point);
    }

    public int chartXAxisCount() {
        return chart.xAxisCount();
    }

    public int chartYAxisCount() {
        return chart.yAxisCount();
    }

    public void zoomChartY(int yAxisIndex, double zoomFactor) {
        chart.zoomY(yAxisIndex, zoomFactor);
    }

    public void translateChartY(int yAxisIndex, int dy) {
        chart.translateY(yAxisIndex, dy);
    }

    public void autoScaleChartY(int yAxisIndex) {
        chart.autoScaleY(yAxisIndex);
    }


    public boolean isChartContains(BPoint point) {
        if(point != null && chartArea.contains(point.getX(), point.getY())) {
            return true;
        }
        return false;
    }


    /**
     * =======================Base methods to interact with navigator==========================
     **/

    public boolean isNavigatorContains(BPoint point) {
        if(point != null && navigatorArea.contains(point.getX(), point.getY())) {
            return true;
        }
        return false;
    }

    public void addNavigatorStack() {
        navigator.addStack();
    }

    public void addNavigatorStack(int weight) {
        navigator.addStack(weight);
    }

    public void addNavigatorTrace(Trace trace, boolean isSplit, boolean isXAxisOpposite, boolean isYAxisOpposite) {
        navigator.addTrace(trace, isSplit, isXAxisOpposite, isYAxisOpposite);
    }

    public void addNavigatorTrace(Trace trace, boolean isSplit) {
        navigator.addTrace(trace, isSplit);
    }

    public void addNavigatorTrace(int stackNumber, Trace trace, boolean isSplit) {
        navigator.addTrace(stackNumber, trace, isSplit);
    }

    public void addNavigatorTrace(int stackNumber, Trace trace, boolean isSplit, boolean isXAxisOpposite, boolean isYAxisOpposite) {
        navigator.addTrace(stackNumber, trace, isSplit, isXAxisOpposite, isYAxisOpposite);
    }

    public void removeNavigatorTrace(int traceNumber) {
        navigator.removeTrace(traceNumber);
    }

    public int navigatorTraceCount() {
        return navigator.traceCount();
    }


    public int navigatorYAxisCount() {
        return navigator.yAxisCount();
    }

    public int getNavigatorYIndex(@Nullable BPoint point) {
        return navigator.getYIndex(point);
    }

    public void zoomNavigatorY(int yAxisIndex, double zoomFactor) {
        navigator.zoomY(yAxisIndex, zoomFactor);
    }

    public void translateNavigatorY(int yAxisIndex, int dy) {
        navigator.translateY(yAxisIndex, dy);

    }
    public void autoScaleNavigatorY(int yAxisIndex) {
        navigator.autoScaleY(yAxisIndex);
    }
}
