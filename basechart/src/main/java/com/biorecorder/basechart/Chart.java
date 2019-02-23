package com.biorecorder.basechart;

import com.biorecorder.basechart.axis.*;
import com.biorecorder.basechart.button.StateListener;
import com.biorecorder.basechart.graphics.*;
import com.biorecorder.basechart.scales.LinearScale;
import com.biorecorder.basechart.scales.Scale;
import com.sun.istack.internal.Nullable;

import java.util.*;
import java.util.List;

/**
 * Created by hdablin on 24.03.17.
 */
public class Chart {
    private String title;

    private boolean isLegendVisible = true;
    private ChartConfig chartConfig = new ChartConfig();

    /*
 * 2 X-axis: 0(even) - BOTTOM and 1(odd) - TOP
 * 2 Y-axis for every section(stack): even - LEFT and odd - RIGHT;
 * All LEFT and RIGHT Y-axis are stacked.
 * If there is no trace associated with some axis... this axis is invisible.
 **/
    private List<AxisWrapper> xAxisList = new ArrayList<>(2);
    private List<AxisWrapper> yAxisList = new ArrayList<>();

    private ArrayList<Integer> stackWeights = new ArrayList<Integer>();
    private List<Trace> traces = new ArrayList<Trace>();
    private Legend legend;
    private Title titleText;
    private BRectangle fullArea;
    private BRectangle graphArea;
    private Insets margin;

    private Crosshair crosshair;
    private Tooltip tooltip;

    private TraceCurve selectedCurve;
    private TraceCurvePoint hoverPoint;
    private DataProcessingConfig dataProcessingConfig;

    public Chart() {
       this(new ChartConfig());
    }

    public Chart(ChartConfig chartConfig) {
        this(chartConfig, null);
    }

    public Chart(ChartConfig chartConfig1, @Nullable DataProcessingConfig dataProcessingConfig) {
        this.dataProcessingConfig = dataProcessingConfig;
        this.chartConfig = new ChartConfig(chartConfig1);

        AxisWrapper bottomAxis = new AxisWrapper(new AxisBottom(new LinearScale(), chartConfig.getBottomAxisConfig()));
        AxisWrapper topAxis = new AxisWrapper(new AxisTop(new LinearScale(), chartConfig.getTopAxisConfig()));
        bottomAxis.setRoundingEnabled(chartConfig.isXAxisRoundingEnabled());
        topAxis.setRoundingEnabled(chartConfig.isXAxisRoundingEnabled());
        if (chartConfig.isXAxisRoundingEnabled()) {
            bottomAxis.setRoundingAccuracyPct(chartConfig.getAxisRoundingAccuracyPctIfRoundingEnabled());
            topAxis.setRoundingAccuracyPct(chartConfig.getAxisRoundingAccuracyPctIfRoundingEnabled());
        } else {
            bottomAxis.setRoundingAccuracyPct(chartConfig.getAxisRoundingAccuracyPctIfRoundingDisabled());
            topAxis.setRoundingAccuracyPct(chartConfig.getAxisRoundingAccuracyPctIfRoundingDisabled());
        }

        xAxisList.add(bottomAxis);
        xAxisList.add(topAxis);

        //legend
        legend = new Legend(chartConfig.getLegendConfig());
    }

    void setMargin(Insets margin) {
        this.margin = margin;
        graphArea = new BRectangle(fullArea.x + margin.left(), fullArea.y + margin.top(),
                fullArea.width - margin.left() - margin.right(), fullArea.height - margin.top() - margin.bottom());
        if (legend.isAttachedToStacks()) {
            legend.setArea(graphArea);
        }
        setYStartEnd(graphArea.y, graphArea.height);
        setXStartEnd(graphArea.x, graphArea.width);
    }

    double getBestExtent(int xIndex) {
        double maxExtent = 0;
        for (Trace trace : traces) {
            if (trace.getXScale() == xAxisList.get(xIndex).getScale()) {
                maxExtent = Math.max(maxExtent, trace.getBestExtent(fullArea.width));
            }
        }
        return maxExtent;
    }


    // for all x axis
    BRange getAllTracesFullMinMax() {
        BRange minMax = null;
        for (int i = 0; i < traces.size(); i++) {
            minMax = BRange.join(minMax, traces.get(i).getFullXMinMax());
        }
        return minMax;
    }


