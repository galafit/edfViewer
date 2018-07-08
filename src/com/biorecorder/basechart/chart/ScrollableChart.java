package com.biorecorder.basechart.chart;

import com.biorecorder.basechart.chart.config.ScrollableChartConfig;
import com.biorecorder.basechart.chart.config.SimpleChartConfig;

import java.util.*;
import java.util.List;


/**
 * Created by galafit on 3/10/17.
 */
public class ScrollableChart {
    private SimpleChart chart;
    private SimpleChart preview;
    private BRectangle chartArea;
    private BRectangle previewArea;
    private Map<Integer, Scroll> scrolls = new Hashtable<Integer, Scroll>(2);
    private ScrollableChartConfig config;

    public ScrollableChart(ScrollableChartConfig config, List<DataSet> chartData, List<DataSet> previewData, BRectangle area) {
        this(config, chartData, previewData, area, new DefaultTraceFactory());
    }


    public ScrollableChart(ScrollableChartConfig config, List<DataSet> chartData, List<DataSet> previewData, BRectangle area, TraceFactory traceFactory) {
        this.config = config;
        SimpleChartConfig chartConfig = config.getChartConfig();
        Set<Integer> scrollableAxis = config.getXAxisWithScroll();
        calculateAreas(area);
        if (config.getScrollsExtents().length == 0) { // Chart without preview
            chart = new SimpleChart(chartConfig,chartData, chartArea, traceFactory);
        } else {
            SimpleChartConfig previewConfig = config.getPreviewConfig();
            chart = new SimpleChart(chartConfig, chartData, chartArea, traceFactory);
            if(config.getPreviewMinMax() == null) {
                Range previewMinMax = null;
                for (Integer xAxisIndex : scrollableAxis) {
                    previewMinMax = Range.max(previewMinMax, chart.getXMinMax(xAxisIndex));
                }
            }

            preview = new SimpleChart(previewConfig, previewData, previewArea, traceFactory);

            for (Integer xAxisIndex : scrollableAxis) {
                Scroll scroll = new Scroll(config.getScrollExtent(xAxisIndex), config.getScrollConfig(), preview.getXAxisScale(0));
                scrolls.put(xAxisIndex, scroll);
                Range scrollRange = new Range(scroll.getValue(), scroll.getValue() + scroll.getExtent());
                chart.setXMinMax(xAxisIndex,  scrollRange);
                scroll.addListener(new ScrollListener() {
                    @Override
                    public void onScrollChanged(double scrollValue, double scrollExtent) {
                        chart.setXMinMax(xAxisIndex, new Range(scrollValue, scrollValue + scrollExtent));
                    }
                });
            }
        }
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
        if (config.getScrollsExtents().length == 0) { // Chart without preview
            chartArea = new BRectangle(area.x + left, area.y + top, width, height);
        } else {
            int gap = config.getGapBetweenChartPreview();
            int chartWeight = config.getChartConfig().getSumWeight();
            int previewWeight = config.getPreviewConfig().getSumWeight();
            int chartHeight = (height - gap) * chartWeight / (chartWeight + previewWeight);
            int previewHeight = (height - gap) - chartHeight;
            chartArea = new BRectangle(area.x + left, area.y + top, width, chartHeight);
            previewArea = new BRectangle(area.x + left, area.y + chartHeight + top + gap, width, previewHeight);
        }
    }

    public void setArea(BRectangle area) {
        calculateAreas(area);
        chart.setArea(chartArea);
        if(preview != null) {
            preview.setArea(previewArea);
        }
    }


    /**
     * =======================Base methods to interact with com.biorecorder.basechart.chart==========================
     **/

    public boolean selectChartTrace(int x, int y) {
        return chart.selectTrace(x, y);
    }

    public boolean selectChartTrace(int traceIndex) {
        return chart.selectTrace(traceIndex);
    }

    public int getChartTraceCounter() {
        return chart.getTraceCounter();
    }

    public boolean chartContains(int x, int y) {
       return chartArea.contains(x, y);
    }

    public void setChartData(List<DataSet> data) {
        chart.setData(data);
    }

    public int getChartSelectedTraceIndex() {
        return chart.getSelectedTraceIndex();
    }

    public RangeInt getChartYStartEnd(int yAxisIndex) {
        return chart.getYStartEnd(yAxisIndex);
    }

    public void setChartYMinMax(int yAxisIndex, Range minMax) {
        chart.setYMinMax(yAxisIndex, minMax);
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
        return chart.getXAxisCounter();
    }

    public int getChartYAxisCounter() {
        return chart.getYAxisCounter();
    }

    public void zoomChartY(int yAxisIndex, float zoomFactor) {
        chart.zoomY(yAxisIndex, zoomFactor);
    }

    public void zoomChartX(int xAxisIndex, float zoomFactor) {
        chart.zoomX(xAxisIndex, zoomFactor);
        if (preview != null) {
            if(chart.getXMinMax(xAxisIndex).length() > preview.getXMinMax(0).length()) {
               chart.setXMinMax(xAxisIndex, preview.getXMinMax(0));
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


    public void setPreviewData(List<DataSet> data) {
        preview.setData(data);
    }

    public boolean selectPreviewTrace(int x, int y) {
        return preview.selectTrace(x, y);
    }

    public boolean selectPreviewTrace(int traceIndex) {
        return preview.selectTrace(traceIndex);
    }

    public int getPreviewTraceCounter() {
        return preview.getTraceCounter();
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
        return preview.getYAxisCounter();
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

    public void setPreviewMinMax(Range minMax) {
        for (int i = 0; i < preview.getXAxisCounter(); i++) {
            preview.setXMinMax(i, minMax);
        }
    }

    public Range getPreviewXMinMax() {
        return preview.getXMinMax(0);
    }

    public RangeInt getPreviewYStartEnd(int yAxisIndex) {
        return preview.getYStartEnd(yAxisIndex);
    }

    public void setPreviewYMinMax(int yAxisIndex, Range minMax) {
        chart.setYMinMax(yAxisIndex, minMax);
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
