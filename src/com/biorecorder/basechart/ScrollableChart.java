package com.biorecorder.basechart;

import com.biorecorder.basechart.config.ChartConfig;
import com.biorecorder.basechart.config.SimpleChartConfig;
import com.biorecorder.basechart.graphics.BCanvas;
import com.biorecorder.basechart.graphics.BRectangle;

import java.util.*;


/**
 * Created by galafit on 3/10/17.
 */
public class ScrollableChart {
    private SimpleChart chart;
    private SimpleChart preview;
    private BRectangle chartArea;
    private BRectangle previewArea;
    private Map<Integer, Scroll> scrolls = new Hashtable<Integer, Scroll>(2);
    private ChartConfig config;
    private boolean scrollsAtTheEnd = true;

    public ScrollableChart(ChartConfig config, BRectangle area) {
        this(config, area, new DefaultTraceFactory());
    }

    public ScrollableChart(ChartConfig config,  BRectangle area, TraceFactory traceFactory) {
        this.config = config;
        calculateAreas(area);
        chart = new SimpleChart(config.getBaseChartConfig(), chartArea, traceFactory);
        if (config.isPreviewEnabled()) {
            SimpleChartConfig previewConfig = config.getPreviewConfig();

            Range previewMinMax = chart.getOriginalDataMinMax();
            for (int xAxisIndex = 0; xAxisIndex < chart.xAxisCount(); xAxisIndex++) {
                previewMinMax = Range.join(previewMinMax, chart.getXMinMax(xAxisIndex));
            }
            if(previewMinMax != null) {
                previewConfig.setXMinMax(0, previewMinMax.getMin(), previewMinMax.getMax());
            }

            preview = new SimpleChart(previewConfig, previewArea, traceFactory);

            for (int xAxisIndex = 0; xAxisIndex < chart.xAxisCount(); xAxisIndex++) {
                if(chart.isXAxisUsed(xAxisIndex)) {
                   double axisExtent = chart.getXMinMax(xAxisIndex).length();
                    Scroll scroll = new Scroll(axisExtent, config.getScrollConfig(), preview.getXAxisScale(0));
                    scrolls.put(xAxisIndex, scroll);
                    final int scrollXIndex = xAxisIndex;
                    scroll.addListener(new ScrollListener() {
                        @Override
                        public void onScrollChanged(double scrollValue, double scrollExtent) {
                            Range xRange = new Range(scrollValue, scrollValue + scrollExtent);
                            chart.setXMinMax(scrollXIndex, xRange.getMin(), xRange.getMax());
                            scrollsAtTheEnd = isScrollAtTheEnd(scrollXIndex);
                            if(config.isAutoScaleEnableDuringScroll()) {
                                autoScaleChartY();
                            }
                        }
                    });
                }
            }
        }
        if(config.isAutoScrollEnable()) {
         //   scrollToEnd();
        }
    }

    private void autoScaleChartY() {
        for (int i = 0; i < getChartYAxisCounter(); i++) {
            autoScaleChartY(i);
        }
    }

    private void updatePreviewMinMax() {
        Range previewMinMax = preview.getXMinMax(0);
        Range chartMinMax = chart.getOriginalDataMinMax();
        for (int xAxisIndex = 0; xAxisIndex < chart.xAxisCount(); xAxisIndex++) {
            chartMinMax = Range.join(chartMinMax, chart.getXMinMax(xAxisIndex));
        }
        if(! previewMinMax.contains(chartMinMax)) {
            previewMinMax = Range.join(previewMinMax, chartMinMax);
            preview.setXMinMax(0, previewMinMax.getMin(), previewMinMax.getMax());
        }
    }

    private boolean scrollToEnd() {
        Range dataMinMax = chart.getOriginalDataMinMax();
        double minExtent = 0;

        for (Integer xAxisIndex : scrolls.keySet()) {
            double scrollExtent = scrolls.get(xAxisIndex).getExtent();
            minExtent = (minExtent == 0) ? scrollExtent : Math.min(minExtent, scrollExtent);
        }
        return setScrollsValue(dataMinMax.getMax() - minExtent);
    }



    private boolean isScrollAtTheEnd(int xAxisIndex) {
        Range dataRange = chart.getOriginalDataMinMax();
        if(dataRange != null) {
            double dataMax = dataRange.getMax();
            double scrollEnd = scrolls.get(xAxisIndex).getValue() + scrolls.get(xAxisIndex).getExtent();
            if (dataMax - scrollEnd > 0) {
                return false;
            }  else {
                return true;
            }
        }
        return false;
    }