    /**
     * 2 Y-axis for every section(stack): even - LEFT and odd - RIGHT;
     */
    private boolean isYAxisLeft(int axisIndex) {
        if ((axisIndex & 1) == 0) { // even (left)
            return true;
        }
        //odd (right)
        return false;
    }

    /**
     * X-axis: 0(even) - BOTTOM and 1(odd) - TOP
     */
    private boolean isXAxisBottom(int axisIndex) {
        if ((axisIndex & 1) == 0) { // even (bottom)
            return true;
        }
        //odd (top)
        return false;
    }

    private void setAreasDirty() {
        if(chartConfig.getMargin() == null) { // if margin is not fixed
            margin = null;
        }
    }

    private boolean isAreasDirty() {
        if (margin == null || graphArea == null) {
            return true;
        }
        return false;
    }

    Insets getMargin(BCanvas canvas) {
        if (margin == null) {
            calculateMarginsAndAreas(canvas);
        }
        return margin;
    }

    BRectangle getGraphArea(BCanvas canvas) {
        if (graphArea == null) {
            calculateMarginsAndAreas(canvas);
        }
        return graphArea;
    }

    Scale getXAxisScale(int xIndex) {
        return xAxisList.get(xIndex).getScale();
    }

    private Insets calculateSpacing() {
        if(chartConfig.getSpacing() != null) {
            return chartConfig.getSpacing();
        }
        int minSpacing = 1;
        int spacingTop = minSpacing;
        int spacingBottom = minSpacing;
        int spacingLeft = minSpacing;
        int spacingRight = minSpacing;
        for (int i = 0; i < yAxisList.size(); i++) {
           AxisWrapper axis = yAxisList.get(i);
           if(i % 2 == 0) { // left
              if(axis.isVisible() && axis.isTickLabelOutside()) {
                 spacingLeft = chartConfig.getAutoSpacing();
              }
           } else { // right
               if(axis.isVisible() && axis.isTickLabelOutside()) {
                   spacingRight = chartConfig.getAutoSpacing();
               }
           }
        }

        for (int i = 0; i < xAxisList.size(); i++) {
            AxisWrapper axis = xAxisList.get(i);
            if(i % 2 == 0) { // bottom
                if(axis.isVisible() && axis.isTickLabelOutside()) {
                    spacingBottom = chartConfig.getAutoSpacing();
                }
            } else { // top
                if(axis.isVisible() && axis.isTickLabelOutside()) {
                    spacingTop = chartConfig.getAutoSpacing();
                }
            }
        }

        if(title != null){
            spacingTop = 0;
        }
        if(!legend.isAttachedToStacks()) {
            if(legend.isTop()) {
                spacingTop = 0;
            } else if (legend.isBottom()) {
                spacingBottom = 0;
            }
        }
        return new Insets(spacingTop, spacingRight, spacingBottom, spacingLeft);
    }

