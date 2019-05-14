package com.biorecorder.basechart;

import com.biorecorder.basechart.axis.AxisConfig;
import com.biorecorder.basechart.graphics.*;
import com.biorecorder.basechart.scales.LinearScale;
import com.biorecorder.basechart.scales.Scale;
import com.biorecorder.basechart.scroll.Scroll;
import com.biorecorder.basechart.scroll.ScrollConfig;
import com.biorecorder.basechart.scroll.ScrollListener;
import com.biorecorder.basechart.themes.DarkTheme;
import com.sun.istack.internal.Nullable;

import java.util.*;


/**
 * Created by galafit on 3/10/17.
 */
public class NavigableChart {
    private Chart chart;
    private Chart navigator;
    private boolean isScrollsDirty = true;
    private boolean isAreasDirty = true;

    private BRectangle fullArea;
    private BRectangle chartArea;
    private BRectangle navigatorArea;
    private Map<Integer, Scroll> scrolls = new Hashtable<Integer, Scroll>(2);
    private List<Integer> scrollsToAutoscale = new ArrayList<>(2);

    private boolean isScrollsAtTheEnd = true;
    private NavigableChartConfig config;
    private boolean isChartAutoscaleNeedDisable;

    public NavigableChart() {
        this(new LinearScale(), new LinearScale());
    }

    public NavigableChart(Scale xScale) {
        this(xScale, new LinearScale());
    }

    public NavigableChart(Scale xScale, Scale yScale) {
        this.config = new DarkTheme(false).getNavigableChartConfig();
        if(!config.isAutoScaleEnabled()) {
            isChartAutoscaleNeedDisable = true;
        }
        chart = new Chart(xScale, yScale);
        navigator = new Chart(xScale, yScale);
        chart.setConfig(config.getChartConfig(), true);
        navigator.setConfig(config.getNavigatorConfig(), true);
    }

    private void autoScaleChartY() {
        for (int i = 0; i < chart.yAxesCount(); i++) {
            chart.autoScaleY(i);
        }
    }

    private void autoScaleNavigatorY() {
        for (int i = 0; i < navigator.yAxesCount(); i++) {
            navigator.autoScaleY(i);
        }
    }

    // navigator have all X axes synchronized (the same min and max)
    private void updateScrollsAndPreview(BCanvas canvas) {
        Range chartDataMinMax = chart.getAllTracesFullMinMax();
        if(chartDataMinMax == null) {
            return;
        }
        double navigatorBestExtent = navigator.getBestExtent(0, canvas);
        Range navigatorRange = chartDataMinMax;
        if(navigatorBestExtent > chartDataMinMax.length()) {
            navigatorRange = new Range(chartDataMinMax.getMin(), chartDataMinMax.getMin()  + navigatorBestExtent);
        }

        for (int i = 0; i < navigator.xAxesCount(); i++) {
            navigator.setXMinMax(i, navigatorRange.getMin(), navigatorRange.getMax());
        }
        // create, remove and update scrolls
        for (int xIndex = 0; xIndex < chart.xAxesCount(); xIndex++) {
            Range scrollRange = chartDataMinMax;
            double extent = chart.getBestExtent(xIndex, canvas);
            if(extent > scrollRange.length()) {
                scrollRange = new Range(scrollRange.getMin(), scrollRange.getMin() + extent);
            }
            if (scrolls.get(xIndex) == null  && chart.isXAxisVisible(xIndex)) {
                if(extent > 0) {
                    Scroll scroll = new Scroll(scrollRange.getMin(), scrollRange.getMax(), extent);
                    chart.setXMinMax(xIndex, scrollRange.getMin(), scrollRange.getMin() + extent);
                    for (int i = 0; i < chart.yAxesCount(); i++) {
                        chart.autoScaleY(i);
                    }
                    scrolls.put(xIndex, scroll);
                    final int scrollXIndex = xIndex;
                    scroll.addListener(new ScrollListener() {
                        @Override
                        public void onScrollChanged(double scrollValue, double scrollExtent) {
                            Range xRange = new Range(scrollValue, scrollValue + scrollExtent);
                            chart.setXMinMax(scrollXIndex, xRange.getMin(), xRange.getMax());
                            isScrollsAtTheEnd = isScrollAtTheEnd(scrollXIndex);
                        }
                    });
                    if (config.isAutoScrollEnabled()) {
                        scrollToEnd();
                    }
                }
            }

            if (scrolls.get(xIndex) != null  && !chart.isXAxisVisible(xIndex)) {
                scrolls.remove(xIndex);
            }

            if (scrolls.get(xIndex) != null) {
                scrolls.get(xIndex).setMinMax(scrollRange.getMin(), scrollRange.getMax());
            }
        }
    }