    public void draw(BCanvas canvas) {
        if(preview != null) {
            Margin chartMargin = chart.getMargin(canvas);
            Margin previewMargin = preview.getMargin(canvas);
            if (chartMargin.left() != previewMargin.left() || chartMargin.right() != previewMargin.right()) {
                int left = Math.max(chartMargin.left(), previewMargin.left());
                int right = Math.max(chartMargin.right(), previewMargin.right());
                chartMargin = new Margin(chartMargin.top(), right, chartMargin.bottom(), left);
                previewMargin = new Margin(previewMargin.top(), right, previewMargin.bottom(), left);
                chart.setMargin(canvas, chartMargin);
                preview.setMargin(canvas, previewMargin);
            }
            preview.draw(canvas);
            for (Integer key : scrolls.keySet()) {
                scrolls.get(key).draw(canvas, preview.getGraphArea(canvas));
            }
        }
        chart.draw(canvas);
    }

    private void calculateAreas(BRectangle area) {
        int top = 0;
        int bottom = 0;
        int left = 0;
        int right = 0;
        Margin margin = config.getMargin();
        if(margin != null) {
           top = margin.top();
           bottom = margin.bottom();
           left = margin.left();
           right = margin.right();
        }
        int width = area.width - left - right;
        int height = area.height -  top - bottom;
        if (config.isPreviewEnabled()) {
            int gap = config.getGapBetweenChartPreview();
            int chartWeight = config.getBaseChartConfig().getSumWeight();
            int previewWeight = config.getPreviewConfig().getSumWeight();
            int chartHeight = (height - gap) * chartWeight / (chartWeight + previewWeight);
            int previewHeight = (height - gap) - chartHeight;
            chartArea = new BRectangle(area.x + left, area.y + top, width, chartHeight);
            previewArea = new BRectangle(area.x + left, area.y + chartHeight + top + gap, width, previewHeight);
        } else {
            chartArea = new BRectangle(area.x + left, area.y + top, width, height);
        }
    }



    /**
     * =======================Base methods to interact with com.biorecorder.basechart.chart==========================
     **/
    public void setArea(BRectangle area) {
        calculateAreas(area);
        chart.setArea(chartArea);
        if(preview != null) {
            preview.setArea(previewArea);
        }
    }

    public void update() {
        updatePreviewMinMax();
        if(config.isAutoScrollEnable() && scrollsAtTheEnd) {
            scrollToEnd();
        }
    }

    public boolean selectChartTrace(int x, int y) {
        return chart.selectTrace(x, y);
    }

    public boolean selectChartTrace(int traceIndex) {
        return chart.selectTrace(traceIndex);
    }

    public int getChartTraceCounter() {
        return chart.traceCount();
    }

    public boolean chartContains(int x, int y) {
       return chartArea.contains(x, y);
    }


    public int getChartSelectedTraceIndex() {
        return chart.getSelectedTraceIndex();
    }

    public RangeInt getChartYStartEnd(int yAxisIndex) {
        return chart.getYStartEnd(yAxisIndex);
    }

    public void setChartYMinMax(int yAxisIndex, double min, double max) {
        chart.setYMinMax(yAxisIndex, min, max);
    }

    public Range getChartYMinMax(int yAxisIndex) {
        return chart.getYMinMax(yAxisIndex);
    }

    public int getChartTraceYIndex(int traceIndex) {
        return chart.getTraceYIndex(traceIndex);
    }

    public int getChartTraceXIndex(int traceIndex) {
        return chart.getTraceXIndex(traceIndex);
    }

    public int getChartYIndex(int x, int y) {
        return chart.getYIndex(x, y);
    }

    public int getChartXIndex(int x, int y) {
        return chart.getXIndex(x, y);
    }

    public int getChartXAxisCounter() {
        return chart.xAxisCount();
    }

    public int getChartYAxisCounter() {
        return chart.yAxisCount();
    }

    public void zoomChartY(int yAxisIndex, float zoomFactor) {
        chart.zoomY(yAxisIndex, zoomFactor);
    }

    public void zoomChartX(int xAxisIndex, float zoomFactor) {
        chart.zoomX(xAxisIndex, zoomFactor);
        if (preview != null) {
            if(chart.getXMinMax(xAxisIndex).length() > preview.getXMinMax(0).length()) {
                Range previewXMinMax = preview.getXMinMax(0);
                chart.setXMinMax(xAxisIndex, previewXMinMax.getMin(), previewXMinMax.getMax());
            }
            scrolls.get(xAxisIndex).setExtent(chart.getXMinMax(xAxisIndex).length());
            setScrollsValue(scrolls.get(xAxisIndex).getValue());
        }
    }

    public void translateChartY(int yAxisIndex, int dy) {
        chart.translateY(yAxisIndex, dy);
    }