    void calculateMarginsAndAreas(BCanvas canvas) {
        if(chartConfig.getMargin() != null) { // fixed margin
            return;
        }
        if(fullArea.width == 0 || fullArea.height == 0) {
            graphArea = fullArea;
            margin = new Insets(0);
            return;
        }

        if (titleText == null) {
            titleText = new Title(title, chartConfig.getTitleConfig(), fullArea, canvas);
        }
        Insets spacing = calculateSpacing();
        int titleHeight = titleText.getBounds().height;

        if(graphArea == null) {
            graphArea = fullArea;
        }
        setXStartEnd(graphArea.x, graphArea.width);

        int top = spacing.top() + titleHeight + xAxisList.get(1).getWidth(canvas);
        int bottom = spacing.bottom() + xAxisList.get(0).getWidth(canvas);

        int legendHeight = 0;
        if (!legend.isAttachedToStacks()) {
            BRectangle legendArea = new BRectangle(fullArea.x + spacing.left(), fullArea.y + titleHeight + spacing.top(), fullArea.width - spacing.left() - spacing.right(), fullArea.height - titleHeight - spacing.top() - spacing.bottom());
            legend.setArea(legendArea);
            legendHeight = legend.getHeight(canvas);
            if (legend.isTop()) {
                top += legendHeight;
            }
            if (legend.isBottom()) {
                bottom += legendHeight;
            }
        }

        setYStartEnd(fullArea.y + top, fullArea.height - top - bottom);

        int left = 0;
        int right = 0;
        for (int i = 0; i < yAxisList.size(); i++) {
            if (i % 2 == 0) {
                left = Math.max(left, yAxisList.get(i).getWidth(canvas));
            } else {
                right = Math.max(right, yAxisList.get(i).getWidth(canvas));
            }
        }

        left += spacing.left();
        right += spacing.right();

        // adjust XAxis ranges
        setXStartEnd(fullArea.x + left, fullArea.width - left - right);
        int topNew = spacing.top() + titleHeight + xAxisList.get(1).getWidth(canvas);
        int bottomNew = spacing.bottom() + xAxisList.get(0).getWidth(canvas);
        if (!legend.isAttachedToStacks()) {
            if (legend.isTop()) {
                topNew += legendHeight;
            }
            if (legend.isBottom()) {
                bottomNew += legendHeight;
            }
        }
        if (topNew != top || bottomNew != bottom) {
            // adjust YAxis ranges
            setYStartEnd(fullArea.y + top, fullArea.height - top - bottom);
            top = topNew;
            bottom = bottomNew;
        }

        margin = new Insets(top, right, bottom, left);
        graphArea = new BRectangle(fullArea.x + left, fullArea.y + top,
                fullArea.width - left - right, fullArea.height - top - bottom);

        if (legend.isAttachedToStacks()) {
            legend.setArea(graphArea);
        }
    }

    public int getStacksSumWeight() {
        int weightSum = 0;
        for (Integer weight : stackWeights) {
            weightSum += weight;
        }
        return weightSum;
    }


    private void setXStartEnd(int areaX, int areaWidth) {
        for (AxisWrapper axis : xAxisList) {
            axis.setStartEnd(areaX, areaX + areaWidth);
        }
    }

    private void setYStartEnd(int areaY, int areaHeight) {
        int weightSum = getStacksSumWeight();

        int weightSumTillYAxis = 0;
        int stackCount = yAxisList.size() / 2;

        for (int stack = 0; stack < stackCount; stack++) {
            int yAxisWeight = stackWeights.get(stack);
            double axisHeight = areaHeight * yAxisWeight / weightSum;
            int end = areaY + areaHeight * weightSumTillYAxis / weightSum;
            int start = end + (int)Math.round(axisHeight);

            if(stack == stackCount - 1) {
                // for integer calculation sum yAxis length can be != areaHeight
                // so we fix that
                start = areaY + areaHeight;
            }
            yAxisList.get(stack * 2).setStartEnd(start, end);
            yAxisList.get(stack * 2 + 1).setStartEnd(start, end);

            weightSumTillYAxis += stackWeights.get(stack);
        }
    }

    boolean isXAxisVisible(int xIndex) {
        if(xAxisList.get(xIndex).isVisible()) {
            return true;
        }
        return false;
    }

    private int chooseXAxisWithGrid(int stackNumber) {
        int bottomAxisIndex = 0;
        int topAxisIndex = 1;

        AxisWrapper leftAxis = yAxisList.get(stackNumber * 2);
        AxisWrapper rightAxis = yAxisList.get(stackNumber * 2 + 1);
        int primaryAxisIndex;
        if(chartConfig.isBottomAxisPrimary()) {
           primaryAxisIndex = bottomAxisIndex;
        } else {
            primaryAxisIndex = topAxisIndex;
        }
        AxisWrapper primaryAxis = xAxisList.get(primaryAxisIndex);
        for (Trace trace : traces) {
            if(trace.getXScale() == primaryAxis.getScale()) {
                Scale[] traceYScales = trace.getYScales();
                for (Scale yScale : traceYScales) {
                    if(yScale == leftAxis.getScale() || yScale == rightAxis.getScale()) {
                        return primaryAxisIndex;
                    }
                }
            }
        }

        if(chartConfig.isBottomAxisPrimary()) {
            return topAxisIndex;
        } else {
            return bottomAxisIndex;
        }
    }


