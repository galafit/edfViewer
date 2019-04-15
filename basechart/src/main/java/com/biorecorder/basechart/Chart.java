package com.biorecorder.basechart;

import com.biorecorder.basechart.axis.*;
import com.biorecorder.basechart.button.StateListener;
import com.biorecorder.basechart.graphics.*;
import com.biorecorder.basechart.scales.LinearScale;
import com.biorecorder.basechart.scales.Scale;
import com.biorecorder.basechart.themes.WhiteTheme;
import com.biorecorder.basechart.utils.StringUtils;
import com.sun.istack.internal.Nullable;

import java.util.*;
import java.util.List;

/**
 * Created by hdablin on 24.03.17.
 */
public class Chart {
    private ChartConfig config = new ChartConfig();
    /*
 * 2 X-axis: 0(even) - BOTTOM and 1(odd) - TOP
 * 2 Y-axis for every section(stack): even - LEFT and odd - RIGHT;
 * All LEFT and RIGHT Y-axis are stacked.
 * If there is no trace associated with some axis... this axis is invisible.
 **/
    private List<AxisWrapper> xAxisList = new ArrayList<>(2);
    private List<AxisWrapper> yAxisList = new ArrayList<>();
    private Map<Integer, Range> xAxisToMinMax = new HashMap<>(1);
    private Map<Integer, Range> yAxisToMinMax = new HashMap<>(1);

    private ArrayList<Integer> stackWeights = new ArrayList<Integer>();
    private List<Trace> traces = new ArrayList<Trace>();
    private Legend legend;
    private Title title;
    private BRectangle fullArea;
    private BRectangle graphArea;
    private Insets margin;

    private Crosshair crosshair;
    private Tooltip tooltip;

    private TraceCurve selectedCurve;
    private TraceCurvePoint hoverPoint;
    private DataProcessingConfig dataProcessingConfig;
    private Scale yScale;

    public Chart() {
       this(new WhiteTheme().getChartConfig());
    }

    public Chart(ChartConfig config) {
        this(config, new LinearScale(), new LinearScale(), new DataProcessingConfig());
    }

    public Chart(Scale xScale, Scale yScale) {
        this(new WhiteTheme().getChartConfig(), xScale, yScale, new DataProcessingConfig());
    }

    public Chart(ChartConfig config, Scale xScale, Scale yScale) {
        this(config, xScale, yScale, new DataProcessingConfig());
    }