    public void translateChartX(int xAxisIndex, int dx) {
        if (preview == null) {
            chart.translateX(xAxisIndex, dx);
        } else {
            if(scrolls.get(xAxisIndex) != null) {
                float scrollTranslation = dx * scrolls.get(xAxisIndex).getWidth() / chartArea.width;
                translateScrolls(scrollTranslation);
            }
         }
    }

    public void autoScaleChartX(int xAxisIndex) {
        if (preview == null) {
            chart.autoScaleX(xAxisIndex);
        }
    }

    public void autoScaleChartY(int yAxisIndex) {
        chart.autoScaleY(yAxisIndex);
    }

    public boolean chartHoverOff() {
        return chart.hoverOff();
    }

    public boolean chartHoverOn(int x, int y, int traceIndex) {
        return chart.hoverOn(x, y, traceIndex);
    }

    public boolean isPointInsideChart(int x, int y) {
        return chartArea.contains(x, y);
    }

    /**
     * =======================Base methods to interact with preview==========================
     **/

    public boolean selectPreviewTrace(int x, int y) {
        return preview.selectTrace(x, y);
    }

    public boolean selectPreviewTrace(int traceIndex) {
        return preview.selectTrace(traceIndex);
    }

    public int getPreviewTraceCounter() {
        return preview.traceCount();
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
    public boolean setScrollsPosition(float x, float y) {
        if (previewArea == null) {
            return false;
        }
        boolean scrollsMoved = false;
        for (Integer key : scrolls.keySet()) {
            scrollsMoved = scrolls.get(key).setPosition(x) || scrollsMoved;
        }
        return scrollsMoved;
    }

    public boolean translateScrolls(float dx) {
        Float maxScrollsPosition = null;
        for (Integer key : scrolls.keySet()) {
            maxScrollsPosition = (maxScrollsPosition == null) ? scrolls.get(key).getPosition() : Math.max(maxScrollsPosition, scrolls.get(key).getPosition());
        }
        return setScrollsPosition(maxScrollsPosition + dx, previewArea.y);
    }


    public Set<Integer> getXAxisWithScroll() {
        return scrolls.keySet();
    }

    public void addScrollListener(int xAxisIndex, ScrollListener listener) {
        scrolls.get(xAxisIndex).addListener(listener);
    }

    public double getScrollExtent(int xAxisIndex) {
        return scrolls.get(xAxisIndex).getExtent();
    }

    public double getScrollValue(int xAxisIndex) {
        return scrolls.get(xAxisIndex).getValue();
    }

    public int getPreviewYAxisCounter() {
        return preview.yAxisCount();
    }

    public boolean isPointInsidePreview(int x, int y) {
        return previewArea.contains(x, y);
    }


    public boolean isPointInsideScroll(int x, int y) {
        if(!isPointInsidePreview(x, y)) {
           return false;
        }
        for (Integer key : scrolls.keySet()) {
            if(scrolls.get(key).isPointInsideScroll(x)) {
                return true;
            }
        }
        return false;
    }


    public boolean previewContains(int x, int y) {
        if (previewArea != null) {
            return previewArea.contains(x, y);
        }
        return false;
    }

    public int getPreviewSelectedTraceIndex() {
        if(preview != null) {
            return preview.getSelectedTraceIndex();
        }
        return -1;
    }

    public void updatePreviewMinMax(double min, double max) {
        for (int i = 0; i < preview.xAxisCount(); i++) {
            preview.setXMinMax(i, min, max);
        }
    }

    public Range getPreviewXMinMax() {
        return preview.getXMinMax(0);
    }

    public RangeInt getPreviewYStartEnd(int yAxisIndex) {
        return preview.getYStartEnd(yAxisIndex);
    }

    public void setPreviewYMinMax(int yAxisIndex, double min, double max) {
        chart.setYMinMax(yAxisIndex, min, max);
    }

    public Range getPreviewYMinMax(int yAxisIndex) {
        return chart.getYMinMax(yAxisIndex);
    }

    public int getPreviewTraceYIndex(int traceIndex) {
        return preview.getTraceYIndex(traceIndex);
    }

    public int getPreviewYIndex(int x, int y) {
        if(preview != null) {
            return preview.getYIndex(x, y);
        }
        return -1;
    }


    public void zoomPreviewY(int yAxisIndex, float zoomFactor) {
        if(preview != null) {
            preview.zoomY(yAxisIndex, zoomFactor);
        }
    }

    public void translatePreviewY(int yAxisIndex, int dy) {
        if(preview != null) {
            preview.translateY(yAxisIndex, dy);
        }

    }

    public void autoScalePreviewY(int yAxisIndex) {
        if(preview != null) {
            preview.autoScaleY(yAxisIndex);
        }
    }

    public boolean previewHoverOff() {
        return preview.hoverOff();
    }

    public boolean previewHoverOn(int x, int y, int traceIndex) {
        return preview.hoverOn(x, y, traceIndex);
    }

}