    public void draw(BCanvas canvas) {
        if(fullArea == null) {
            setArea(canvas.getBounds());
        }
        if(fullArea.width == 0 || fullArea.height == 0) {
            return;
        }

        if (titleText == null) {
            titleText = new Title(title, chartConfig.getTitleConfig(), fullArea, canvas);
        }

        if (isAreasDirty()) {
            calculateMarginsAndAreas(canvas);
        }

        canvas.setColor(chartConfig.getMarginColor());
        canvas.fillRect(fullArea.x, fullArea.y, fullArea.width, fullArea.height);

        canvas.setColor(chartConfig.getBackgroundColor());
        canvas.fillRect(graphArea.x, graphArea.y, graphArea.width, graphArea.height);

        canvas.enableAntiAliasAndHinting();

        /*
         * Attention!!!
         * Drawing  axis and grids should be done before drawing traces
         * because this methods invokes axis rounding
         * First we should draw all grids and only after that axes
         * (otherwise the grid will draw over the axes)
         */
        int stackCount = yAxisList.size() / 2;

        // draw X axes grids
        AxisWrapper bottomAxis = xAxisList.get(0);
        AxisWrapper topAxis = xAxisList.get(1);

        if(bottomAxis.isVisible() && !topAxis.isVisible()) {
            bottomAxis.drawGrid(canvas, graphArea);
        } else if(!bottomAxis.isVisible() && topAxis.isVisible()) {
            topAxis.drawGrid(canvas, graphArea);
        } else if(bottomAxis.isVisible() && topAxis.isVisible()) {
            // draw separately for every stack
            for (int i = 0; i < stackCount; i++) {
                AxisWrapper yAxis = yAxisList.get(2 * i);
                BRectangle stackArea = new BRectangle(graphArea.x, yAxis.getEnd(), graphArea.width, yAxis.length());
                xAxisList.get(chooseXAxisWithGrid(i)).drawGrid(canvas, stackArea);
            }
        }
        // draw Y axes grids
        for (int i = 0; i < stackCount; i++) {
            AxisWrapper leftAxis = yAxisList.get(i * 2);
            AxisWrapper rightAxis = yAxisList.get(i * 2 + 1);

            if(rightAxis.isVisible() && !leftAxis.isVisible()) {
                rightAxis.drawGrid(canvas, graphArea);
            } else if(!rightAxis.isVisible() && leftAxis.isVisible()) {
                leftAxis.drawGrid(canvas, graphArea);
            } else if(rightAxis.isVisible() && leftAxis.isVisible()) {
                if(chartConfig.isLeftAxisPrimary()) {
                    leftAxis.drawGrid(canvas, graphArea);
                } else {
                    rightAxis.drawGrid(canvas, graphArea);
                }
            }
        }

        // draw X axes
        bottomAxis.drawAxis(canvas, graphArea);
        topAxis.drawAxis(canvas, graphArea);

        // draw Y axes
        for (AxisWrapper axis : yAxisList) {
            axis.drawAxis(canvas, graphArea);
        }


        canvas.save();
        canvas.setClip(graphArea.x, graphArea.y, graphArea.width, graphArea.height);

        for (Trace trace : traces) {
            trace.draw(canvas);
        }
        canvas.restore();

        titleText.draw(canvas);

        if (isLegendVisible) {
            legend.draw(canvas);
        }

        if (hoverPoint != null) {
            crosshair.draw(canvas, graphArea);
            tooltip.draw(canvas, fullArea);
        }
    }

    private int getCurveXIndex(Trace trace) {
        for (int i = 0; i < xAxisList.size(); i++) {
            if (xAxisList.get(i).getScale() == trace.getXScale()) {
                return i;
            }
        }
        return -1;
    }

    private int getCurveYIndex(Trace trace, int curveNumber) {
        for (int i = 0; i < yAxisList.size(); i++) {
            if (yAxisList.get(i).getScale() == trace.getYScale(curveNumber)) {
                return i;
            }
        }
        return -1;
    }

    private void checkStackNumber(int stack) {
        int stackCount = yAxisList.size() / 2;
        if(stack >= stackCount) {
            String errMsg = "Stack = " + stack + " Number of stacks: " + stackCount;
            throw new IllegalArgumentException(errMsg);
        }
    }


    /**
     * =======================Base methods to interact==========================
     **/

    public void setXAxisScale(int xIndex, Scale scale) {
        AxisWrapper axis = xAxisList.get(xIndex);
        for (Trace trace : traces) {
           if(trace.getXScale() == axis.getScale()) {
               trace.setXScale(scale);
           }
        }
        axis.setScale(scale);
        setAreasDirty();
    }

