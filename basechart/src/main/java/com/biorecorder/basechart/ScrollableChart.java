package com.biorecorder.basechart;

import com.biorecorder.basechart.graphics.*;
import com.biorecorder.basechart.scales.Scale;
import com.biorecorder.basechart.scroll.Scroll;
import com.biorecorder.basechart.scroll.ScrollConfig;
import com.biorecorder.basechart.scroll.ScrollListener;
import com.biorecorder.basechart.themes.ScrollableChartConfigWhite;
import com.sun.istack.internal.Nullable;

import java.util.*;


/**
 * Created by galafit on 3/10/17.
 */
public class ScrollableChart {
    private Chart chart;
    private Chart preview;
    private int previewXIndex = 0;
    private boolean isScrollDirty = true;

    private BRectangle fullArea;
    private BRectangle chartArea;
    private BRectangle previewArea;
    private Map<Integer, Scroll> scrolls = new Hashtable<Integer, Scroll>(2);
    private boolean scrollsAtTheEnd = true;

    private boolean autoScrollEnable = true;
    private boolean autoScaleEnableDuringScroll = true; // chart Y auto scale during scrolling
    private ScrollableChartConfig config;

    public ScrollableChart() {
        this.config = new ScrollableChartConfigWhite();
        chart = new Chart(config.getChartConfig());
        DataProcessingConfig previewDataProcessingConfig = new DataProcessingConfig();
        previewDataProcessingConfig.setCropEnabled(false);
        preview = new Chart(config.getPreviewConfig(), previewDataProcessingConfig);
    }

    private void createScrolls() {
        updatePreviewMinMax();
        Range previewXRange = preview.getXMinMax(previewXIndex);
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
        Range previewMinMax = preview.getXMinMax(0);
        Range chartMinMax = chart.getAllTracesFullMinMax();
        for (int xAxisIndex = 0; xAxisIndex < chart.xAxisCount(); xAxisIndex++) {
            chartMinMax = Range.join(chartMinMax, chart.getXMinMax(xAxisIndex));
        }
        if (!previewMinMax.contains(chartMinMax)) {
            previewMinMax = Range.join(previewMinMax, chartMinMax);
            preview.setXMinMax(0, previewMinMax.getMin(), previewMinMax.getMax());
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
        Insets previewMargin = preview.getMargin(canvas);
        if (chartMargin.left() != previewMargin.left() || chartMargin.right() != previewMargin.right()) {
            int left = Math.max(chartMargin.left(), previewMargin.left());
            int right = Math.max(chartMargin.right(), previewMargin.right());
            chartMargin = new Insets(chartMargin.top(), right, chartMargin.bottom(), left);
            previewMargin = new Insets(previewMargin.top(), right, previewMargin.bottom(), left);
            chart.setMargin(chartMargin);
            preview.setMargin(previewMargin);
        }
        preview.draw(canvas);

        for (Integer key : scrolls.keySet()) {
            drawScroll(canvas, scrolls.get(key));
        }

        chart.draw(canvas);
    }

    private void drawScroll(BCanvas canvas, Scroll scroll) {
        BRectangle area = preview.getGraphArea(canvas);
        ScrollConfig scrollConfig = config.getScrollConfig();
        Range scrollRange = getScrollStartEnd(scroll);

        int borderWidth = scrollConfig.getBorderWidth();

        int scrollX = (int)scrollRange.getMin();
        int scrollY = area.y + borderWidth/2;
        int scrollHeight = area.height - borderWidth;
        int scrollWidth = Math.max(1, (int)scrollRange.length());


        BColor scrollColor = scrollConfig.getColor();
        BColor fillColor = new BColor(scrollColor.getRed(), scrollColor.getGreen(), scrollColor.getBlue(), 70);
        canvas.setColor(fillColor);
        canvas.fillRect(scrollX, scrollY, scrollWidth, scrollHeight);

        BColor borderColor = new BColor(scrollColor.getRed(), scrollColor.getGreen(), scrollColor.getBlue(), 130);
        canvas.setColor(borderColor);
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

        int previewHeight;
        if (preview.traceCount() == 0) {
            previewHeight = config.getPreviewHeightMin();
        } else {
            int chartWeight = chart.getStacksSumWeight();
            int previewWeight = preview.getStacksSumWeight();
            previewHeight = height * previewWeight / (chartWeight + previewWeight);

        }

        int chartHeight = height - previewHeight;

        if (chartHeight <= 0 || previewHeight <= 0) {
           // TODO think...
        }

        chartArea = new BRectangle(fullArea.x + left, fullArea.y + top, width, chartHeight);
        previewArea = new BRectangle(fullArea.x + left, fullArea.y + fullArea.height - previewHeight, width, previewHeight);

        chart.setArea(chartArea);
        preview.setArea(previewArea);
    }


    private Range getScrollStartEnd(Scroll scroll) {
        Scale scale = preview.getXAxisScale(previewXIndex);
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
            scrolls.get(xAxis).setMinMax(preview.getXMinMax(previewXIndex));
        }

        if (autoScrollEnable && scrollsAtTheEnd) {
            scrollToEnd();
        }
    }