    private boolean scrollToEnd() {
        boolean isMoved = false;
        for (Integer xAxisIndex : scrolls.keySet()) {
            Scroll scroll = scrolls.get(xAxisIndex);
            if(scroll.setValue(scroll.getMax() - scroll.getExtent())) {
                isMoved = true;
            }
        }
        return isMoved;
    }


    private boolean isScrollAtTheEnd(int xAxisIndex) {
        int gap = 5;
        Scroll scroll = scrolls.get(xAxisIndex);
        double max = scroll.getMax();
        double scrollEnd = scroll.getValue() + scroll.getExtent();
        int max_position = (int)navigatorScale(max);
        int scrollEndPosition = (int) navigatorScale(scrollEnd);
        int distance = max_position - scrollEndPosition;
        if (distance > gap) {
            return false;
        } else {
            return true;
        }
    }

    private Range getScrollTouchRange(Scroll scroll) {
        int scrollStart = (int)navigatorScale(scroll.getValue());
        int scrollEnd = (int)navigatorScale(scroll.getValue() + scroll.getExtent());
        int scrollWidth = scrollEnd - scrollStart;

        int touchRadius = config.getScrollConfig().getTouchRadius();
        if(scrollWidth  < 2 * touchRadius) {
            int delta = touchRadius - scrollWidth / 2;
            int touchStart = scrollStart - delta;
            int touchEnd = scrollEnd  + delta;
            int touchWidth = touchEnd - touchStart;

            int scrollAreaStart = (int)navigatorScale(scroll.getMin());
            int scrollAreaEnd = (int)navigatorScale(scroll.getMax());
            touchWidth = Math.min(touchWidth, scrollAreaEnd - scrollAreaStart);

            if(touchEnd > scrollAreaEnd) {
                touchEnd = scrollAreaEnd;
                touchStart = touchEnd - touchWidth;
            }
            if(touchStart < scrollAreaStart) {
                touchStart = scrollAreaStart;
                touchEnd = touchStart + touchWidth;
            }

            return new Range(touchStart, touchEnd);
        }

        return new Range(scrollStart, scrollEnd);
    }


    private void calculateAndSetAreas() {
        int top = config.getSpacing().top();
        int bottom = config.getSpacing().bottom();
        int left = config.getSpacing().left();
        int right = config.getSpacing().right();
        int gap = config.getGap();

        int width = fullArea.width - left - right;
        int height = fullArea.height - top - bottom;
        if(height > gap) {
            height -= gap;
        }

        int navigatorHeight;
        if (navigator.traceCount() == 0) {
            navigatorHeight = Math.min(config.getNavigatorHeightMin(), height/2);
        } else {
            int chartWeight = chart.getStacksSumWeight();
            int navigatorWeight = navigator.getStacksSumWeight();
            navigatorHeight = height * navigatorWeight / (chartWeight + navigatorWeight);

        }

        int chartHeight = height - navigatorHeight;

        chartArea = new BRectangle(fullArea.x + left, fullArea.y + top, width, chartHeight);
        navigatorArea = new BRectangle(fullArea.x + left, fullArea.y + fullArea.height - navigatorHeight, width, navigatorHeight);

        chart.setArea(chartArea);
        navigator.setArea(navigatorArea);
    }