    public void setYAxisScale(int yIndex, Scale scale) {
        AxisWrapper axis = yAxisList.get(yIndex);
        for (Trace trace : traces) {
            Scale[] traceYScales = trace.getYScales();
            for (int i = 0; i < traceYScales.length; i++) {
                if(traceYScales[i] == axis.getScale()) {
                    traceYScales[i] = scale;
                }
            }
        }
        axis.setScale(scale);
        setAreasDirty();
    }

    public void setStackWeight(int stack, int weight) {
        checkStackNumber(stack);
        stackWeights.set(stack, weight);
        if(chartConfig.getMargin() != null) { // fixed margins
            setYStartEnd(graphArea.y, graphArea.height);
        } else {
            setAreasDirty();
        }
    }

    public void addStack() {
        addStack(chartConfig.getDefaultStackWeight());
    }

    public void addStack(int weight) {
        AxisWrapper leftAxis = new AxisWrapper(new AxisLeft(new LinearScale(), chartConfig.getLeftAxisConfig()));
        AxisWrapper rightAxis = new AxisWrapper(new AxisRight(new LinearScale(), chartConfig.getRightAxisConfig()));
        leftAxis.setRoundingEnabled(chartConfig.isYAxisRoundingEnabled());
        rightAxis.setRoundingEnabled(chartConfig.isYAxisRoundingEnabled());
        if (chartConfig.isYAxisRoundingEnabled()) {
            leftAxis.setRoundingAccuracyPct(chartConfig.getAxisRoundingAccuracyPctIfRoundingEnabled());
            rightAxis.setRoundingAccuracyPct(chartConfig.getAxisRoundingAccuracyPctIfRoundingEnabled());
        } else {
            leftAxis.setRoundingAccuracyPct(chartConfig.getAxisRoundingAccuracyPctIfRoundingDisabled());
            rightAxis.setRoundingAccuracyPct(chartConfig.getAxisRoundingAccuracyPctIfRoundingDisabled());
        }
        yAxisList.add(leftAxis);
        yAxisList.add(rightAxis);
        stackWeights.add(weight);

        if(chartConfig.getMargin() != null) { // fixed margins
            setYStartEnd(graphArea.y, graphArea.height);
        } else {
            setAreasDirty();
        }
    }

    /**
     * add trace to the last stack
     */
    public void addTrace(Trace trace, boolean isSplit, boolean isXAxisOpposite, boolean isYAxisOpposite) {
        int stackCount = yAxisList.size() / 2;
        addTrace(Math.max(0, stackCount - 1), trace, isSplit, isXAxisOpposite, isYAxisOpposite);
    }

    /**
     * add trace to the last stack
     */
    public void addTrace(Trace trace, boolean isSplit) {
        addTrace(trace, isSplit, false, false);
    }

    public void addTrace(int stack, Trace trace, boolean isSplit) {
        addTrace(stack, trace, isSplit, false, false);
    }


