package com.biorecorder.basechart;

import com.biorecorder.basechart.axis.AxisConfig;
import com.biorecorder.basechart.graphics.*;
import com.biorecorder.basechart.scales.LinearScale;
import com.biorecorder.basechart.scales.Scale;
import com.biorecorder.basechart.scroll.Scroll;
import com.biorecorder.basechart.scroll.ScrollConfig;
import com.biorecorder.basechart.scroll.ScrollListener;
import com.biorecorder.basechart.themes.DarkTheme;

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
    private Map<XAxisPosition, Scroll> scrolls = new Hashtable<XAxisPosition, Scroll>(2);
    private List<XAxisPosition> scrollsToAutoscale = new ArrayList<>(2);

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
        if (!config.isAutoScaleEnabled()) {
            isChartAutoscaleNeedDisable = true;
        }
        chart = new Chart(xScale, yScale);
        navigator = new Chart(xScale, yScale);
        chart.setConfig(config.getChartConfig(), true);
        navigator.setConfig(config.getNavigatorConfig(), true);
    }

    private void autoScaleChartY() {
        for (int i = 0; i < chart.stackCount(); i++) {
            chart.autoScaleY(i, YAxisPosition.LEFT);
            chart.autoScaleY(i, YAxisPosition.RIGHT);
        }
    }

    private void autoScaleNavigatorY() {
        for (int i = 0; i < navigator.stackCount(); i++) {
            navigator.autoScaleY(i, YAxisPosition.LEFT);
            navigator.autoScaleY(i, YAxisPosition.RIGHT);
        }
    }

    // navigator have all X axes synchronized (the same min and max)
    private void updateScrollsAndPreview(BCanvas canvas) {
        Range chartDataMinMax = chart.getAllTracesFullMinMax();
        if (chartDataMinMax == null) {
            return;
        }
        double navigatorBestExtent = navigator.getBestExtent(XAxisPosition.BOTTOM, canvas);
        Range navigatorRange = chartDataMinMax;
        if (navigatorBestExtent > chartDataMinMax.length()) {
            navigatorRange = new Range(chartDataMinMax.getMin(), chartDataMinMax.getMin() + navigatorBestExtent);
        }
        XAxisPosition[] xAxisPositions = navigator.getXAxes();
        for (int i = 0; i < xAxisPositions.length; i++) {
            navigator.setXMinMax(xAxisPositions[i], navigatorRange.getMin(), navigatorRange.getMax());
        }

        // create, remove and update scrolls
        xAxisPositions = chart.getXAxes();
        for (int i = 0; i < xAxisPositions.length; i++) {
            Range scrollRange = chartDataMinMax;
            XAxisPosition xAxisPosition = xAxisPositions[i];
            double extent = chart.getBestExtent(xAxisPosition, canvas);
            if (extent > scrollRange.length()) {
                scrollRange = new Range(scrollRange.getMin(), scrollRange.getMin() + extent);
            }
            if (scrolls.get(xAxisPosition) == null && chart.isXAxisVisible(xAxisPosition)) {
                if (extent > 0) {
                    Scroll scroll = new Scroll(scrollRange.getMin(), scrollRange.getMax(), extent);
                    chart.setXMinMax(xAxisPosition, scrollRange.getMin(), scrollRange.getMin() + extent);
                    for (int stack = 0; stack < chart.stackCount(); stack++) {
                        YAxisPosition[] yAxisPositions = chart.getYAxes(stack);
                        for (int j = 0; j < yAxisPositions.length; j++) {
                            chart.autoScaleY(stack, yAxisPositions[stack]);
                        }

                    }
                    scrolls.put(xAxisPosition, scroll);
                    scroll.addListener(new ScrollListener() {
                        @Override
                        public void onScrollChanged(double scrollValue, double scrollExtent) {
                            Range xRange = new Range(scrollValue, scrollValue + scrollExtent);
                            chart.setXMinMax(xAxisPosition, xRange.getMin(), xRange.getMax());
                            isScrollsAtTheEnd = isScrollAtTheEnd(xAxisPosition);
                        }
                    });
                    if (config.isAutoScrollEnabled()) {
                        scrollToEnd();
                    }
                }
            }

            if (scrolls.get(xAxisPosition) != null && !chart.isXAxisVisible(xAxisPosition)) {
                scrolls.remove(xAxisPosition);
            }

            if (scrolls.get(xAxisPosition) != null) {
                scrolls.get(xAxisPosition).setMinMax(scrollRange.getMin(), scrollRange.getMax());
            }
        }
    }


    private boolean scrollToEnd() {
        boolean isMoved = false;
        for (XAxisPosition key : scrolls.keySet()) {
            Scroll scroll = scrolls.get(key);
            if (scroll.setValue(scroll.getMax() - scroll.getExtent())) {
                isMoved = true;
            }
        }
        return isMoved;
    }


    private boolean isScrollAtTheEnd(XAxisPosition xAxisPosition) {
        int gap = 5;
        Scroll scroll = scrolls.get(xAxisPosition);
        double max = scroll.getMax();
        double scrollEnd = scroll.getValue() + scroll.getExtent();
        int max_position = (int) navigatorScale(max);
        int scrollEndPosition = (int) navigatorScale(scrollEnd);
        int distance = max_position - scrollEndPosition;
        if (distance > gap) {
            return false;
        } else {
            return true;
        }
    }

    private Range getScrollTouchRange(Scroll scroll) {
        int scrollStart = (int) navigatorScale(scroll.getValue());
        int scrollEnd = (int) navigatorScale(scroll.getValue() + scroll.getExtent());
        int scrollWidth = scrollEnd - scrollStart;

        int touchRadius = config.getScrollConfig().getTouchRadius();
        if (scrollWidth < 2 * touchRadius) {
            int delta = touchRadius - scrollWidth / 2;
            int touchStart = scrollStart - delta;
            int touchEnd = scrollEnd + delta;
            int touchWidth = touchEnd - touchStart;

            int scrollAreaStart = (int) navigatorScale(scroll.getMin());
            int scrollAreaEnd = (int) navigatorScale(scroll.getMax());
            touchWidth = Math.min(touchWidth, scrollAreaEnd - scrollAreaStart);

            if (touchEnd > scrollAreaEnd) {
                touchEnd = scrollAreaEnd;
                touchStart = touchEnd - touchWidth;
            }
            if (touchStart < scrollAreaStart) {
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
        if (height > gap) {
            height -= gap;
        }

        int navigatorHeight;
        if (navigator.traceCount() == 0) {
            navigatorHeight = Math.min(config.getNavigatorHeightMin(), height / 2);
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
        return navigator.scale(XAxisPosition.BOTTOM, value);
    }

    double navigatorInvert(double value) {
        return navigator.invert(XAxisPosition.BOTTOM, value);
    }

    private void drawScroll(BCanvas canvas, Scroll scroll) {
        BRectangle area = navigator.getGraphArea(canvas);
        ScrollConfig scrollConfig = config.getScrollConfig();

        int borderWidth = scrollConfig.getBorderWidth();

        int scrollStart = (int) navigatorScale(scroll.getValue());
        int scrollEnd = (int) navigatorScale(scroll.getValue() + scroll.getExtent());
        int scrollY = area.y + borderWidth / 2;
        int scrollHeight = area.height - (borderWidth / 2) * 2;
        int scrollWidth = Math.max(1, scrollEnd - scrollStart);

        Range touchRange = getScrollTouchRange(scroll);
        int touchStart = (int) touchRange.getMin();
        int touchWidth = (int) touchRange.length();
        if (touchStart != scrollStart || touchWidth != scrollWidth) {
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
     * =============================================================*
     * Protected method for careful use                            *
     * ==============================================================
     */


    boolean hoverOff() {
        if (chart.hoverOff()) {
            return true;
        }
        if (navigator.hoverOff()) {
            return true;
        }
        return false;
    }

    boolean hoverOn(int x, int y) {
        if (chart.hoverOn(x, y)) {
            return true;
        }
        if (navigator.hoverOn(x, y)) {
            return true;
        }

        return false;
    }

    boolean isChartContains(BPoint point) {
        if (point != null && chartArea.contains(point.getX(), point.getY())) {
            return true;
        }
        return false;
    }

    boolean isScrollContain(int x, int y) {
        if (!navigatorArea.contains(x, y)) {
            return false;
        }

        for (XAxisPosition key : scrolls.keySet()) {
            if (getScrollTouchRange(scrolls.get(key)).contains(x)) {
                return true;
            }
        }
        return false;
    }

    XAxisPosition[] getChartXAxes() {
        return chart.getXAxes();
    }

    YAxisPosition[] getChartYAxes(int stack) {
        return chart.getYAxes(stack);
    }

    int getChartStack(BPoint point) {
        return chart.getStack(point);
    }


    YAxisPosition getChartYAxis(int stack, BPoint point) {
        return chart.getYAxis(stack, point);
    }

    XAxisPosition getChartXAxis(BPoint point) {
        return chart.getXAxis(point);
    }


    int getNavigatorStack(BPoint point) {
        return chart.getStack(point);
    }


    YAxisPosition getNavigatorYAxis(int stack, BPoint point) {
        return navigator.getYAxis(stack, point);
    }

    public boolean isNavigatorTraceSelected() {
        return navigator.isTraceSelected();
    }

    XAxisPosition getNavigatorSelectedTraceX() {
        return navigator.getSelectedTraceX();
    }

    int getNavigatorSelectedTraceStack() {
        return navigator.getSelectedTraceStack();
    }

    YAxisPosition getNavigatorSelectedTraceY() {
        return navigator.getSelectedTraceY();
    }

    public boolean isChartTraceSelected() {
        return chart.isTraceSelected();
    }

    XAxisPosition getChartSelectedTraceX() {
        return chart.getSelectedTraceX();
    }

    int getChartSelectedTraceStack() {
        return chart.getSelectedTraceStack();
    }

    YAxisPosition getChartSelectedTraceY() {
        return chart.getSelectedTraceY();
    }

    XAxisPosition[] getNavigatorXAxes() {
        return navigator.getXAxes();
    }

    YAxisPosition[] getNavigatorYAxes(int stack) {
        return navigator.getYAxes(stack);
    }


    /**==================================================*
     *                Base methods to interact           *
     * ==================================================*/

    public void draw(BCanvas canvas) {
        if (fullArea == null) {
            setArea(canvas.getBounds());
        }
        if (isAreasDirty) {
            int dx = 0;
            if (chartArea != null) {
                dx = fullArea.width - chartArea.width;
            }
            if (dx != 0) {
                for (XAxisPosition key : scrolls.keySet()) {
                    Scroll scroll = scrolls.get(key);
                    double scrollExtentNew = chart.invert(key, fullArea.x + fullArea.width) - chart.invert(key, fullArea.x);
                    scroll.setExtent(scrollExtentNew);
                }
            }
            calculateAndSetAreas();
            isAreasDirty = false;
        }
        if (isScrollsDirty) {
            updateScrollsAndPreview(canvas);
            if (isScrollsAtTheEnd) {
                scrollToEnd();
            }
            isScrollsDirty = false;
        }

        for (int i = 0; i < scrollsToAutoscale.size(); i++) {
            XAxisPosition xAxisPosition = scrollsToAutoscale.get(i);
            Scroll scroll = scrolls.get(xAxisPosition);
            if (scroll != null) {
                scroll.setExtent(chart.getBestExtent(xAxisPosition, canvas));
            }
        }
        scrollsToAutoscale.clear();

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
        for (XAxisPosition key : scrolls.keySet()) {
            drawScroll(canvas, scrolls.get(key));
        }
        if (isChartAutoscaleNeedDisable) {
            for (int stack = 0; stack < chart.stackCount(); stack++) {
                YAxisPosition[] yAxisPositions = chart.getYAxes(stack); 
                for (int i = 0; i < yAxisPositions.length; i++) {
                    YAxisPosition yAxisPosition = yAxisPositions[i];
                    Range minMax = chart.getYMinMax(stack, yAxisPosition, canvas);
                    chart.setYMinMax(stack, yAxisPosition, minMax.getMin(), minMax.getMax());
                    isChartAutoscaleNeedDisable = false;
                } 
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
        if (!config.isAutoScaleEnabled()) {
            isChartAutoscaleNeedDisable = true;
        }
    }

    public void zoomNavigatorY(int stack, YAxisPosition yPosition, double zoomFactor) {
        navigator.zoomY(stack, yPosition, zoomFactor);
    }

    public void translateNavigatorY(int stack, YAxisPosition yPosition, int dy) {
        navigator.translateY(stack, yPosition, dy);

    }

    /**
     * @return true if scrollValue was changed and false if newValue = current scroll value
     */
    public boolean setScrollsValue(double newValue) {
        boolean scrollsMoved = false;
        for (XAxisPosition key : scrolls.keySet()) {
            scrollsMoved = scrolls.get(key).setValue(newValue) || scrollsMoved;
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
        for (XAxisPosition key : scrolls.keySet()) {
            double value = navigatorInvert(x);
            scrollsMoved = scrolls.get(key).setValue(value) || scrollsMoved;
        }
        return scrollsMoved;
    }

    public boolean translateScrolls(double dx) {
        Double maxScrollsPosition = null;
        for (XAxisPosition key : scrolls.keySet()) {
            double scrollPosition = navigatorScale(scrolls.get(key).getValue());
            maxScrollsPosition = (maxScrollsPosition == null) ? scrollPosition : Math.max(maxScrollsPosition, scrollPosition);
        }
        if (maxScrollsPosition != null) {
            return setScrollsPosition(maxScrollsPosition + dx, navigatorArea.y);
        }
        return false;
    }


    public boolean zoomScrollExtent(XAxisPosition xAxisPosition, double zoomFactor) {
        Scroll scroll = scrolls.get(xAxisPosition);
        if (scroll != null) {
            double bestExtent = chart.getTracesBestExtent(xAxisPosition);
            double newExtent = scroll.getExtent() * zoomFactor;
            if (config.getNavigatorMaxZoomFactor() <= 0 || newExtent / bestExtent < config.getNavigatorMaxZoomFactor()) {
                scroll.setExtent(newExtent);
                return true;
            }
        }
        return false;
    }

    public int getWidth() {
        return fullArea.width;
    }

    public double getScrollWidth(XAxisPosition xAxisPosition) throws IllegalArgumentException {
        Scroll scroll = scrolls.get(xAxisPosition);
        if (scroll != null) {
            double scrollStart = navigatorScale(scroll.getValue());
            double scrollEnd = navigatorScale(scroll.getValue() + scroll.getExtent());
            return scrollEnd - scrollStart;
        }
        String errMsg = "No scroll associated with x axis: " + xAxisPosition;
        throw new IllegalArgumentException(errMsg);
    }

    public void setScrollExtent(XAxisPosition xAxisPosition, double extent) {
        Scroll scroll = scrolls.get(xAxisPosition);
        if (scroll != null) {
            scroll.setExtent(extent);
        }
    }

    public void autoScaleScrollExtent(XAxisPosition xAxisPosition) {
        scrollsToAutoscale.add(xAxisPosition);
    }

    public boolean hasScroll(XAxisPosition xAxisPosition) {
        return scrolls.get(xAxisPosition) != null;
    }

    public boolean selectTrace(int x, int y) {
        if (chart.selectTrace(x, y)) {
            return true;
        } else {
            return navigator.selectTrace(x, y);
        }
    }

    public void setTitle(String title) {
        chart.setTitle(title);
    }


    /**
     * =======================Base methods to interact with chart ==========================
     **/
    public int chartStackCount() {
        return chart.stackCount();
    }

    public int navigatorStackCount() {
        return navigator.stackCount();
    }

    public void addChartStack() {
        chart.addStack();
        isAreasDirty = true;
    }

    public void autoScaleChartY(int stack, YAxisPosition yPosition) {
        chart.autoScaleY(stack, yPosition);
        if (!config.isAutoScaleEnabled()) {
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
     * @throws IllegalStateException if stack axis are used by some trace traces and
     *                               therefor can not be deleted
     */
    public void removeChartStack(int stackNumber) throws IllegalStateException {
        chart.removeStack(stackNumber);
        isAreasDirty = true;
    }

    public void addChartTrace(ChartData data, Trace tracePainter) {
        chart.addTraces(data, tracePainter);
        isAreasDirty = true;
        isScrollsDirty = true;
    }

    public void addChartTrace(ChartData data, Trace tracePainter, boolean isSplit) {
        chart.addTraces(data, tracePainter, isSplit);
        isAreasDirty = true;
        isScrollsDirty = true;
    }

    public void addChartTrace(ChartData data, Trace tracePainter, boolean isSplit, XAxisPosition xPosition, YAxisPosition yPosition) {
        chart.addTraces(data,  tracePainter, isSplit, xPosition, yPosition);
        isAreasDirty = true;
        isScrollsDirty = true;
    }

    public void addChartTrace(ChartData data, Trace tracePainter, boolean isSplit, int stackNumber) {
        chart.addTraces(data,  tracePainter, isSplit, stackNumber);
        isAreasDirty = true;
        isScrollsDirty = true;
    }

    public void addChartTrace(ChartData data, Trace tracePainter, boolean isSplit, int stackNumber, XAxisPosition xPosition, YAxisPosition yPosition) {
        chart.addTraces(data,  tracePainter, isSplit, stackNumber, xPosition, yPosition);
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

    public void setChartXTitle(XAxisPosition xPosition, String title) {
        chart.setXTitle(xPosition, title);
    }

    public void setChartYTitle(int stack, YAxisPosition yPosition, String title) {
        chart.setYTitle(stack, yPosition, title);
    }

    public void setChartXScale(XAxisPosition xPosition, Scale scale) {
        chart.setXScale(xPosition, scale);
        scrolls.clear();
        isScrollsDirty = true;
    }

    public void setChartYScale(int stack, YAxisPosition yPosition, Scale scale) {
        chart.setYScale(stack, yPosition, scale);
    }

    public void setChartXConfig(XAxisPosition xPosition, AxisConfig axisConfig) {
        chart.setXConfig(xPosition, axisConfig);
    }

    public void setChartYConfig(int stack, YAxisPosition yPosition, AxisConfig axisConfig) {
        chart.setYConfig(stack, yPosition, axisConfig);
    }

    public AxisConfig getChartXConfig(XAxisPosition xPosition) {
        return chart.getXConfig(xPosition);
    }

    public AxisConfig getChartYConfig(int stack, YAxisPosition yPosition) {
        return chart.getYConfig(stack, yPosition);
    }

    public String[] getChartTraceNames() {
        return chart.getTraceNames();
    }

    public void setChartTraceColor(int trace, BColor color) {
        chart.setTraceColor(trace, color);
    }

    public void setChartTraceName(int trace, String name) {
        chart.setTraceName(trace, name);
    }

    public int getChartTraceNumberByName(String name) {
        return chart.getTraceNumberByName(name);

    }

    public int getChartSelectedTraceNumber() {
        return chart.getSelectedTraceNumber();
    }

    public void setChartYMinMax(int stack, YAxisPosition yPosition, double min, double max) {
        chart.setYMinMax(stack, yPosition, min, max);
    }

    public void zoomChartY(int stack, YAxisPosition yPosition, double zoomFactor) {
        chart.zoomY(stack, yPosition, zoomFactor);
    }

    public void translateChartY(int stack, YAxisPosition yPosition, int dy) {
        chart.translateY(stack, yPosition, dy);
    }


    /**
     * =======================Base methods to interact with navigator==========================
     **/

    public boolean isNavigatorContains(BPoint point) {
        if (point != null && navigatorArea.contains(point.getX(), point.getY())) {
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
     * @throws IllegalStateException if stack axis are used by some trace traces and
     *                               therefor can not be deleted
     */
    public void removeNavigatorStack(int stack) throws IllegalStateException {
        navigator.removeStack(stack);
        isAreasDirty = true;
    }

    public void addNavigatorTrace(ChartData data, Trace tracePainter) {
        navigator.addTraces(data,  tracePainter, true);
        isAreasDirty = true;
        isScrollsDirty = true;
    }

    public void addNavigatorTrace(ChartData data, Trace tracePainter, boolean isSplit) {
        navigator.addTraces(data,  tracePainter, isSplit);
        isAreasDirty = true;
        isScrollsDirty = true;
    }

    public void addNavigatorTrace(ChartData data, Trace tracePainter, boolean isSplit, int stack) {
        navigator.addTraces(data,  tracePainter, isSplit, stack);
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

    public void setNavigatorTraceColor(int trace, BColor color) {
        navigator.setTraceColor(trace, color);
    }

    public void setNavigatorTraceName(int trace, String name) {
        navigator.setTraceName(trace, name);
    }


    public void setNavigatorXTitle(XAxisPosition xPosition, String title) {
        navigator.setXTitle(xPosition, title);
    }

    public void setNavigatorYTitle(int stack, YAxisPosition yPosition, String title) {
        navigator.setYTitle(stack, yPosition, title);
    }

    public void setNavigatorXScale(XAxisPosition xPosition, Scale scale) {
        navigator.setXScale(xPosition, scale);
        scrolls.clear();
        isScrollsDirty = true;
    }

    public void setNavigatorYScale(int stack, YAxisPosition yPosition, Scale scale) {
        navigator.setYScale(stack, yPosition, scale);
    }

    public void setNavigatorXConfig(XAxisPosition xPosition, AxisConfig axisConfig) {
        navigator.setXConfig(xPosition, axisConfig);
    }

    public void setNavigatorYConfig(int stack, YAxisPosition yPosition, AxisConfig axisConfig) {
        navigator.setYConfig(stack, yPosition, axisConfig);
    }

    public AxisConfig getNavigatorXConfig(XAxisPosition xPosition) {
        return navigator.getXConfig(xPosition);
    }

    public AxisConfig getNavigatorYConfig(int stack, YAxisPosition yPosition) {
        return navigator.getYConfig(stack, yPosition);
    }

    public String[] getNavigatorTraceNames() {
        return navigator.getTraceNames();
    }

    public int getNavigatorTraceNumberByName(String name) {
        return navigator.getTraceNumberByName(name);

    }

    public int getNavigatorSelectedTraceNumber() {
        return navigator.getSelectedTraceNumber();
    }

    public void setNavigatorYMinMax(int stack, YAxisPosition yPosition, double min, double max) {
        navigator.setYMinMax(stack, yPosition, min, max);
    }

    public void autoScaleNavigatorY(int stack, YAxisPosition yPosition) {
        navigator.autoScaleY(stack, yPosition);
    }

}