    double navigatorScale(double value) {
        return navigator.scale(0, value);
    }

    double navigatorInvert(double value) {
        return navigator.invert(0, value);
    }

    private void drawScroll(BCanvas canvas, Scroll scroll) {
        BRectangle area = navigator.getGraphArea(canvas);
        ScrollConfig scrollConfig = config.getScrollConfig();

        int borderWidth = scrollConfig.getBorderWidth();

        int scrollStart = (int)navigatorScale(scroll.getValue());
        int scrollEnd = (int)navigatorScale(scroll.getValue() + scroll.getExtent());
        int scrollY = area.y + borderWidth/2;
        int scrollHeight = area.height - (borderWidth / 2 ) * 2;
        int scrollWidth = Math.max(1, scrollEnd - scrollStart);

        Range touchRange = getScrollTouchRange(scroll);
        int touchStart = (int) touchRange.getMin();
        int touchWidth = (int) touchRange.length();
        if(touchStart != scrollStart || touchWidth != scrollWidth) {
            canvas.setColor(scrollConfig.getFillColor());
            canvas.fillRect(touchStart, scrollY, touchWidth, scrollHeight);
        } else {
            canvas.setColor(scrollConfig.getFillColor());
            canvas.fillRect(scrollStart, scrollY, scrollWidth, scrollHeight);
        }

        canvas.setColor(scrollConfig.getColor());
        canvas.setStroke(new BStroke(borderWidth));
        canvas.drawRect(scrollStart, scrollY, scrollWidth, scrollHeight);

    }


    /**
     * =======================Base methods to interact==========================
     **/

    public void draw(BCanvas canvas) {
        if(fullArea == null) {
            setArea(canvas.getBounds());
        }
        if(isAreasDirty) {
            int dx = 0;
            if(chartArea != null) {
                dx = fullArea.width - chartArea.width;
            }
            if(dx != 0) {
                for (Integer key : scrolls.keySet()) {
                    Scroll scroll = scrolls.get(key);
                    double scrollExtentNew = chart.invert(key, fullArea.x +fullArea.width) - chart.invert(key, fullArea.x);
                    scroll.setExtent(scrollExtentNew);
                }
            }
            calculateAndSetAreas();
            isAreasDirty = false;
        }
        if(isScrollsDirty) {
            updateScrollsAndPreview(canvas);
            if(isScrollsAtTheEnd) {
                scrollToEnd();
            }
            isScrollsDirty = false;
        }

        if(scrollsToAutoscale.size() > 0) {
            for (int i = 0; i < scrollsToAutoscale.size(); i++) {
                int xIndex = scrollsToAutoscale.get(i);
                Scroll scroll = scrolls.get(xIndex);
                if(scroll != null) {
                    scroll.setExtent(chart.getBestExtent(xIndex, canvas));
                }
            }
            scrollsToAutoscale.clear();
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
            chart.setMargin(chartMargin, canvas);
            navigator.setMargin(previewMargin, canvas);
        }
        navigator.draw(canvas);
        chart.draw(canvas);
        for (Integer key : scrolls.keySet()) {
            drawScroll(canvas, scrolls.get(key));
        }
        if(isChartAutoscaleNeedDisable) {
            for (int i = 0; i < chart.yAxesCount(); i++) {
                Range minMax = chart.getYMinMax(i, canvas);
                chart.setYMinMax(i, minMax.getMin(), minMax.getMax());
                isChartAutoscaleNeedDisable = false;
            }
        }
    }

    public void appendData() {
        isScrollsDirty = true;
        chart.appendData();
        navigator.appendData();
    }

    public void setArea(BRectangle area) {
        fullArea = area;
        isAreasDirty = true;
        isScrollsDirty = true;
    }

    public void setConfig(NavigableChartConfig config) {
        setConfig(config, true);
    }