    /**
     * add trace to the stack with the given number
     */
    public void addTrace(int stack, Trace trace, boolean isSplit, boolean isXAxisOpposite, boolean isYAxisOpposite) {
        if(dataProcessingConfig != null) {
            trace.setDataProcessingConfig(dataProcessingConfig);
        }

        if(yAxisList.size() == 0) {
            addStack(); // add stack if there is no stack
        }
        checkStackNumber(stack);

        boolean isBottomXAxis = true;
        boolean isLeftYAxis = true;
        if (isXAxisOpposite && chartConfig.isBottomAxisPrimary()) {
            isBottomXAxis = false;
        }
        if (!isXAxisOpposite && !chartConfig.isBottomAxisPrimary()) {
            isBottomXAxis = false;
        }
        if (isYAxisOpposite && chartConfig.isLeftAxisPrimary()) {
            isLeftYAxis = false;
        }
        if (!isYAxisOpposite && !chartConfig.isLeftAxisPrimary()) {
            isLeftYAxis = false;
        }
        int xIndex = isBottomXAxis ? 0 : 1;
        int yIndex = isLeftYAxis ? stack * 2 : stack * 2 + 1;

        trace.setXScale(xAxisList.get(xIndex).getScale());
        xAxisList.get(xIndex).setVisible(true);
        if (!isSplit) {
            AxisWrapper yAxis = yAxisList.get(yIndex);
            yAxis.setVisible(true);
            trace.setYScales(yAxis.getScale());
        } else {
            int stackCount = yAxisList.size() / 2;
            int availableStacks = stackCount - stack;
            if (trace.curveCount() > availableStacks) {
                for (int i = 0; i < trace.curveCount() - availableStacks; i++) {
                    addStack();
                }
            }
            Scale[] yScales = new Scale[trace.curveCount()];
            for (int i = 0; i < trace.curveCount(); i++) {
                AxisWrapper yAxis = yAxisList.get(yIndex + i * 2);
                yScales[i] = yAxis.getScale();
                yAxis.setVisible(true);
            }
            trace.setYScales(yScales);
        }

        BColor[] colors = chartConfig.getTraceColors();
        int totalCurves = 0;
        for (Trace trace1 : traces) {
            totalCurves += trace1.curveCount();
        }
        for (int i = 0; i < trace.curveCount(); i++) {
            if (trace.getCurveColor(i) == null) {
                trace.setCurveColor(i, colors[(totalCurves + i) % colors.length]);
            }
            if (trace.getCurveName(i) == null || trace.getCurveName(i).isEmpty()) {
                trace.setCurveName(i, "Trace" + traces.size() + "_curve" + i);
            }

        }

        traces.add(trace);

        for (int i = 0; i < trace.curveCount(); i++) {
            final int curveNumber = i;
            StateListener traceSelectionListener = new StateListener() {
                @Override
                public void stateChanged(boolean isSelected) {
                    if (isSelected) {
                        selectedCurve = new TraceCurve(trace, curveNumber);
                    }
                    if (!isSelected && selectedCurve.getTrace() == trace && selectedCurve.getCurveNumber() == curveNumber) {
                        selectedCurve = null;
                    }
                }
            };
            legend.add(trace, i, traceSelectionListener);
        }
    }

    public void removeTrace(int traceNumber) {
        legend.remove(traces.get(traceNumber));
        traces.remove(traceNumber);
    }

    public void setArea(BRectangle area) {
        fullArea = area;
        if(chartConfig.getMargin() != null) {
            setMargin(chartConfig.getMargin());
        }
        setAreasDirty();
        titleText = null;
    }

    public int traceCount() {
        return traces.size();
    }

    public int xAxisCount() {
        return xAxisList.size();
    }

    public int yAxisCount() {
        return yAxisList.size();
    }

    public void setXMinMax(int xIndex, double min, double max) {
        if (xAxisList.get(xIndex).setMinMax(min, max)) {
            if (xAxisList.get(xIndex).isTickLabelOutside()) {
                setAreasDirty();
            }
        }
    }

    public BRange getXMinMax(int xIndex) {
        return new BRange(xAxisList.get(xIndex).getMin(), xAxisList.get(xIndex).getMax());
    }

    public BRange getYMinMax(int yAxisIndex) {
        return new BRange(yAxisList.get(yAxisIndex).getMin(), yAxisList.get(yAxisIndex).getMax());
    }

    public void setYMinMax(int yAxisIndex, double min, double max) {
        yAxisList.get(yAxisIndex).setMinMax(min, max);
        if (yAxisList.get(yAxisIndex).isTickLabelOutside()) {
            setAreasDirty();
        }
    }

    public void zoomY(int yAxisIndex, double zoomFactor) {
        Scale zoomedScale = yAxisList.get(yAxisIndex).zoom(zoomFactor);
        double zoomedMin = zoomedScale.getDomain()[0];
        double zoomedMax = zoomedScale.getDomain()[zoomedScale.getDomain().length - 1];
        setYMinMax(yAxisIndex, zoomedMin, zoomedMax);
    }

    public void zoomX(int xIndex, double zoomFactor) {
        Scale zoomedScale = xAxisList.get(xIndex).zoom(zoomFactor);
        double zoomedMin = zoomedScale.getDomain()[0];
        double zoomedMax = zoomedScale.getDomain()[zoomedScale.getDomain().length - 1];
        setXMinMax(xIndex, zoomedMin, zoomedMax);
    }