    public boolean hoverOff() {
        if (chart.hoverOff()) {
            return true;
        }
        if (preview.hoverOff()) {
            return true;
        }
        return false;
    }

    public boolean hoverOn(int x, int y) {
        if(chart.hoverOn(x, y)) {
            return true;
        }
        if(preview.hoverOn(x, y)) {
            return true;
        }

       return false;
    }


    public boolean isScrollContains(int x, int y) {
        if(!previewArea.contains(x, y)) {
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
        if (previewArea == null) {
            return false;
        }
        boolean scrollsMoved = false;
        for (Integer key : scrolls.keySet()) {
            Scale scale = preview.getXAxisScale(previewXIndex);
            double value = scale.invert(x);
            scrollsMoved = scrolls.get(key).setValue(value) || scrollsMoved;
        }
        return scrollsMoved;
    }

    public boolean translateScrolls(double dx) {
        Double maxScrollsPosition = null;
        Scale scale = preview.getXAxisScale(previewXIndex);
        for (Integer key : scrolls.keySet()) {
            double scrollPosition = scale.scale(scrolls.get(key).getValue());
            maxScrollsPosition = (maxScrollsPosition == null) ? scrollPosition : Math.max(maxScrollsPosition, scrollPosition);
        }
        return setScrollsPosition(maxScrollsPosition + dx, previewArea.y);
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
            return preview.selectCurve(x, y);
        }
    }


    public Range getXMinMax() {
        return preview.getXMinMax(0);
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
     * =======================Base methods to interact with preview==========================
     **/

    public boolean isPreviewContains(BPoint point) {
        if(point != null && previewArea.contains(point.getX(), point.getY())) {
            return true;
        }
        return false;
    }

    public void addPreviewStack() {
        preview.addStack();
    }

    public void addPreviewStack(int weight) {
        preview.addStack(weight);
    }

    public void addPreviewTrace(Trace trace, boolean isSplit, boolean isXAxisOpposite, boolean isYAxisOpposite) {
        preview.addTrace(trace, isSplit, isXAxisOpposite, isYAxisOpposite);
    }

    public void addPreviewTrace(Trace trace, boolean isSplit) {
        preview.addTrace(trace, isSplit);
    }

    public void addPreviewTrace(int stackNumber, Trace trace, boolean isSplit) {
        preview.addTrace(stackNumber, trace, isSplit);
    }

    public void addPreviewTrace(int stackNumber, Trace trace, boolean isSplit, boolean isXAxisOpposite, boolean isYAxisOpposite) {
        preview.addTrace(stackNumber, trace, isSplit, isXAxisOpposite, isYAxisOpposite);
    }

    public void removePreviewTrace(int traceNumber) {
        preview.removeTrace(traceNumber);
    }

    public int previewTraceCount() {
        return preview.traceCount();
    }


    public int previewYAxisCount() {
        return preview.yAxisCount();
    }

    public int getPreviewYIndex(@Nullable BPoint point) {
        return preview.getYIndex(point);
    }

    public void zoomPreviewY(int yAxisIndex, double zoomFactor) {
        preview.zoomY(yAxisIndex, zoomFactor);
    }

    public void translatePreviewY(int yAxisIndex, int dy) {
        preview.translateY(yAxisIndex, dy);

    }
    public void autoScalePreviewY(int yAxisIndex) {
        preview.autoScaleY(yAxisIndex);
    }
}
