package com.biorecorder.basechart;

import com.biorecorder.basechart.axis.*;
import com.biorecorder.basechart.button.ButtonGroup;
import com.biorecorder.basechart.button.StateListener;
import com.biorecorder.basechart.button.SwitchButton;
import com.biorecorder.basechart.graphics.*;
import com.biorecorder.basechart.scales.CategoryScale;
import com.biorecorder.basechart.scales.LinearScale;
import com.biorecorder.basechart.scales.Scale;
import com.biorecorder.basechart.themes.DarkTheme;
import com.biorecorder.basechart.utils.StringUtils;
import com.biorecorder.data.sequence.StringSequence;
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

    private boolean isDirty = true;

    public Chart() {
        this(new LinearScale(), new LinearScale());
    }

    public Chart(Scale xScale) {
        this(xScale, new LinearScale());
    }

    public Chart(Scale xScale, Scale yScale) {
        this.dataProcessingConfig = new DataProcessingConfig();
        this.config = new DarkTheme(false).getChartConfig();
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

    private Insets calculateSpacing() {
        if (config.getSpacing() != null) {
            return config.getSpacing();
        }
        int minSpacing = 0;
        int spacingTop = minSpacing;
        int spacingBottom = minSpacing;
        int spacingLeft = minSpacing;
        int spacingRight = minSpacing;
        for (int i = 0; i < yAxisList.size(); i++) {
            AxisWrapper axis = yAxisList.get(i);
            if (i % 2 == 0) { // left
                if (axis.isVisible() && (axis.isTickLabelOutside() || !StringUtils.isNullOrBlank(axis.getTitle()))) {
                    spacingLeft = config.getAutoSpacing();
                }
            } else { // right
                if (axis.isVisible() && (axis.isTickLabelOutside() || !StringUtils.isNullOrBlank(axis.getTitle()))) {
                    spacingRight = config.getAutoSpacing();
                }
            }
        }

        for (int i = 0; i < xAxisList.size(); i++) {
            AxisWrapper axis = xAxisList.get(i);
            if (i % 2 == 0) { // bottom
                if (axis.isVisible() && (axis.isTickLabelOutside() || !StringUtils.isNullOrBlank(axis.getTitle()))) {
                    spacingBottom = config.getAutoSpacing();
                }
            } else { // top
                if (axis.isVisible() && (axis.isTickLabelOutside() || !StringUtils.isNullOrBlank(axis.getTitle()))) {
                    spacingTop = config.getAutoSpacing();
                }
            }
        }
        if (isLegendEnabled() && !legend.isAttachedToStacks()) {
            if (legend.isTop()) {
                spacingTop = config.getAutoSpacing();
            } else if (legend.isBottom()) {
                spacingBottom = config.getAutoSpacing();
            }
        }
        if (!title.isNullOrBlank()) {
            spacingTop = 0;
        }

        return new Insets(spacingTop, spacingRight, spacingBottom, spacingLeft);
    }

    private boolean isLegendEnabled() {
        if (legend == null || !legend.isEnabled()) {
            return false;
        }
        return true;
    }

    private void setXMinMax(BCanvas canvas) {
        for (int xIndex = 0; xIndex < xAxisList.size(); xIndex++) {
            Range minMax = xAxisToMinMax.get(xIndex);
            AxisWrapper xAxis = xAxisList.get(xIndex);
            if (minMax != null) {
                // NO ROUNDING !!! course the X min and max depends data processing
                xAxis.setMinMax(minMax.getMin(), minMax.getMax());
            } else { // auto scale X
                Range tracesXMinMax = null;
                for (Trace trace : traces) {
                    if (trace.getXIndex() == xIndex) {
                        tracesXMinMax = Range.join(tracesXMinMax, trace.getFullXMinMax(xAxis.getScale()));
                    }
                }

                if (tracesXMinMax != null) {
                    xAxis.setMinMax(tracesXMinMax.getMin(), tracesXMinMax.getMax());
                }
                // rounding only in the case of auto scale when no data processing
                xAxis.roundMinMax(canvas);
            }
        }
    }

    private void setYMinMax(BCanvas canvas) {
        for (int yIndex = 0; yIndex < yAxisList.size(); yIndex++) {
            Range minMax = yAxisToMinMax.get(yIndex);
            AxisWrapper yAxis = yAxisList.get(yIndex);
            if (minMax != null) {
                yAxis.setMinMax(minMax.getMin(), minMax.getMax());
            } else { // auto scale Y
                Range tracesYMinMax = null;
                for (Trace trace : traces) {
                    int curveCount = trace.curveCount();
                    for (int curve = 0; curve < curveCount; curve++) {
                        if (trace.getYIndex(curve) == yIndex) {
                            tracesYMinMax = Range.join(tracesYMinMax, trace.curveYMinMax(curve, xAxisList.get(trace.getXIndex()).getScale(), yAxis.getScale()));
                        }
                    }
                }

                if (tracesYMinMax != null) {
                    yAxis.setMinMax(tracesYMinMax.getMin(), tracesYMinMax.getMax());
                }
            }
            yAxis.roundMinMax(canvas);
        }
    }

    private void doCalculations(BCanvas canvas) {
        if (fullArea.width == 0 || fullArea.height == 0) {
            graphArea = fullArea;
            margin = new Insets(0);
            return;
        }

        // all calculation with x axes must be done always first course data processing depends on it!!!
        if (config.getMargin() != null) { // fixed margin
            setMargin(config.getMargin(), canvas);
            return;
        }

        Insets spacing = calculateSpacing();
        int titleHeight = title.getBounds(canvas).height;

        if (graphArea == null) {
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

        // recalculate with precise y axis width
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
        isDirty = false;
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
        int height = areaHeight - (stackCount - 1) * gap;
        if (height <= 0) {
            height = areaHeight;
            gap = 0;
        }

        double end = areaY;
        for (int stack = 0; stack < stackCount; stack++) {
            int yAxisWeight = stackWeights.get(stack);
            double axisHeight = 1.0 * height * yAxisWeight / weightSum;
            double start = end + axisHeight;
           /* if(stack == stackCount - 1) {
                // for integer calculation sum yAxis intervalLength can be != areaHeight
                // so we fix that
                start = areaY + areaHeight;
            }*/
            yAxisList.get(stack * 2).setStartEnd(start, end);
            yAxisList.get(stack * 2 + 1).setStartEnd(start, end);
            end = start + gap;
        }
    }

    private int chooseXAxisWithGrid(int stack) {
        int leftAxisIndex = stack * 2;
        int rightAxisIndex = stack * 2 + 1;
        int primaryAxisIndex = getXIndex(config.getPrimaryXPosition());

        for (Trace trace : traces) {
            if (trace.getXIndex() == primaryAxisIndex) {
                for (int curve = 0; curve < trace.curveCount(); curve++) {
                    int curveYIndex = trace.getYIndex(curve);
                    if (curveYIndex == leftAxisIndex || curveYIndex == rightAxisIndex) {
                        return primaryAxisIndex;
                    }
                }
            }
        }

        if (config.getPrimaryXPosition() == XAxisPosition.BOTTOM) {
            return getXIndex(XAxisPosition.TOP);
        } else {
            return getXIndex(XAxisPosition.BOTTOM);
        }
    }

    private void checkStackNumber(int stack) {
        int stackCount = yAxisList.size() / 2;
        if (stack >= stackCount) {
            String errMsg = "Stack = " + stack + " Number of stacks: " + stackCount;
            throw new IllegalArgumentException(errMsg);
        }
    }

    private int getYIndex(int stack, YAxisPosition yPosition) {
        if (yPosition == YAxisPosition.LEFT) {
            return 2 * stack;
        } else {
            return 2 * stack + 1;
        }
    }

    private int getXIndex(XAxisPosition xPosition) {
        if (xPosition == XAxisPosition.BOTTOM) {
            return 0;
        } else {
            return 1;
        }
    }

    private int getYStack(int yIndex) {
        return yIndex / 2;
    }

    /**
     * 2 Y-axis for every section(stack): even - LEFT and odd - RIGHT;
     */
    private YAxisPosition getYPosition(int yIndex) {
        if ((yIndex & 1) == 0) {
            return YAxisPosition.LEFT;
        }

        return YAxisPosition.RIGHT;
    }

    /**
     * X-axis: 0(even) - BOTTOM and 1(odd) - TOP
     */
    private XAxisPosition getXPosition(int xIndex) {
        if ((xIndex & 1) == 0) {
            return XAxisPosition.BOTTOM;
        }
        return XAxisPosition.TOP;
    }


    /**
     * =============================================================*
     * Protected method for careful use                            *
     * ==============================================================
     */

    double getBestExtent(XAxisPosition xAxisPosition, BCanvas canvas) {
        double extent = xAxisList.get(getXIndex(xAxisPosition)).getBestExtent(canvas, fullArea.width);
        double tracesExtent = getTracesBestExtent(xAxisPosition);
        if (extent < 0) {
            extent = tracesExtent;
        } else if (tracesExtent > 0) {
            extent = Math.min(extent, tracesExtent);
        }
        return extent;
    }

    double getTracesBestExtent(XAxisPosition xAxisPosition) {
        double extent = -1;
        for (Trace trace : traces) {
            if (trace.getXIndex() == getXIndex(xAxisPosition)) {
                double traceExtent = trace.getBestExtent(fullArea.width);
                // System.out.println("trace extent "+traceExtent);
                if (extent < 0) {
                    extent = traceExtent;
                } else if (traceExtent > 0) {
                    extent = Math.min(extent, traceExtent);
                }
            }
        }
        return extent;
    }


    // for all x axis
    Range getAllTracesFullMinMax() {
        Range minMax = null;
        for (Trace trace : traces) {
            Scale traceXScale = xAxisList.get(trace.getXIndex()).getScale();
            minMax = Range.join(minMax, trace.getFullXMinMax(traceXScale));
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

    void setMargin(Insets margin, BCanvas canvas) {
        this.margin = margin;
        if (fullArea != null) {
            int graphAreaWidth = fullArea.width - margin.left() - margin.right();
            int graphAreaHeight = fullArea.height - margin.top() - margin.bottom();
            if (graphAreaHeight < 0) {
                graphAreaHeight = 0;
            }
            if (graphAreaWidth < 0) {
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
        if (isDirty) {
            doCalculations(canvas);
        }
        return margin;
    }

    BRectangle getGraphArea(BCanvas canvas) {
        if (isDirty) {
            doCalculations(canvas);
        }
        return graphArea;
    }

    double scale(XAxisPosition xAxisPosition, double value) {
        return xAxisList.get(getXIndex(xAxisPosition)).getScale().scale(value);
    }

    double invert(XAxisPosition xAxisPosition, double value) {
        return xAxisList.get(getXIndex(xAxisPosition)).getScale().invert(value);
    }

    boolean isXAxisVisible(XAxisPosition xAxisPosition) {
        if (xAxisList.get(getXIndex(xAxisPosition)).isVisible()) {
            return true;
        }
        return false;
    }

    Range getYMinMax(int stack, YAxisPosition yAxisPosition, BCanvas canvas) {
        if (isDirty) {
            doCalculations(canvas);
        }
        AxisWrapper yAxis = yAxisList.get(getYIndex(stack, yAxisPosition));
        return new Range(yAxis.getMin(), yAxis.getMax());
    }

    public boolean isCurveSelected() {
        return selectedCurve != null;
    }

    XAxisPosition getSelectedCurveX() {
        return getXPosition(selectedCurve.getTrace().getXIndex());
    }

    int getSelectedCurveStack() {
        int yIndex = selectedCurve.getTrace().getYIndex(selectedCurve.getCurve());
        return getYStack(yIndex);
    }


    YAxisPosition getSelectedCurveY() {
        int yIndex = selectedCurve.getTrace().getYIndex(selectedCurve.getCurve());
        return getYPosition(yIndex);
    }

    int getStack(BPoint point) {
        if (fullArea != null && fullArea.contains(point.getX(), point.getY())) {
            // find point stack
            int stackCount = yAxisList.size() / 2;
            for (int i = 0; i < stackCount; i++) {
                int leftYIndex = 2 * i;
                AxisWrapper axisLeft = yAxisList.get(leftYIndex);
                if (axisLeft.getEnd() <= point.getY() && axisLeft.getStart() >= point.getY()) {
                    return i;
                }
            }
        }
        return -1;
    }

    XAxisPosition[] getXAxes() {
        return XAxisPosition.values();
    }

    YAxisPosition[] getYAxes(int stack) {
        return YAxisPosition.values();
    }


    YAxisPosition getYAxis(int stack, BPoint point) {
        if (fullArea != null && fullArea.contains(point.getX(), point.getY())) {
            // find axis position
            AxisWrapper axisLeft = yAxisList.get(2 * stack);
            AxisWrapper axisRight = yAxisList.get(2 * stack + 1);
            if (axisLeft.getEnd() <= point.getY() && axisLeft.getStart() >= point.getY()) {
                if (axisLeft.getEnd() <= point.getY() && axisLeft.getStart() >= point.getY()) {
                    if (!axisLeft.isVisible() && !axisRight.isVisible()) {
                        return null;
                    }
                    if (!axisLeft.isVisible()) {
                        return YAxisPosition.RIGHT;
                    }
                    if (!axisRight.isVisible()) {
                        return YAxisPosition.LEFT;
                    }
                    if (fullArea.x <= point.getX() && point.getX() <= fullArea.x + fullArea.width / 2 && axisLeft.isVisible()) { // left half
                        return YAxisPosition.LEFT;
                    } else {
                        return YAxisPosition.RIGHT;
                    }
                }
            }
        }
        return null;
    }

    XAxisPosition getXAxis(BPoint point) {
        if (fullArea != null && fullArea.contains(point.getX(), point.getY())) {
            int bottomAxisIndex = 0;
            int topAxisIndex = 1;
            AxisWrapper bottomAxis = xAxisList.get(bottomAxisIndex);
            AxisWrapper topAxis = xAxisList.get(topAxisIndex);

            if (bottomAxis.isVisible() && !topAxis.isVisible()) {
                return XAxisPosition.BOTTOM;
            } else if (!bottomAxis.isVisible() && topAxis.isVisible()) {
                return XAxisPosition.TOP;
            } else if (bottomAxis.isVisible() && topAxis.isVisible()) {
                if (fullArea.contains(point.getX(), point.getY())) {
                    // find point stack
                    int stackCount = yAxisList.size() / 2;
                    for (int i = 0; i < stackCount; i++) {
                        AxisWrapper axisLeft = yAxisList.get(2 * i);
                        if (axisLeft.getEnd() <= point.getY() && axisLeft.getStart() >= point.getY()) {
                            return getXPosition(chooseXAxisWithGrid(i));
                        }
                    }
                }
            }
        }
        return null;
    }

    boolean hoverOff() {
        if (hoverPoint != null) {
            hoverPoint = null;
            return true;
        }
        return false;
    }

    boolean hoverOn(int x, int y) {
        if (!graphArea.contains(x, y)) {
            return hoverOff();
        }
        if (selectedCurve != null) {
            Scale xScale = xAxisList.get(selectedCurve.getTrace().getXIndex()).getScale();
            Scale yScale = yAxisList.get(selectedCurve.getTrace().getYIndex(selectedCurve.getCurve())).getScale();

            NearestCurvePoint nearestCurvePoint = selectedCurve.getTrace().nearest(x, y, selectedCurve.getCurve(), xScale, yScale);
            if (nearestCurvePoint != null) {
                if (nearestCurvePoint.getCurvePoint().equals(hoverPoint)) {
                    return false;
                } else {
                    hoverPoint = nearestCurvePoint.getCurvePoint();
                    updateTooltipAndCrosshair();
                    return true;
                }
            } else if (hoverPoint == null) {
                return false;
            }
            return true;
        }

        if (hoverPoint != null) {
            Scale xScale = xAxisList.get(hoverPoint.getTrace().getXIndex()).getScale();
            Scale yScale = yAxisList.get(hoverPoint.getTrace().getYIndex(hoverPoint.getCurve())).getScale();
            NearestCurvePoint nearestCurvePoint = hoverPoint.getTrace().nearest(x, y, hoverPoint.getCurve(), xScale, yScale);
            if (nearestCurvePoint != null) {
                if (nearestCurvePoint.getCurvePoint().equals(hoverPoint)) {
                    return false;
                } else {
                    hoverPoint = nearestCurvePoint.getCurvePoint();
                    updateTooltipAndCrosshair();
                    return true;
                }
            }
        }

        // find nearest trace curve
        NearestCurvePoint closestTracePoint = null;
        for (Trace trace : traces) {
            Scale[] traceYScales = new Scale[trace.curveCount()];
            for (int curve = 0; curve < trace.curveCount(); curve++) {
                traceYScales[curve] = yAxisList.get(trace.getYIndex(curve)).getScale();
            }
            Scale xScale = xAxisList.get(trace.getXIndex()).getScale();

            NearestCurvePoint nearestCurvePoint = trace.nearest(x, y, xScale, traceYScales);
            if (nearestCurvePoint != null) {
                if (nearestCurvePoint.getDistanceSqw() == 0) {
                    closestTracePoint = nearestCurvePoint;
                    break;
                } else {
                    if (closestTracePoint == null || closestTracePoint.getDistanceSqw() > nearestCurvePoint.getDistanceSqw()) {
                        closestTracePoint = nearestCurvePoint;
                    }
                }
            }
        }

        if (closestTracePoint != null) {
            hoverPoint = closestTracePoint.getCurvePoint();
            updateTooltipAndCrosshair();
            return true;
        }

        return false;
    }

    private void updateTooltipAndCrosshair() {
        if (hoverPoint == null) {
            return;
        }
        Trace hoverTrace = hoverPoint.getTrace();
        int hoverCurve = hoverPoint.getCurve();
        int hoverPointIndex = hoverPoint.getPointIndex();
        int tooltipYPosition = 0;
        Scale xScale = xAxisList.get(hoverTrace.getXIndex()).getScale();
        NamedValue xValue = hoverTrace.xValue(hoverPointIndex, xScale);

        int xPosition;
        int curveStart;
        int curveEnd;
        if (config.isMultiCurveTooltip()) { // all trace curves
            curveStart = 0;
            curveEnd = hoverTrace.curveCount() - 1;
        } else { // only hover curve
            curveStart = hoverCurve;
            curveEnd = hoverCurve;
        }
        Scale yScale = yAxisList.get(hoverTrace.getYIndex(hoverCurve)).getScale();
        BRectangle hoverAreaStart = hoverTrace.curvePointHoverArea(hoverPointIndex, curveStart, xScale, yScale);
        BRectangle hoverAreaEnd = hoverTrace.curvePointHoverArea(hoverPointIndex, curveEnd, xScale, yScale);

        xPosition = (hoverAreaEnd.x + hoverAreaEnd.width + hoverAreaStart.x) / 2;
        crosshair = new Crosshair(config.getCrossHairConfig(), xPosition);
        tooltip = new Tooltip(config.getTooltipConfig(), xPosition, tooltipYPosition);
        tooltip.setHeader(null, null, xValue.getValue());


        for (int curve = curveStart; curve <= curveEnd; curve++) {
            yScale = yAxisList.get(hoverTrace.getYIndex(curve)).getScale();
            NamedValue[] curveValues = hoverTrace.curveValues(hoverPointIndex, curve, xScale, yScale);
            if (curveValues.length == 2) {
                tooltip.addLine(hoverTrace.getCurveColor(curve), hoverTrace.getCurveName(curve), curveValues[1].getValue());
            } else {
                tooltip.addLine(hoverTrace.getCurveColor(curve), hoverTrace.getCurveName(curve), "");
                for (NamedValue curveValue : curveValues) {
                    tooltip.addLine(null, curveValue.getValueName(), curveValue.getValue());
                }
            }
            crosshair.addY(hoverTrace.curvePointHoverArea(hoverPointIndex, curve, xScale, yScale).y);
        }

    }


    /**
     * =================================================*
     * Base methods to interact        *
     * ==================================================
     */
    public void draw(BCanvas canvas) {
        if (fullArea == null) {
            setArea(canvas.getBounds());
        }

        if (fullArea.width == 0 || fullArea.height == 0) {
            return;
        }

        if (isDirty) {
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
            BRectangle stackArea = new BRectangle(graphArea.x, (int) yAxis.getEnd(), graphArea.width, (int) yAxis.length());
            canvas.fillRect(stackArea.x, stackArea.y, stackArea.width, stackArea.height);
        }

        // draw X axes grids
        AxisWrapper bottomAxis = xAxisList.get(0);
        AxisWrapper topAxis = xAxisList.get(1);

        // draw separately for every stack
        for (int i = 0; i < stackCount; i++) {
            AxisWrapper yAxis = yAxisList.get(2 * i);
            BRectangle stackArea = new BRectangle(graphArea.x, (int) yAxis.getEnd(), graphArea.width, (int) yAxis.length());
            if (bottomAxis.isVisible() && !topAxis.isVisible()) {
                bottomAxis.drawGrid(canvas, stackArea);
            } else if (!bottomAxis.isVisible() && topAxis.isVisible()) {
                topAxis.drawGrid(canvas, stackArea);
            } else if (bottomAxis.isVisible() && topAxis.isVisible()) {
                xAxisList.get(chooseXAxisWithGrid(i)).drawGrid(canvas, stackArea);
            }
        }

        // draw Y axes grids
        for (int i = 0; i < stackCount; i++) {
            AxisWrapper leftAxis = yAxisList.get(i * 2);
            AxisWrapper rightAxis = yAxisList.get(i * 2 + 1);

            if (rightAxis.isVisible() && !leftAxis.isVisible()) {
                rightAxis.drawGrid(canvas, graphArea);
            } else if (!rightAxis.isVisible() && leftAxis.isVisible()) {
                leftAxis.drawGrid(canvas, graphArea);
            } else if (rightAxis.isVisible() && leftAxis.isVisible()) {
                if (config.getPrimaryYPosition() == YAxisPosition.LEFT) {
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
            for (int curve = 0; curve < trace.curveCount(); curve++) {
                trace.drawCurve(canvas, curve, xAxisList.get(trace.getXIndex()).getScale(), yAxisList.get(trace.getYIndex(curve)).getScale());
            }
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

    public int stackCount() {
        return yAxisList.size() / 2;
    }


    public void appendData() {
        for (Trace trace : traces) {
            trace.appendData();
        }
        isDirty = true;
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
                if (trace.getCurveName(i).equals(name)) {
                    return new CurveNumber(i, j);
                }
            }
        }
        return null;
    }

    public CurveNumber getSelectedCurveNumber() {
        if (selectedCurve != null) {
            for (int i = 0; i < traces.size(); i++) {
                if (selectedCurve.getTrace() == traces.get(i)) {
                    return new CurveNumber(i, selectedCurve.getCurve());
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

    public void setConfig(ChartConfig chartConfig) {
        setConfig(chartConfig, true);
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
        if (isTraceColorChangeEnabled) {
            int curve = 0;
            for (Trace trace : traces) {
                for (int i = 0; i < trace.curveCount(); i++) {
                    trace.setCurveColor(i, colors[(curve + i) % colors.length]);
                    curve++;
                }
            }
        }
        isDirty = true;
    }


    public void setTitle(String title) {
        this.title.setTitle(title);
        isDirty = true;
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
        isDirty = true;
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

        isDirty = true;
    }

    /**
     * @throws IllegalStateException if stack axis are used by some trace curves and
     *                               therefor can not be deleted
     */
    public void removeStack(int stack) throws IllegalStateException {
        // check that no trace use that stack
        int leftYIndex = stack * 2;
        int rightYIndex = stack * 2 + 1;

        for (Trace trace : traces) {
            for (int curve = 0; curve < trace.curveCount(); curve++) {
                int curveYIndex = trace.getYIndex(curve);
                if(curveYIndex == leftYIndex || curveYIndex == rightYIndex) {
                    String errMsg = "Stack: " + stack + "can not be removed. It is used by trace";
                    throw new IllegalStateException(errMsg);
                }
            }

            if(trace.getYStartIndex() > leftYIndex) {
                trace.setYStartIndex(trace.getYStartIndex() - 2);
            }
        }

        stackWeights.remove(stack);
        yAxisList.remove(stack * 2 + 1);
        yAxisList.remove(stack * 2);
        isDirty = true;
    }

    /**
     * add trace to the last stack
     */
    public void addTrace(ChartData data, TracePainter tracePainter) {
        addTrace(data, tracePainter, true);
    }


    /**
     * add trace to the last stack
     */
    public void addTrace(ChartData data, TracePainter tracePainter, boolean isSplit) {
        int stack = Math.max(0, yAxisList.size() / 2 - 1);
        addTrace(data, tracePainter, isSplit, stack);
    }


    /**
     * add trace to the stack with the given number
     */
    public void addTrace(ChartData data, TracePainter tracePainter, boolean isSplit, int stack) {
        addTrace(data, tracePainter, isSplit, stack, config.getPrimaryXPosition(), config.getPrimaryYPosition());
    }

    public void addTrace(ChartData data, TracePainter tracePainter, boolean isSplit, XAxisPosition xPosition, YAxisPosition yPosition) {
        int stack = Math.max(0, yAxisList.size() / 2 - 1);
        addTrace(data, tracePainter, isSplit, stack, xPosition, yPosition);
    }

    /**
     * add trace to the stack with the given number
     */
    public void addTrace(ChartData data, TracePainter tracePainter, boolean isSplit, int stack, XAxisPosition xPosition, YAxisPosition yPosition) throws IllegalArgumentException {
        Trace trace = new Trace(data, tracePainter, isSplit, dataProcessingConfig, getXIndex(xPosition), getYIndex(stack, yPosition));
        if (trace.curveCount() < 1) {
            String errMsg = "Number of trace curves: " + trace.curveCount() + ". Please specify valid trace data";
            throw new IllegalArgumentException(errMsg);
        }

        if (yAxisList.size() == 0) {
            addStack(); // add stack if there is no stack
        }
        checkStackNumber(stack);

        int xIndex = getXIndex(xPosition);
        int yIndex = getYIndex(stack, yPosition);

        AxisWrapper xAxis = xAxisList.get(xIndex);
        StringSequence dataLabels = trace.getLabelsIfXColumnIsString();

        if (dataLabels != null && xAxis.getScale() instanceof CategoryScale) {
            CategoryScale scale = (CategoryScale) xAxis.getScale();
            StringSequence scaleLabels = scale.getLabels();
            if (scaleLabels == null) {
                scale.setLabels(dataLabels);
            }
        }

        xAxis.setVisible(true);
        if (!isSplit) {
            AxisWrapper yAxis = yAxisList.get(yIndex);
            yAxis.setVisible(true);
        } else {
            int stackCount = yAxisList.size() / 2;
            int availableStacks = stackCount - stack;
            if (trace.curveCount() > availableStacks) {
                for (int i = 0; i < trace.curveCount() - availableStacks; i++) {
                    addStack();
                }
            }
            for (int i = 0; i < trace.curveCount(); i++) {
                AxisWrapper yAxis = yAxisList.get(yIndex + i * 2);
                yAxis.setVisible(true);
            }
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

        if (isLegendEnabled()) {
            for (int i = 0; i < trace.curveCount(); i++) {
                final int curveNumber = i;
                StateListener traceSelectionListener = new StateListener() {
                    @Override
                    public void stateChanged(boolean isSelected) {
                        if (isSelected) {
                            selectedCurve = new TraceCurve(trace, curveNumber);
                        }
                        if (!isSelected && selectedCurve.getTrace() == trace && selectedCurve.getCurve() == curveNumber) {
                            selectedCurve = null;
                        }
                    }
                };
                legend.add(trace, curveNumber, traceSelectionListener);
            }
        }
        isDirty = true;
    }

    public void removeTrace(int traceNumber) {
        Trace trace = traces.get(traceNumber);
        if (isLegendEnabled()) {
            legend.remove(trace);
        }
        traces.remove(traceNumber);
        // remove the stacks used by the trace curves if that stacks
        // are not used anymore
        int traceStartStack = trace.getYIndex(0) / 2;
        int traceStackCount = 1;
        if(trace.isSplit()) {
            traceStackCount = trace.curveCount();
        }
        for (int i = traceStartStack + traceStackCount - 1; i >= traceStartStack; i--) {
            try {
                removeStack(i);
            } catch (IllegalStateException ex) {
                // do nothing;
            }
        }
        isDirty = true;
    }

    public void setArea(BRectangle area) {
        fullArea = area;
        title.setArea(area);
        isDirty = true;
    }

    public int traceCount() {
        return traces.size();
    }

    public void setXConfig(XAxisPosition xPosition, AxisConfig axisConfig) {
        xAxisList.get(getXIndex(xPosition)).setConfig(axisConfig);
        isDirty = true;
    }

    public void setYConfig(int stack, YAxisPosition yPosition, AxisConfig axisConfig) {
        yAxisList.get(getYIndex(stack, yPosition)).setConfig(axisConfig);
        isDirty = true;
    }

    /**
     * return COPY of X axis config. To change axis config use setXConfig
     */
    public AxisConfig getXConfig(XAxisPosition xPosition) {
        return xAxisList.get(getXIndex(xPosition)).getConfig();
    }

    /**
     * return COPY of Y axis config. To change axis config use setYConfig
     */
    public AxisConfig getYConfig(int stack, YAxisPosition yPosition) {
        return yAxisList.get(getYIndex(stack, yPosition)).getConfig();
    }

    public void setXTitle(XAxisPosition xPosition, @Nullable String title) {
        xAxisList.get(getXIndex(xPosition)).setTitle(title);
        isDirty = true;
    }

    public void setYTitle(int stack, YAxisPosition yPosition, @Nullable String title) {
        yAxisList.get(getYIndex(stack, yPosition)).setTitle(title);
        isDirty = true;
    }


    public void setXMinMax(XAxisPosition xPosition, double min, double max) {
        xAxisToMinMax.put(getXIndex(xPosition), new Range(min, max));
        isDirty = true;
    }

    public void setYMinMax(int stack, YAxisPosition yPosition, double min, double max) {
        yAxisToMinMax.put(getYIndex(stack, yPosition), new Range(min, max));
        isDirty = true;
    }


    public void autoScaleX(XAxisPosition xPosition) {
        xAxisToMinMax.remove(getXIndex(xPosition));
        isDirty = true;
    }

    public void autoScaleY(int stack, YAxisPosition yPosition) {
        yAxisToMinMax.remove(getYIndex(stack, yPosition));
        isDirty = true;
    }

    public void setXScale(XAxisPosition xPosition, Scale scale) {
        AxisWrapper axis = xAxisList.get(getXIndex(xPosition));
        axis.setScale(scale);
        isDirty = true;
    }

    public void setYScale(int stack, YAxisPosition yPosition, Scale scale) {
        AxisWrapper axis = yAxisList.get(getYIndex(stack, yPosition));
        axis.setScale(scale);
        isDirty = true;
    }


    public void zoomY(int stack, YAxisPosition yPosition, double zoomFactor) {
        Scale zoomedScale = yAxisList.get(getYIndex(stack, yPosition)).zoom(zoomFactor);
        double zoomedMin = zoomedScale.getDomain()[0];
        double zoomedMax = zoomedScale.getDomain()[zoomedScale.getDomain().length - 1];
        setYMinMax(stack, yPosition, zoomedMin, zoomedMax);
    }

    public void zoomX(XAxisPosition xPosition, double zoomFactor) {
        Scale zoomedScale = xAxisList.get(getXIndex(xPosition)).zoom(zoomFactor);
        double zoomedMin = zoomedScale.getDomain()[0];
        double zoomedMax = zoomedScale.getDomain()[zoomedScale.getDomain().length - 1];
        setXMinMax(xPosition, zoomedMin, zoomedMax);
    }

    public void translateY(int stack, YAxisPosition yPosition, int translation) {
        Scale translatedScale = yAxisList.get(getYIndex(stack, yPosition)).translate(translation);
        double translatedMin = translatedScale.getDomain()[0];
        double translatedMax = translatedScale.getDomain()[translatedScale.getDomain().length - 1];
        setYMinMax(stack, yPosition, translatedMin, translatedMax);
    }

    public void translateX(XAxisPosition xPosition, int translation) {
        Scale translatedScale = xAxisList.get(getXIndex(xPosition)).translate(translation);
        double translatedMin = translatedScale.getDomain()[0];
        double translatedMax = translatedScale.getDomain()[translatedScale.getDomain().length - 1];
        setXMinMax(xPosition, translatedMin, translatedMax);
    }


    public boolean selectCurve(int x, int y) {
        if (isLegendEnabled() && legend.selectItem(x, y)) {
            return true;
        }
        return false;
    }


    class Legend {
        private ButtonGroup buttonGroup;
        // only LinkedHashMap will iterate in the order in which the entries were put into the map
        private Map<TraceCurve, SwitchButton> traceCurvesToButtons = new LinkedHashMap<>();
        private BRectangle area;
        private int height;

        private LegendConfig config;
        private boolean isDirty = true;

        public Legend(LegendConfig legendConfig) {
            this.config = legendConfig;
            this.buttonGroup = new ButtonGroup();
        }

        public boolean isEnabled() {
            return config.isEnabled();
        }

        public int getHeight(BCanvas canvas) {
            if(!config.isAttachedToStacks()) {
                if(isDirty) {
                    arrangeButtons(canvas);
                }
                return height;
            }
            return 0;
        }

        public boolean isAttachedToStacks() {
            return config.isAttachedToStacks();
        }

        public boolean isTop() {
            if(config.getVerticalAlign() == VerticalAlign.TOP) {
                return true;
            }
            return false;
        }

        public boolean isBottom() {
            if(config.getVerticalAlign() == VerticalAlign.BOTTOM) {
                return true;
            }
            return false;
        }

        public boolean selectItem(int x, int y) {
            for (TraceCurve key : traceCurvesToButtons.keySet()){
                SwitchButton button = traceCurvesToButtons.get(key);
                if(button.contains(x, y)) {
                    button.switchState();
                    return true;
                }
            }
            return false;
        }

        public  void setArea(BRectangle area) {
            this.area = area;
            isDirty = true;
        }

        public void setCurveName(Trace trace, int curveNumber, String name) {
            for (TraceCurve key : traceCurvesToButtons.keySet()){
                if(key.getTrace() == trace && key.getCurve() == curveNumber) {
                    SwitchButton button = traceCurvesToButtons.get(key);
                    button.setLabel(name);
                    isDirty = true;
                    return;
                }
            }
        }


        public void setConfig(LegendConfig legendConfig) {
            config = legendConfig;
            for (TraceCurve key : traceCurvesToButtons.keySet()){
                SwitchButton button = traceCurvesToButtons.get(key);
                button.setBackgroundColor(config.getBackgroundColor());
                button.setTextStyle(config.getTextStyle());
                button.setMargin(config.getPadding());
            }
            isDirty = true;
        }

        public void add(Trace trace, int curveNumber, StateListener traceSelectionListener) {
            // add curve legend button
            TraceCurve traceCurve = new TraceCurve(trace, curveNumber);
            SwitchButton traceButton = new SwitchButton(trace.getCurveName(curveNumber));
            traceButton.addListener(traceSelectionListener);
            traceCurvesToButtons.put(traceCurve, traceButton);
            buttonGroup.add(traceButton.getModel());
            traceButton.setBackgroundColor(config.getBackgroundColor());
            traceButton.setTextStyle(config.getTextStyle());
            traceButton.setMargin(config.getPadding());
            isDirty = true;
        }

        public void remove(Trace trace) {
            for (int i = 0; i < trace.curveCount(); i++) {
                TraceCurve traceCurve = new TraceCurve(trace, i);
                SwitchButton traceButton = traceCurvesToButtons.get(traceCurve);
                buttonGroup.remove(traceButton.getModel());
                traceCurvesToButtons.remove(traceCurve);
            }

            isDirty = true;
        }

        private BRectangle getTraceCurveArea(TraceCurve traceCurve) {
            if(!config.isAttachedToStacks()) {
                return  area;
            }

            double[] yRange = yAxisList.get(traceCurve.getTrace().getYIndex(traceCurve.getCurve())).getScale().getRange();
            int yStart = (int)yRange[0];
            int yEnd = (int)yRange[yRange.length - 1];
            return new BRectangle(area.x, yEnd, area.width, Math.abs(yStart - yEnd));
        }

        private void arrangeButtons(BCanvas canvas) {
            // only LinkedHashMap will iterate in the order in which the entries were put into the map
            Map<BRectangle, List<TraceCurve>> areasToTraces = new LinkedHashMap<>();
            for (TraceCurve traceCurve : traceCurvesToButtons.keySet()) {
                BRectangle traceArea = getTraceCurveArea(traceCurve);
                List<TraceCurve> traceCurves = areasToTraces.get(traceArea);
                if(traceCurves == null) {
                    traceCurves = new ArrayList<>();
                    areasToTraces.put(traceArea, traceCurves);
                }
                traceCurves.add(traceCurve);
            }

            List<SwitchButton> lineButtons = new ArrayList<SwitchButton>();
            Insets margin = config.getMargin();
            for (BRectangle area : areasToTraces.keySet()) {
                List<TraceCurve> traceCurves = areasToTraces.get(area);
                height = 0;
                int width = 0;
                int x = area.x;
                int y = area.y;
                for (TraceCurve traceCurve : traceCurves) {
                    SwitchButton button = traceCurvesToButtons.get(traceCurve);
                    BRectangle btnArea = button.getBounds(canvas);
                    if(height == 0) {
                        height = btnArea.height;
                        lineButtons.clear();
                    }
                    if(lineButtons.size() > 0 && x + config.getInterItemSpace() + btnArea.width >= area.x + area.width - margin.left() - margin.right()) {
                        width += (lineButtons.size() - 1) * config.getInterItemSpace();
                        if(config.getHorizontalAlign() == HorizontalAlign.LEFT) {
                            moveButtons(lineButtons, margin.left(),0);
                        }
                        if(config.getHorizontalAlign() == HorizontalAlign.RIGHT) {
                            moveButtons(lineButtons, area.width - width - margin.right(),0);
                        }
                        if(config.getHorizontalAlign() == HorizontalAlign.CENTER) {
                            moveButtons(lineButtons, (area.width - width) / 2,0);
                        }

                        x = area.x;
                        y += btnArea.height + config.getInterLineSpace();
                        button.setLocation(x, y, canvas);

                        x += btnArea.width + config.getInterItemSpace();
                        height += btnArea.height + config.getInterLineSpace();
                        width = btnArea.width;
                        lineButtons.clear();
                        lineButtons.add(button);
                    } else {
                        button.setLocation(x, y, canvas);
                        x += config.getInterItemSpace() + btnArea.width;
                        width += btnArea.width;
                        lineButtons.add(button);
                    }
                }
                width += (lineButtons.size() - 1) * config.getInterItemSpace();
                if(config.getHorizontalAlign() == HorizontalAlign.LEFT) {
                    moveButtons(lineButtons, margin.left(),0);
                }
                if(config.getHorizontalAlign() == HorizontalAlign.RIGHT) {
                    moveButtons(lineButtons, area.width - width - margin.right(),0);
                }
                if(config.getHorizontalAlign() == HorizontalAlign.CENTER) {
                    moveButtons(lineButtons, (area.width - width) / 2,0);
                }

                if(config.getVerticalAlign() == VerticalAlign.TOP) {
                    moveTracesButtons(traceCurves, 0, margin.top());
                }
                if(config.getVerticalAlign() == VerticalAlign.BOTTOM) {
                    moveTracesButtons(traceCurves, 0, area.height - height - margin.bottom());
                }
                if(config.getVerticalAlign() == VerticalAlign.MIDDLE) {
                    moveTracesButtons(traceCurves, 0, (area.height - height)/2);
                }
            }
            height += margin.top() + margin.bottom();
            isDirty = false;
        }

        private void moveTracesButtons(List<TraceCurve> curves, int dx, int dy) {
            if(dx != 0 || dy != 0) {
                for (TraceCurve curve : curves) {
                    traceCurvesToButtons.get(curve).moveLocation(dx, dy);
                }
            }
        }
        private void moveButtons(List<SwitchButton> buttons, int dx, int dy) {
            if(dx != 0 || dy != 0) {
                for (SwitchButton button : buttons) {
                    button.moveLocation(dx, dy);
                }
            }
        }



        public void draw(BCanvas canvas) {
            if (traceCurvesToButtons.size() == 0) {
                return;
            }
            if(isDirty) {
                arrangeButtons(canvas);
            }
            for (TraceCurve traceCurve : traceCurvesToButtons.keySet()){
                traceCurvesToButtons.get(traceCurve).setColor(traceCurve.getTrace().getCurveColor(traceCurve.getCurve()));
            }
            canvas.setTextStyle(config.getTextStyle());
            for (TraceCurve key : traceCurvesToButtons.keySet()) {
                traceCurvesToButtons.get(key).draw(canvas);
            }
        }
    }

}