    public void translateY(int yAxisIndex, int translation) {
        Scale translatedScale = yAxisList.get(yAxisIndex).translate(translation);
        double translatedMin = translatedScale.getDomain()[0];
        double translatedMax = translatedScale.getDomain()[translatedScale.getDomain().length - 1];
        setYMinMax(yAxisIndex, translatedMin, translatedMax);
    }

    public void translateX(int xIndex, int translation) {
        Scale translatedScale = xAxisList.get(xIndex).translate(translation);
        double translatedMin = translatedScale.getDomain()[0];
        double translatedMax = translatedScale.getDomain()[translatedScale.getDomain().length - 1];
        setXMinMax(xIndex, translatedMin, translatedMax);
    }

    public void autoScaleX(int xIndex) {
        AxisWrapper axis = xAxisList.get(xIndex);
        if(!axis.isVisible()) {
            return;
        }

        BRange tracesXMinMax = null;
        for (Trace trace : traces) {
            if (trace.getXScale() == axis.getScale()) {
                tracesXMinMax = BRange.join(tracesXMinMax, trace.getFullXMinMax());
            }
        }

        if (tracesXMinMax != null) {
            setXMinMax(xIndex, tracesXMinMax.getMin(), tracesXMinMax.getMax());
        }
    }

    public void autoScaleY(int yIndex) {
        AxisWrapper axis = yAxisList.get(yIndex);
        if(!axis.isVisible()) {
            return;
        }

        BRange tracesYMinMax = null;
        for (Trace trace : traces) {
            int curveCount = trace.curveCount();
            for (int i = 0; i < curveCount; i++) {
                if (trace.getYScale(i) == axis.getScale()) {
                    tracesYMinMax = BRange.join(tracesYMinMax, trace.curveYMinMax(i));
                }
            }
        }

        if (tracesYMinMax != null) {
            setYMinMax(yIndex, tracesYMinMax.getMin(), tracesYMinMax.getMax());
        }
    }


    public boolean selectCurve(int x, int y) {
        if (legend.selectItem(x, y)) {
            return true;
        }
        return false;
    }

    /**
     * If point == null then return selectedCurve x index or -1 if no curve is selected
     * <p>
     * If point != null
     * <ul>
     * <li>if selectedCurve != null then return selectedCurve x index</li>
     * <li>if selectedCurve == null then return the index of X axis that has grid in the stack containing the point
     * <li>chart does not contain the point then return -1</li>
     */
    public int getXIndex(@Nullable BPoint point) {
        if (selectedCurve != null) {
            return getCurveXIndex(selectedCurve.getTrace());
        }
        if (point != null && fullArea.contains(point.getX(), point.getY())) {
            int bottomAxisIndex = 0;
            int topAxisIndex = 1;
            AxisWrapper bottomAxis = xAxisList.get(bottomAxisIndex);
            AxisWrapper topAxis = xAxisList.get(topAxisIndex);

            if(bottomAxis.isVisible() && !topAxis.isVisible()) {
                return bottomAxisIndex;
            } else if(!bottomAxis.isVisible() && topAxis.isVisible()) {
                return topAxisIndex;
            } else if(bottomAxis.isVisible() && topAxis.isVisible()) {
                if (point != null && fullArea.contains(point.getX(), point.getY())) {
                    // find point stack
                    int stackCount = yAxisList.size() / 2;
                    for (int i = 0; i < stackCount; i++) {
                        AxisWrapper axisLeft = yAxisList.get(2 * i);
                        if (axisLeft.getEnd() <= point.getY() && axisLeft.getStart() >= point.getY()) {
                            return chooseXAxisWithGrid(i);
                        }
                    }
                }
            }
        }

        return -1;
    }