    public Chart(ChartConfig config, Scale xScale, Scale yScale, DataProcessingConfig dataProcessingConfig) {
        this.dataProcessingConfig = new DataProcessingConfig(dataProcessingConfig);
        this.config = new ChartConfig(config);
        this.yScale = yScale;

        AxisWrapper bottomAxis = new AxisWrapper(new AxisBottom(xScale.copy(), config.getXAxisConfig()));
        AxisWrapper topAxis = new AxisWrapper(new AxisTop(xScale.copy(), config.getXAxisConfig()));

        xAxisList.add(bottomAxis);
        xAxisList.add(topAxis);

        //legend
        legend = new Legend(config.getLegendConfig());

        //title
        title = new Title(config.getTitleConfig());
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

    private void setDirty() {
        margin = null;
        graphArea = null;
    }

    private boolean isDirty() {
        if (margin == null || graphArea == null) {
            return true;
        }
        return false;
    }

    private Insets calculateSpacing() {
        if(config.getSpacing() != null) {
            return config.getSpacing();
        }
        int minSpacing = 0;
        int spacingTop = minSpacing;
        int spacingBottom = minSpacing;
        int spacingLeft = minSpacing;
        int spacingRight = minSpacing;
        for (int i = 0; i < yAxisList.size(); i++) {
           AxisWrapper axis = yAxisList.get(i);
           if(i % 2 == 0) { // left
              if(axis.isVisible() && (axis.isTickLabelOutside() || !StringUtils.isNullOrBlank(axis.getTitle())) ) {
                 spacingLeft = config.getAutoSpacing();
              }
           } else { // right
               if(axis.isVisible() && (axis.isTickLabelOutside() || !StringUtils.isNullOrBlank(axis.getTitle()))) {
                   spacingRight = config.getAutoSpacing();
               }
           }
        }

        for (int i = 0; i < xAxisList.size(); i++) {
            AxisWrapper axis = xAxisList.get(i);
            if(i % 2 == 0) { // bottom
                if(axis.isVisible() && (axis.isTickLabelOutside() || !StringUtils.isNullOrBlank(axis.getTitle()))) {
                    spacingBottom = config.getAutoSpacing();
                }
            } else { // top
                if(axis.isVisible() && (axis.isTickLabelOutside() || !StringUtils.isNullOrBlank(axis.getTitle()))) {
                    spacingTop = config.getAutoSpacing();
                }
            }
        }
        if(isLegendEnabled() && !legend.isAttachedToStacks()) {
            if(legend.isTop()) {
                spacingTop = config.getAutoSpacing();
            } else if (legend.isBottom()) {
                spacingBottom = config.getAutoSpacing();
            }
        }
        if(!title.isNullOrBlank()){
            spacingTop = 0;
        }

        return new Insets(spacingTop, spacingRight, spacingBottom, spacingLeft);
    }

    private boolean isLegendEnabled() {
        if(legend == null || !legend.isEnabled()) {
            return false;
        }
        return true;
    }

    private void setXMinMax(BCanvas canvas) {
        for (int i = 0; i < xAxisList.size(); i++) {
            Range minMax = xAxisToMinMax.get(i);
            AxisWrapper axis = xAxisList.get(i);
            if(minMax != null) {
                // NO ROUNDING !!! course the X min and max depends data processing
                axis.setMinMax(minMax.getMin(), minMax.getMax());
            } else { // auto scale X
                Range tracesXMinMax = null;
                for (Trace trace : traces) {
                    if (trace.getXScale() == axis.getScale()) {
                        tracesXMinMax = Range.join(tracesXMinMax, trace.getFullXMinMax());
                    }
                }

                if (tracesXMinMax != null) {
                    axis.setMinMax(tracesXMinMax.getMin(), tracesXMinMax.getMax());
                }
                // rounding only in the case of auto scale when no data processing
                axis.roundMinMax(canvas);
            }
        }
    }

    private void setYMinMax(BCanvas canvas) {
        for (int i = 0; i < yAxisList.size(); i++) {
            Range minMax = yAxisToMinMax.get(i);
            AxisWrapper axis = yAxisList.get(i);
            if(minMax != null) {
                axis.setMinMax(minMax.getMin(), minMax.getMax());
            } else { // auto scale Y
                Range tracesYMinMax = null;
                for (Trace trace : traces) {
                    int curveCount = trace.curveCount();
                    for (int curve = 0; curve < curveCount; curve++) {
                        if (trace.getYScale(curve) == axis.getScale()) {
                            tracesYMinMax = Range.join(tracesYMinMax, trace.curveYMinMax(curve));
                        }
                    }
                }

                if (tracesYMinMax != null) {
                    axis.setMinMax(tracesYMinMax.getMin(), tracesYMinMax.getMax());
                }
            }
            axis.roundMinMax(canvas);
        }
    }

    private void doCalculations(BCanvas canvas) {
        if(fullArea.width == 0 || fullArea.height == 0) {
            graphArea = fullArea;
            margin = new Insets(0);
            return;
        }

        // all calculation with x axes must be done always first course data processing depends on it!!!
        if(config.getMargin() != null) { // fixed margin
            setMargin(config.getMargin(), canvas);
            return;
        }

        Insets spacing = calculateSpacing();
        int titleHeight = title.getBounds(canvas).height;

        if(graphArea == null) {
            graphArea = fullArea;
        }
        setXStartEnd(graphArea.x, graphArea.width);
        setXMinMax(canvas);

        int top = spacing.top() + titleHeight + xAxisList.get(1).getWidth(canvas);
        int bottom = spacing.bottom() + xAxisList.get(0).getWidth(canvas);

        int legendHeight = 0;
        if (isLegendEnabled() && !legend.isAttachedToStacks()) {
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
        setYMinMax(canvas);
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

        margin = new Insets(top, right, bottom, left);
        graphArea = new BRectangle(fullArea.x + left, fullArea.y + top,
                Math.max(0, fullArea.width - left - right), Math.max(0, fullArea.height - top - bottom));


        // adjust XAxis ranges
        setXStartEnd(graphArea.x, graphArea.width);
        setXMinMax(canvas);

        if (isLegendEnabled() && legend.isAttachedToStacks()) {
            legend.setArea(graphArea);
        }
    }

    private void setXStartEnd(int areaX, int areaWidth) {
        for (AxisWrapper axis : xAxisList) {
            axis.setStartEnd(areaX, areaX + areaWidth);
        }
    }

    private void setYStartEnd(int areaY, int areaHeight) {
        int weightSum = getStacksSumWeight();

        int stackCount = yAxisList.size() / 2;

        int gap = Math.abs(config.getStackGap());
        int height = areaHeight - (stackCount  - 1) * gap;
        if(height <= 0) {
            height = areaHeight;
            gap = 0;
        }

        double end = areaY;
        for (int stack = 0; stack < stackCount; stack++) {
            int yAxisWeight = stackWeights.get(stack);
            double axisHeight = 1.0 * height * yAxisWeight / weightSum;
            double start = end + axisHeight;
           /* if(stack == stackCount - 1) {
                // for integer calculation sum yAxis length can be != areaHeight
                // so we fix that
                start = areaY + areaHeight;
            }*/
            yAxisList.get(stack * 2).setStartEnd(start, end);
            yAxisList.get(stack * 2 + 1).setStartEnd(start, end);
            end = start + gap;
        }
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

    private int chooseXAxisWithGrid(int stackNumber) {
        int bottomAxisIndex = 0;
        int topAxisIndex = 1;

        AxisWrapper leftAxis = yAxisList.get(stackNumber * 2);
        AxisWrapper rightAxis = yAxisList.get(stackNumber * 2 + 1);
        int primaryAxisIndex;
        if(config.isBottomAxisPrimary()) {
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

        if(config.isBottomAxisPrimary()) {
            return topAxisIndex;
        } else {
            return bottomAxisIndex;
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
     * ===================== Protected method for careful use (in NavigableChart)=========
     **/
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
    Range getAllTracesFullMinMax() {
        Range minMax = null;
        for (int i = 0; i < traces.size(); i++) {
            minMax = Range.join(minMax, traces.get(i).getFullXMinMax());
        }
        return minMax;
    }


    int getStacksSumWeight() {
        int weightSum = 0;
        for (Integer weight : stackWeights) {
            weightSum += weight;
        }
        return weightSum;
    }

    boolean isXAxisVisible(int xIndex) {
        if(xAxisList.get(xIndex).isVisible()) {
            return true;
        }
        return false;
    }

    Range getYMinMax(int yIndex, BCanvas canvas) {
        if (isDirty()) {
            doCalculations(canvas);
        }
        AxisWrapper yAxis = yAxisList.get(yIndex);
        return new Range(yAxis.getMin(), yAxis.getMax());
    }

    void setMargin(Insets margin, BCanvas canvas) {
        this.margin = margin;
        if(fullArea != null) {
            int graphAreaWidth = fullArea.width - margin.left() - margin.right();
            int graphAreaHeight = fullArea.height - margin.top() - margin.bottom();
            if(graphAreaHeight < 0) {
                graphAreaHeight = 0;
            }
            if(graphAreaWidth < 0) {
                graphAreaWidth = 0;
            }
            graphArea = new BRectangle(fullArea.x + margin.left(), fullArea.y + margin.top(), graphAreaWidth, graphAreaHeight);
            if (isLegendEnabled() && legend.isAttachedToStacks()) {
                legend.setArea(graphArea);
            }
            setXStartEnd(graphArea.x, graphArea.width);
            setYStartEnd(graphArea.y, graphArea.height);
            setXMinMax(canvas);
            setYMinMax(canvas);
        }
    }


    Insets getMargin(BCanvas canvas) {
        if (isDirty()) {
            doCalculations(canvas);
        }
        return margin;
    }

    BRectangle getGraphArea(BCanvas canvas) {
        if (isDirty()) {
            doCalculations(canvas);
        }
        return graphArea;
    }

    double scale(int xIndex, double value) {
        return xAxisList.get(xIndex).getScale().scale(value);
    }

    double invert(int xIndex, double value) {
        return xAxisList.get(xIndex).getScale().invert(value);
    }


    /**
     * =======================Base methods to interact==========================
     **/
    public void draw(BCanvas canvas) {
        if(fullArea == null) {
            setArea(canvas.getBounds());
        }

        if(fullArea.width == 0 || fullArea.height == 0) {
            return;
        }

        if (isDirty()) {
            doCalculations(canvas);
        }

        canvas.enableAntiAliasAndHinting();

        canvas.setColor(config.getMarginColor());
        canvas.fillRect(fullArea.x, fullArea.y, fullArea.width, fullArea.height);

        // fill stacks
        int stackCount = yAxisList.size() / 2;

        canvas.setColor(config.getBackgroundColor());
        for (int i = 0; i < stackCount; i++) {
            AxisWrapper yAxis = yAxisList.get(i * 2);
            BRectangle stackArea = new BRectangle(graphArea.x, (int)yAxis.getEnd(), graphArea.width, (int)yAxis.length());
            canvas.fillRect(stackArea.x, stackArea.y, stackArea.width, stackArea.height);
        }

        // draw X axes grids
        AxisWrapper bottomAxis = xAxisList.get(0);
        AxisWrapper topAxis = xAxisList.get(1);

        // draw separately for every stack
        for (int i = 0; i < stackCount; i++) {
            AxisWrapper yAxis = yAxisList.get(2 * i);
            BRectangle stackArea = new BRectangle(graphArea.x, (int)yAxis.getEnd(), graphArea.width, (int)yAxis.length());
            if(bottomAxis.isVisible() && !topAxis.isVisible()) {
                bottomAxis.drawGrid(canvas, stackArea);
            } else if(!bottomAxis.isVisible() && topAxis.isVisible()) {
                topAxis.drawGrid(canvas, stackArea);
            } else if(bottomAxis.isVisible() && topAxis.isVisible()) {
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
                if(config.isLeftAxisPrimary()) {
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

        title.draw(canvas);

        if (isLegendEnabled()) {
            legend.draw(canvas);
        }

        if (hoverPoint != null) {
            crosshair.draw(canvas, graphArea);
            tooltip.draw(canvas, fullArea);
        }
    }


    public void appendData() {
        for (Trace trace : traces) {
            trace.appendData();
        }
        setDirty();
    }

    public String[] getCurveNames() {
       List<String> names = new ArrayList<>();
        for (Trace trace : traces) {
            for (int i = 0; i < trace.curveCount(); i++) {
                names.add(trace.getCurveName(i));
            }
        }
        String[] namesArr = new String[names.size()];
        return names.toArray(namesArr);
    }

    public int traceCurveCount(int traceNumber) {
        return traces.get(traceNumber).curveCount();
    }

    public CurveNumber getCurveNumberByName(String name) {
        for (int i = 0; i < traces.size(); i++) {
            Trace trace = traces.get(i);
            for (int j = 0; j < trace.curveCount(); j++) {
               if(trace.getCurveName(i).equals(name)) {
                   return new CurveNumber(i, j);
               }
            }
        }
        return null;
    }

    public CurveNumber getSelectedCurveNumber() {
        if(selectedCurve != null) {
            for (int i = 0; i < traces.size(); i++) {
                if(selectedCurve.getTrace() == traces.get(i)){
                    return new CurveNumber(i, selectedCurve.getCurveNumber());
                }
            }
        }

        return null;
    }

    /**
     * return COPY of chart config. To change chart config use setConfig
     */
    public ChartConfig getConfig() {
        return new ChartConfig(config);
    }

    /**
     * if isTraceColorChangeEnabled is true all curves colors will be
     * changed according with the config traceColors.
     * Otherwise curve colors will stay as they are.
     */
    public void setConfig(ChartConfig chartConfig, boolean isTraceColorChangeEnabled) {
        this.config = new ChartConfig(chartConfig);
        title.setConfig(config.getTitleConfig());
        for (int i = 0; i < xAxisList.size(); i++) {
            xAxisList.get(i).setConfig(this.config.getXAxisConfig());
        }
        for (int i = 0; i < yAxisList.size(); i++) {
            yAxisList.get(i).setConfig(this.config.getYAxisConfig());
        }
        legend.setConfig(config.getLegendConfig());

        BColor[] colors = this.config.getTraceColors();
        if(isTraceColorChangeEnabled) {
            int curve = 0;
            for (Trace trace : traces) {
                for (int i = 0; i < trace.curveCount(); i++) {
                    trace.setCurveColor(i, colors[(curve + i) % colors.length]);
                    curve++;
                }
            }
        }
        setDirty();
    }

    public void setTitle(String title) {
        this.title.setTitle(title);
        setDirty();
    }

    public void setCurveColor(int traceNumber, int curveNumber, BColor color) {
        traces.get(traceNumber).setCurveColor(curveNumber, color);
    }

    public void setCurveName(int traceNumber, int curveNumber, String name) {
        traces.get(traceNumber).setCurveName(curveNumber, name);
        legend.setCurveName(traces.get(traceNumber), curveNumber, name);
    }

    public void setStackWeight(int stack, int weight) {
        checkStackNumber(stack);
        stackWeights.set(stack, weight);
        setDirty();
    }

    public void addStack() {
        addStack(config.getDefaultStackWeight());
    }

    public void addStack(int weight) {
        AxisWrapper leftAxis = new AxisWrapper(new AxisLeft(yScale.copy(), config.getYAxisConfig()));
        AxisWrapper rightAxis = new AxisWrapper(new AxisRight(yScale.copy(), config.getYAxisConfig()));
        yAxisList.add(leftAxis);
        yAxisList.add(rightAxis);
        stackWeights.add(weight);

        setDirty();
    }

    /**
     *
     * @throws IllegalStateException if stack axis are used by some trace curves and
     * therefor can not be deleted
     */
    public void removeStack(int stackNumber) throws IllegalStateException{
        // check that no trace use that stack
        Scale leftScale = yAxisList.get(stackNumber * 2).getScale();
        Scale rightScale = yAxisList.get(stackNumber * 2 + 1).getScale();


        for (int i = 0; i < traces.size(); i++) {
            for (Scale scale : traces.get(i).getYScales()) {
                if(scale == leftScale || scale == rightScale) {
                     String errMsg = "Stack: " + stackNumber + "can not be removed. It is used by trace number: " + i;
                    throw new IllegalStateException(errMsg);
                }
            }
        }
        stackWeights.remove(stackNumber);
        yAxisList.remove(stackNumber * 2 + 1);
        yAxisList.remove(stackNumber * 2);
        setDirty();
    }

    /**
     * add trace to the last stack
     */
    public void addTrace(Trace trace) {
        addTrace(trace, true);
    }


    /**
     * add trace to the last stack
     */
    public void addTrace(Trace trace, boolean isSplit) {
        addTrace(trace, isSplit, false, false);
    }


    /**
     * add trace to the last stack
     */
    public void addTrace(Trace trace, boolean isSplit, boolean isXAxisOpposite, boolean isYAxisOpposite) {
        int stackCount = yAxisList.size() / 2;
        addTrace(Math.max(0, stackCount - 1), trace, isSplit, isXAxisOpposite, isYAxisOpposite);
    }

    /**
     * add trace to the stack with the given number
     */
    public void addTrace(int stack, Trace trace, boolean isSplit) {
        addTrace(stack, trace, isSplit, false, false);
    }

    /**
     * add trace to the stack with the given number
     */
    public void addTrace(int stack, Trace trace, boolean isSplit, boolean isXAxisOpposite, boolean isYAxisOpposite) throws IllegalArgumentException {
        if(trace.curveCount() < 1) {
            String errMsg = "Number of trace curves: " + trace.curveCount() + ". Please specify valid trace data";
            throw new IllegalArgumentException(errMsg);
        }

        if(dataProcessingConfig != null) {
            trace.setDataProcessingConfig(dataProcessingConfig);
        }

        if(yAxisList.size() == 0) {
            addStack(); // add stack if there is no stack
        }
        checkStackNumber(stack);

        boolean isBottomXAxis = true;
        boolean isLeftYAxis = true;
        if (isXAxisOpposite && config.isBottomAxisPrimary()) {
            isBottomXAxis = false;
        }
        if (!isXAxisOpposite && !config.isBottomAxisPrimary()) {
            isBottomXAxis = false;
        }
        if (isYAxisOpposite && config.isLeftAxisPrimary()) {
            isLeftYAxis = false;
        }
        if (!isYAxisOpposite && !config.isLeftAxisPrimary()) {
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

        BColor[] colors = config.getTraceColors();
        int totalCurves = 0;
        for (Trace trace1 : traces) {
            totalCurves += trace1.curveCount();
        }
        for (int i = 0; i < trace.curveCount(); i++) {
            if (trace.getCurveColor(i) == null) {
                trace.setCurveColor(i, colors[(totalCurves + i) % colors.length]);
            }
            if (trace.getCurveName(i) == null || StringUtils.isNullOrBlank(trace.getCurveName(i))) {
                trace.setCurveName(i, "Trace" + traces.size() + "_curve" + i);
            }

        }

        traces.add(trace);

        if(isLegendEnabled()) {
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
                legend.add(trace, curveNumber, traceSelectionListener);
            }
        }
        setDirty();
    }

    public void removeTrace(int traceNumber) {
        Trace trace = traces.get(traceNumber);
        if(isLegendEnabled()) {
            legend.remove(trace);
        }
        traces.remove(traceNumber);
        // remove the stacks used by the trace curves if that stacks
        // are not used anymore
        int traceStack = 0;
        for (int i = 0; i < yAxisList.size(); i++) {
            if(trace.getYScale(0) == yAxisList.get(i).getScale()) {
                traceStack = i / 2;
            }
        }
        int stacks = trace.getYScales().length;
        for (int i = traceStack + stacks - 1; i >= traceStack; i--) {
            try {
                removeStack(i);
            } catch (IllegalStateException ex) {
                // do nothing;
            }
        }
        setDirty();
    }

    public void setArea(BRectangle area) {
        fullArea = area;
        title.setArea(area);
        setDirty();
    }

    public int traceCount() {
        return traces.size();
    }

    public int xAxesCount() {
        return xAxisList.size();
    }

    public int yAxesCount() {
        return yAxisList.size();
    }

    public void setXConfig(int xIndex, AxisConfig axisConfig) {
        xAxisList.get(xIndex).setConfig(axisConfig);
        setDirty();
    }

    public void setYConfig(int yIndex, AxisConfig axisConfig) {
        yAxisList.get(yIndex).setConfig(axisConfig);
        setDirty();
    }

    /**
     * return COPY of X axis config. To change axis config use setXConfig
     */
    public AxisConfig getXConfig(int xIndex) {
        return xAxisList.get(xIndex).getConfig();
    }

    /**
     * return COPY of Y axis config. To change axis config use setYConfig
     */
    public AxisConfig getYConfig(int yIndex) {
        return yAxisList.get(yIndex).getConfig();
    }

    public void setXTitle(int xIndex, @Nullable String title) {
        xAxisList.get(xIndex).setTitle(title);
        setDirty();
    }

    public void setYTitle(int yIndex, @Nullable String title) {
        yAxisList.get(yIndex).setTitle(title);
        setDirty();
    }


    public void setXMinMax(int xIndex, double min, double max) {
        xAxisToMinMax.put(xIndex, new Range(min, max));
        setDirty();
    }

    public void setYMinMax(int yIndex, double min, double max) {
        yAxisToMinMax.put(yIndex, new Range(min, max));
        setDirty();
    }


    public void autoScaleX(int xIndex) {
        xAxisToMinMax.remove(xIndex);
        setDirty();
    }

    public void autoScaleY(int yIndex) {
        yAxisToMinMax.remove(yIndex);
        setDirty();
    }

    public void setXScale(int xIndex, Scale scale) {
        AxisWrapper axis = xAxisList.get(xIndex);
        for (Trace trace : traces) {
            if(trace.getXScale() == axis.getScale()) {
                trace.setXScale(scale);
            }
        }
        axis.setScale(scale);
        setDirty();
    }

    public void setYScale(int yIndex, Scale scale) {
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
        setDirty();
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


    public boolean selectCurve(int x, int y) {
        if (isLegendEnabled() && legend.selectItem(x, y)) {
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
            if(nearestPoint == null) {
                hoverPoint = null;
                return true;
            }
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
                if ( np!= null && (nearestPoint == null || nearestPoint.getDistanceSq() > np.getDistanceSq())) {
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

                crosshair = new Crosshair(config.getCrossHairConfig(), xPosition);
                tooltip = new Tooltip(config.getTooltipConfig(), xPosition, tooltipYPosition);
                tooltip.setHeader(null, null, xValue.getValueLabel());
                if (config.isMultiCurveTooltip()) { // all trace curves
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
}