    public void setConfig(NavigableChartConfig config1, boolean isTraceColorChangeEnabled) {
        this.config = new NavigableChartConfig(config1);
        chart.setConfig(config.getChartConfig(), isTraceColorChangeEnabled);
        navigator.setConfig(config.getNavigatorConfig(), isTraceColorChangeEnabled);
        scrolls.clear();
        isAreasDirty = true;
        isScrollsDirty = true;
        if(!config.isAutoScaleEnabled()) {
            isChartAutoscaleNeedDisable = true;
        }
    }

    public void setChartDataProcessingConfig(DataProcessingConfig dataProcessingConfig) {
        chart.setDataProcessingConfig(dataProcessingConfig);
        scrolls.clear();
        isAreasDirty = true;
        isScrollsDirty = true;
    }

    public void setNavigatorDataProcessingConfig(DataProcessingConfig dataProcessingConfig) {
        navigator.setDataProcessingConfig(dataProcessingConfig);
        scrolls.clear();
        isAreasDirty = true;
        isScrollsDirty = true;
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

    public boolean isScrollContain(int x, int y) {
        if(!navigatorArea.contains(x, y)) {
            return false;
        }

        for (Integer key : scrolls.keySet()) {
            if (getScrollTouchRange(scrolls.get(key)).contains(x)) {
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
            double value = navigatorInvert(x);
            scrollsMoved = scrolls.get(key).setValue(value) || scrollsMoved;
        }
        return scrollsMoved;
    }

    public boolean translateScrolls(double dx) {
        Double maxScrollsPosition = null;
        for (Integer key : scrolls.keySet()) {
            double scrollPosition = navigatorScale(scrolls.get(key).getValue());
            maxScrollsPosition = (maxScrollsPosition == null) ? scrollPosition : Math.max(maxScrollsPosition, scrollPosition);
        }
        if(maxScrollsPosition != null) {
            return setScrollsPosition(maxScrollsPosition + dx, navigatorArea.y);
        }
        return false;
    }


    public void zoomScrollExtent(int xIndex, double zoomFactor) {
        Scroll scroll = scrolls.get(xIndex);
        if (scroll != null) {
            scroll.setExtent(scroll.getExtent() * zoomFactor);
        }
    }

    public int getWidth() {
        return fullArea.width;
    }

    public double getScrollWidth(int xIndex) throws IllegalArgumentException {
        Scroll scroll = scrolls.get(xIndex);
        if (scroll != null) {
            double scrollStart = navigatorScale(scroll.getValue());
            double scrollEnd = navigatorScale(scroll.getValue() + scroll.getExtent());
            return scrollEnd - scrollStart;
        }
        String errMsg = "No scroll associated with x axis: " + xIndex;
        throw  new IllegalArgumentException(errMsg);
    }

    public void setScrollExtent(int xIndex, double extent) {
        Scroll scroll = scrolls.get(xIndex);
        if (scroll != null) {
            scroll.setExtent(extent);
        }
    }

    public boolean hasScroll(int xIndex) {
       return scrolls.get(xIndex) != null;
    }

    public void autoScaleScrollExtent(int xIndex) {
        scrollsToAutoscale.add(xIndex);
    }

    public boolean selectTrace(int x, int y) {
        if(chart.selectCurve(x, y)) {
            return true;
        } else {
            return navigator.selectCurve(x, y);
        }
    }

    public void setTitle(String title) {
        chart.setTitle(title);
    }


    /**
     * =======================Base methods to interact with chart ==========================
     **/

    public void addChartStack() {
        chart.addStack();
        isAreasDirty = true;
    }

    public void autoScaleChartY(int yIndex) {
        chart.autoScaleY(yIndex);
        if(!config.isAutoScaleEnabled()) {
            isChartAutoscaleNeedDisable = true;
        }
    }

    public void addChartStack(int weight) {
        chart.addStack(weight);
        isAreasDirty = true;
    }

    public void setChartStackWeigt(int stack, int weight) {
        chart.setStackWeight(stack, weight);
        isAreasDirty = true;
    }

    /**
     * @throws IllegalStateException if stack axis are used by some trace curves and
     * therefor can not be deleted
     */
    public void removeChartStack(int stackNumber) throws IllegalStateException {
        chart.removeStack(stackNumber);
        isAreasDirty = true;
    }

    public void addChartTrace(Trace trace) {
        chart.addTrace(trace);
        isAreasDirty = true;
        isScrollsDirty = true;
    }

    public void addChartTrace(Trace trace, boolean isSplit) {
        chart.addTrace(trace, isSplit);
        isAreasDirty = true;
        isScrollsDirty = true;
    }

    public void addChartTrace(Trace trace, boolean isSplit, boolean isXAxisOpposite, boolean isYAxisOpposite) {
        chart.addTrace(trace, isSplit, isXAxisOpposite, isYAxisOpposite);
        isAreasDirty = true;
        isScrollsDirty = true;
    }


    public void addChartTrace(int stackNumber, Trace trace, boolean isSplit) {
        chart.addTrace(stackNumber, trace, isSplit);
        isAreasDirty = true;
        isScrollsDirty = true;
    }

    public void addChartTrace(int stackNumber, Trace trace, boolean isSplit, boolean isXAxisOpposite, boolean isYAxisOpposite) {
        chart.addTrace(stackNumber, trace, isSplit, isXAxisOpposite, isYAxisOpposite);
        isAreasDirty = true;
        isScrollsDirty = true;
    }

    public void removeChartTrace(int traceNumber) {
        chart.removeTrace(traceNumber);
        isAreasDirty = true;
        isScrollsDirty = true;
    }

    public int chartTraceCount() {
        return chart.traceCount();
    }

    public void setChartXTitle(int xIndex, String title) {
        chart.setXTitle(xIndex, title);
    }

    public void setChartYTitle(int yIndex, String title) {
        chart.setYTitle(yIndex, title);
    }

    public void setChartXScale(int xIndex, Scale scale) {
        chart.setXScale(xIndex, scale);
        scrolls.clear();
        isScrollsDirty = true;
    }

    public void setChartYScale(int yIndex, Scale scale) {
        chart.setYScale(yIndex, scale);
    }

    public void setChartXConfig(int xIndex, AxisConfig axisConfig) {
        chart.setXConfig(xIndex, axisConfig);
    }

    public void setChartYConfig(int yIndex, AxisConfig axisConfig) {
        chart.setYConfig(yIndex, axisConfig);
    }

    public AxisConfig getChartXConfig(int xIndex) {
        return chart.getXConfig(xIndex);
    }

    public AxisConfig getChartYConfig(int yIndex) {
        return chart.getYConfig(yIndex);
    }

    public String[] getChartCurveNames() {
        return chart.getCurveNames();
    }

    public void setChartCurveColor(int traceNumber, int curveNumber, BColor color) {
        chart.setCurveColor(traceNumber, curveNumber, color);
    }

    public void setChartCurveName(int traceNumber, int curveNumber, String name) {
        chart.setCurveName(traceNumber, curveNumber, name);
    }

    public int chartTraceCurveCount(int traceNumber) {
        return chart.traceCurveCount(traceNumber);
    }

    public CurveNumber getChartCurveNumberByName(String name) {
        return chart.getCurveNumberByName(name);

    }

    public CurveNumber getChartSelectedCurveNumber() {
        return chart.getSelectedCurveNumber();
    }

    public void setChartYMinMax(int yAxisIndex, double min, double max) {
        chart.setYMinMax(yAxisIndex, min, max);
    }

    public int getChartYIndex(@Nullable BPoint point) {
        return chart.getYIndex(point);
    }

    public int getChartXIndex(@Nullable BPoint point) {
        return chart.getXIndex(point);
    }

    public int chartXAxisCount() {
        return chart.xAxesCount();
    }

    public int chartYAxisCount() {
        return chart.yAxesCount();
    }

    public void zoomChartY(int yAxisIndex, double zoomFactor) {
        chart.zoomY(yAxisIndex, zoomFactor);
    }

    public void translateChartY(int yAxisIndex, int dy) {
        chart.translateY(yAxisIndex, dy);
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
        isAreasDirty = true;
    }

    public void addNavigatorStack(int weight) {
        navigator.addStack(weight);
        isAreasDirty = true;
    }

    public void setNavigatorStackWeigt(int stack, int weight) {
        navigator.setStackWeight(stack, weight);
        isAreasDirty = true;
    }

    /**
     * @throws IllegalStateException if stack axis are used by some trace curves and
     * therefor can not be deleted
     */
    public void removeNavigatorStack(int stackNumber) throws IllegalStateException {
        navigator.removeStack(stackNumber);
        isAreasDirty = true;
    }

    public void addNavigatorTrace(Trace trace) {
        navigator.addTrace(trace, true);
        isAreasDirty = true;
        isScrollsDirty = true;
    }

    public void addNavigatorTrace(Trace trace, boolean isSplit) {
        navigator.addTrace(trace, isSplit);
        isAreasDirty = true;
        isScrollsDirty = true;
    }

    public void addNavigatorTrace(int stackNumber, Trace trace, boolean isSplit) {
        navigator.addTrace(stackNumber, trace, isSplit);
        isAreasDirty = true;
        isScrollsDirty = true;
    }

    public void removeNavigatorTrace(int traceNumber) {
        navigator.removeTrace(traceNumber);
        isAreasDirty = true;
        isScrollsDirty = true;
    }

    public int navigatorTraceCount() {
        return navigator.traceCount();
    }

    public void setNavigatorCurveColor(int traceNumber, int curveNumber, BColor color) {
        navigator.setCurveColor(traceNumber, curveNumber, color);
    }

    public void setNavigatorCurveName(int traceNumber, int curveNumber, String name) {
        navigator.setCurveName(traceNumber, curveNumber, name);
    }


    public void setNavigatorXTitle(int xIndex, String title) {
        navigator.setXTitle(xIndex, title);
    }

    public void setNavigatorYTitle(int yIndex, String title) {
        navigator.setYTitle(yIndex, title);
    }

    public void setNavigatorXScale(int xIndex, Scale scale) {
        navigator.setXScale(xIndex, scale);
        scrolls.clear();
        isScrollsDirty = true;
    }

    public void setNavigatorYScale(int yIndex, Scale scale) {
        navigator.setYScale(yIndex, scale);
    }

    public void setNavigatorXConfig(int xIndex, AxisConfig axisConfig) {
        navigator.setXConfig(xIndex, axisConfig);
    }

    public void setNavigatorYConfig(int yIndex, AxisConfig axisConfig) {
        navigator.setYConfig(yIndex, axisConfig);
    }

    public AxisConfig getNavigatorXConfig(int xIndex) {
        return navigator.getXConfig(xIndex);
    }

    public AxisConfig getNavigatorYConfig(int yIndex) {
        return navigator.getYConfig(yIndex);
    }

    public String[] getNavigatorCurveNames() {
        return navigator.getCurveNames();
    }

    public int navigatorTraceCurveCount(int traceNumber) {
        return navigator.traceCurveCount(traceNumber);
    }

    public CurveNumber getNavigatorCurveNumberByName(String name) {
        return navigator.getCurveNumberByName(name);

    }

    public CurveNumber getNavigatorSelectedCurveNumber() {
        return navigator.getSelectedCurveNumber();
    }

    public void setNavigatorYMinMax(int yAxisIndex, double min, double max) {
        navigator.setYMinMax(yAxisIndex, min, max);
    }

    public int navigatorYAxisCount() {
        return navigator.yAxesCount();
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