    /**
     * If point == null then return selectedCurve y index or -1 if no curve is selected
     * <p>
     * If point != null
     * <ul>
     * <li>if selectedCurve != null then return selectedCurve y index</li>
     * <li>if selectedCurve == null then return the index of visible y axis belonging to the stack containing the point
     * <li>chart does not contain the point then return -1</li>
     */
    public int getYIndex(@Nullable BPoint point) {
        if (selectedCurve != null) {
            return getCurveYIndex(selectedCurve.getTrace(), selectedCurve.getCurveNumber());
        }
        if (point != null && fullArea.contains(point.getX(), point.getY())) {
            // find point stack and point "side" (left or right)
            int stackCount = yAxisList.size() / 2;
            for (int i = 0; i < stackCount; i++) {
                int leftYIndex = 2 * i;
                int rightYIndex = 2 * i + 1;
                AxisWrapper axisLeft = yAxisList.get(leftYIndex);
                AxisWrapper axisRight = yAxisList.get(rightYIndex);
                if (axisLeft.getEnd() <= point.getY() && axisLeft.getStart() >= point.getY()) {
                    if(!axisLeft.isVisible() && !axisRight.isVisible()) {
                        break;
                    }
                    if(!axisLeft.isVisible()) {
                        return rightYIndex;
                    }
                    if(!axisRight.isVisible()) {
                        return leftYIndex;
                    }
                    if (fullArea.x <= point.getX() && point.getX() <= fullArea.x + fullArea.width / 2 && axisLeft.isVisible()) { // left half
                        return leftYIndex;
                    } else {
                        return rightYIndex;
                    }
                }
            }
        }

        return -1;
    }

    public boolean hoverOff() {
        if (hoverPoint != null) {
            hoverPoint = null;
            return true;
        }
        return false;
    }

    public boolean hoverOn(int x, int y) {
        if (!graphArea.contains(x, y)) {
            return hoverOff();
        }
        if (hoverPoint == null && selectedCurve != null) {
            hoverPoint = new TraceCurvePoint(selectedCurve.getTrace(), selectedCurve.getCurveNumber(), -1);
        }

        if (hoverPoint != null) {
            NearestPoint nearestPoint = hoverPoint.getTrace().nearest(x, y, hoverPoint.getCurveNumber());
            if (hoverPoint.getPointIndex() == nearestPoint.getPointIndex()) {
                return false;
            } else {
                hoverPoint = nearestPoint.getCurvePoint();
            }
        } else {
            // find nearest trace curve
            NearestPoint nearestPoint = null;
            for (Trace trace : traces) {
                NearestPoint np = trace.nearest(x, y, -1);
                if (nearestPoint == null || nearestPoint.getDistanceSq() > np.getDistanceSq()) {
                    nearestPoint = np;
                }
            }

            if (nearestPoint != null) {
                hoverPoint = nearestPoint.getCurvePoint();
            }
        }

        if (hoverPoint != null) {
            if (hoverPoint.getPointIndex() >= 0) {
                Trace hoverTrace = hoverPoint.getTrace();
                int hoverCurveNumber = hoverPoint.getCurveNumber();
                int hoverPointIndex = hoverPoint.getPointIndex();
                int xPosition = hoverTrace.xPosition(hoverPointIndex);
                int tooltipYPosition = 0;
                NamedValue xValue = hoverTrace.xValue(hoverPointIndex);

                crosshair = new Crosshair(chartConfig.getCrossHairConfig(), xPosition);
                tooltip = new Tooltip(chartConfig.getTooltipConfig(), xPosition, tooltipYPosition);
                tooltip.setHeader(null, null, xValue.getValueLabel());
                if (chartConfig.isMultiCurveTooltip()) { // all trace curves
                    for (int i = 0; i < hoverTrace.curveCount(); i++) {
                        addCurvePointToTooltip(tooltip, hoverTrace, i, hoverPointIndex);
                        crosshair.addY(hoverTrace.curveYPosition(i, hoverPointIndex));

                    }
                } else { // only hover curve
                    addCurvePointToTooltip(tooltip, hoverTrace, hoverCurveNumber, hoverPointIndex);
                    crosshair.addY(hoverTrace.curveYPosition(hoverCurveNumber, hoverPointIndex));
                }
            }
            return true;
        }
        return false;
    }

    private void addCurvePointToTooltip(Tooltip tooltip, Trace trace, int curveNumber, int pointIndex) {
        NamedValue[] curveValues = trace.curveValues(curveNumber, pointIndex);
        if (curveValues.length == 1) {
            tooltip.addLine(trace.getCurveColor(curveNumber), trace.getCurveName(curveNumber), curveValues[0].getValueLabel());
        } else {
            tooltip.addLine(trace.getCurveColor(curveNumber), trace.getCurveName(curveNumber), "");
            for (NamedValue curveValue : curveValues) {
                tooltip.addLine(null, curveValue.getValueName(), curveValue.getValueLabel());
            }
        }
    }
}
