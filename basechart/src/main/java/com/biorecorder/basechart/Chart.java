package com.biorecorder.basechart;

import com.biorecorder.basechart.axis.*;
import com.biorecorder.basechart.button.ButtonGroup;
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
    private List<DataPainter> dataPainters = new ArrayList<DataPainter>();
    private Legend legend;
    private Title title;
    private BRectangle fullArea;
    private BRectangle graphArea;
    private Insets margin;

    private Crosshair crosshair;
    private Tooltip tooltip;

    private DataPainterTracePoint hoverPoint;
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
                if (axis.isUsed() && (axis.isTickLabelOutside() || !StringUtils.isNullOrBlank(axis.getTitle()))) {
                    spacingLeft = config.getAutoSpacing();
                }
            } else { // right
                if (axis.isUsed() && (axis.isTickLabelOutside() || !StringUtils.isNullOrBlank(axis.getTitle()))) {
                    spacingRight = config.getAutoSpacing();
                }
            }
        }

        for (int i = 0; i < xAxisList.size(); i++) {
            AxisWrapper axis = xAxisList.get(i);
            if (i % 2 == 0) { // bottom
                if (axis.isUsed() && (axis.isTickLabelOutside() || !StringUtils.isNullOrBlank(axis.getTitle()))) {
                    spacingBottom = config.getAutoSpacing();
                }
            } else { // top
                if (axis.isUsed() && (axis.isTickLabelOutside() || !StringUtils.isNullOrBlank(axis.getTitle()))) {
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
            AxisWrapper xAxis = xAxisList.get(xIndex);
            if(xAxis.isUsed()) {
                Range minMax = xAxisToMinMax.get(xIndex);
                if (minMax != null) {
                    // NO ROUNDING !!! course the X min and max depends data processing
                    xAxis.setMinMax(minMax.getMin(), minMax.getMax());
                } else { // auto scale X
                    Range tracesXMinMax = null;
                    for (DataPainter trace : dataPainters) {
                        if (trace.getXIndex() == xIndex) {
                            tracesXMinMax = Range.join(tracesXMinMax, trace.getFullXMinMax());
                        }
                    }

                    if (tracesXMinMax != null) {
                        if(xAxis.getScale() instanceof CategoryScale) {
                            CategoryScale xScale = (CategoryScale)xAxis.getScale();
                            xScale.setMinMax(xScale.normalizeMin(tracesXMinMax.getMin()), xScale.normalizeMax(tracesXMinMax.getMax()));
                        } else {
                            xAxis.setMinMax(tracesXMinMax.getMin(), tracesXMinMax.getMax());

                        }
                    }
                    // rounding only in the case of auto scale when no data processing
                    if(xAxis.isRoundingEnabled()) {
                        xAxis.roundMinMax(canvas);
                    }
                }
            }
        }
    }

    private void setYMinMax(BCanvas canvas) {
        for (int yIndex = 0; yIndex < yAxisList.size(); yIndex++) {
            AxisWrapper yAxis = yAxisList.get(yIndex);
            if(yAxis.isUsed()) {
                Range minMax = yAxisToMinMax.get(yIndex);
                if (minMax != null) {
                    yAxis.setMinMax(minMax.getMin(), minMax.getMax());
                } else { // auto scale Y
                    Range tracesYMinMax = null;
                    for (DataPainter dataPainter : dataPainters) {
                        int traceCount = dataPainter.traceCount();
                        for (int trace = 0; trace < traceCount; trace++) {
                            if (getTraceYIndex(dataPainter,  trace) == yIndex) {
                                tracesYMinMax = Range.join(tracesYMinMax, dataPainter.traceYMinMax(trace, xAxisList.get(dataPainter.getXIndex()).getScale(), yAxis.getScale()));
                            }
                        }
                    }
                    if (tracesYMinMax != null) {
                        if(yAxis.getScale() instanceof CategoryScale) {
                            CategoryScale yScale = (CategoryScale)yAxis.getScale();
                            yScale.setMinMax(yScale.normalizeMin(tracesYMinMax.getMin()), yScale.normalizeMax(tracesYMinMax.getMax()));
                        } else {
                            yAxis.setMinMax(tracesYMinMax.getMin(), tracesYMinMax.getMax());
                            // rounding only in the case of auto scale
                            if(yAxis.isRoundingEnabled()) {
                                yAxis.roundMinMax(canvas);
                            }
                        }
                    }
                }

            }
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

        int top = spacing.top() + titleHeight;
        int bottom = spacing.bottom();
        AxisWrapper topAxis = xAxisList.get(getXIndex(XAxisPosition.TOP));
        AxisWrapper bottomAxis = xAxisList.get(getXIndex(XAxisPosition.BOTTOM));
        if(topAxis.isUsed()) {
            top += topAxis.getWidth(canvas);
        }
        if(bottomAxis.isUsed()) {
            bottom += bottomAxis.getWidth(canvas);
        }

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
            AxisWrapper yAxis = yAxisList.get(i);
            if(yAxis.isUsed()) {
                if (i % 2 == 0) {
                    left = Math.max(left, yAxis.getWidth(canvas));
                } else {
                    right = Math.max(right, yAxis.getWidth(canvas));
                }
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
            AxisWrapper leftAxis = yAxisList.get(stack * 2);
            AxisWrapper rightAxis = yAxisList.get(stack * 2 + 1);
            leftAxis.setStartEnd(start, end);
            rightAxis.setStartEnd(start, end);
            end = start + gap;
        }
    }

    private int chooseXAxisWithGrid(int stack) {
        int primaryAxisIndex = getXIndex(config.getPrimaryXPosition());

        int leftAxisIndex = getYIndex(stack, YAxisPosition.LEFT);
        int rightAxisIndex = getYIndex(stack, YAxisPosition.RIGHT);
        for (DataPainter dataPainter : dataPainters) {
            if (dataPainter.getXIndex() == primaryAxisIndex) {
                for (int trace = 0; trace < dataPainter.traceCount(); trace++) {
                    int traceYIndex = getTraceYIndex( dataPainter,  trace);
                    if (traceYIndex == leftAxisIndex || traceYIndex == rightAxisIndex) {
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

    int getTraceYIndex(DataPainter dataPainter, int trace) {
        if(dataPainter.isSplit()) {
            return dataPainter.getYStartIndex() + trace * 2;
        } else {
            return dataPainter.getYStartIndex();
        }
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
        for (DataPainter trace : dataPainters) {
            if (trace.getXIndex() == getXIndex(xAxisPosition)) {
                double traceExtent = trace.getBestExtent(fullArea.width);
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
        for (DataPainter trace : dataPainters) {
            Range traceXMinMax = trace.getFullXMinMax();
            AxisWrapper traceXAxis = xAxisList.get(trace.getXIndex());
            if(traceXMinMax != null && traceXAxis.getScale() instanceof CategoryScale){
                CategoryScale xScale = (CategoryScale) traceXAxis.getScale();
                traceXMinMax = new Range(xScale.normalizeMin(traceXMinMax.getMin()), xScale.normalizeMax(traceXMinMax.getMax()));
            }

            minMax = Range.join(minMax, traceXMinMax);
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

    Range getYMinMax(int stack, YAxisPosition yAxisPosition, BCanvas canvas) {
        if (isDirty) {
            doCalculations(canvas);
        }
        AxisWrapper yAxis = yAxisList.get(getYIndex(stack, yAxisPosition));
        return new Range(yAxis.getMin(), yAxis.getMax());
    }

    private DataPainterTrace getSelectedTrace() {
        if(legend != null) {
            return legend.getSelectedTrace();
        }
        return null;
    }

    public boolean isTraceSelected() {
        return getSelectedTrace() != null;
    }

    XAxisPosition getSelectedTraceX() {
        return getXPosition(getSelectedTrace().getDataPainter().getXIndex());
    }

    int getSelectedTraceStack() {
        DataPainterTrace selectedTrace = getSelectedTrace();
        int yIndex = getTraceYIndex(selectedTrace.getDataPainter(), selectedTrace.getTrace());
        return getYStack(yIndex);
    }


    YAxisPosition getSelectedTraceY() {
        DataPainterTrace selectedTrace = getSelectedTrace();
        int yIndex = getTraceYIndex(selectedTrace.getDataPainter(), selectedTrace.getTrace());
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
        List<XAxisPosition> positions = new ArrayList<>();
        for (XAxisPosition position : XAxisPosition.values()) {
            if(xAxisList.get(getXIndex(position)).isUsed()) {
                positions.add(position);
            }

        }
        return positions.toArray(new XAxisPosition[positions.size()]);
    }

    YAxisPosition[] getYAxes(int stack) {
        List<YAxisPosition> positions = new ArrayList<>();
        for (YAxisPosition position : YAxisPosition.values()) {
            if(yAxisList.get(getYIndex(stack, position)).isUsed()) {
                positions.add(position);
            }

        }
        return positions.toArray(new YAxisPosition[positions.size()]);
    }


    YAxisPosition getYAxis(int stack, BPoint point) {
        if (fullArea != null && fullArea.contains(point.getX(), point.getY())) {
            // find axis position
            AxisWrapper axisLeft = yAxisList.get(2 * stack);
            AxisWrapper axisRight = yAxisList.get(2 * stack + 1);
            if (axisLeft.getEnd() <= point.getY() && axisLeft.getStart() >= point.getY()) {
                if (axisLeft.getEnd() <= point.getY() && axisLeft.getStart() >= point.getY()) {
                    if (!axisLeft.isUsed() && !axisRight.isUsed()) {
                        return null;
                    }
                    if (!axisLeft.isUsed()) {
                        return YAxisPosition.RIGHT;
                    }
                    if (!axisRight.isUsed()) {
                        return YAxisPosition.LEFT;
                    }
                    if (fullArea.x <= point.getX() && point.getX() <= fullArea.x + fullArea.width / 2 && axisLeft.isUsed()) { // left half
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
            int bottomAxisIndex = getXIndex(XAxisPosition.BOTTOM);
            int topAxisIndex = getXIndex(XAxisPosition.TOP);;
            AxisWrapper bottomAxis = xAxisList.get(bottomAxisIndex);
            AxisWrapper topAxis = xAxisList.get(topAxisIndex);
            if(!bottomAxis.isUsed() && !topAxis.isUsed()) {
                return null;
            } else if (!topAxis.isUsed()) {
                return XAxisPosition.BOTTOM;
            } else if (!bottomAxis.isUsed()) {
                return XAxisPosition.TOP;
            } else { // both axis is used
                // find point stack
                int stackCount = yAxisList.size() / 2;
                for (int stack = 0; stack < stackCount; stack++) {
                    AxisWrapper axisLeft = yAxisList.get(2 * stack);
                    if (axisLeft.getEnd() <= point.getY() && axisLeft.getStart() >= point.getY()) {
                        return getXPosition(chooseXAxisWithGrid(stack));
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
        DataPainterTrace selectedTrace = getSelectedTrace();
        if (selectedTrace != null) {
            Scale xScale = xAxisList.get(selectedTrace.getDataPainter().getXIndex()).getScale();
            Scale yScale = yAxisList.get(getTraceYIndex(selectedTrace.getDataPainter(), selectedTrace.getTrace())).getScale();

            NearestTracePoint nearestTracePoint = selectedTrace.getDataPainter().nearest(x, y, selectedTrace.getTrace(), xScale, yScale);
            if (nearestTracePoint != null) {
                if (nearestTracePoint.getTracePoint().equals(hoverPoint)) {
                    return false;
                } else {
                    hoverPoint = nearestTracePoint.getTracePoint();
                    updateTooltipAndCrosshair();
                    return true;
                }
            } else if (hoverPoint == null) {
                return false;
            }
            return true;
        }

        if (hoverPoint != null) {
            Scale xScale = xAxisList.get(hoverPoint.getDataPainter().getXIndex()).getScale();
            Scale yScale = yAxisList.get(getTraceYIndex(hoverPoint.getDataPainter(), hoverPoint.getTrace())).getScale();
            NearestTracePoint nearestTracePoint = hoverPoint.getDataPainter().nearest(x, y, hoverPoint.getTrace(), xScale, yScale);
            if (nearestTracePoint != null) {
                if (nearestTracePoint.getTracePoint().equals(hoverPoint)) {
                    return false;
                } else {
                    hoverPoint = nearestTracePoint.getTracePoint();
                    updateTooltipAndCrosshair();
                    return true;
                }
            }
        }

        // find nearest trace trace
        NearestTracePoint closestTracePoint = null;
        for (DataPainter dataPainter : dataPainters) {
            Scale[] traceYScales = new Scale[dataPainter.traceCount()];
            for (int trace = 0; trace < dataPainter.traceCount(); trace++) {
                traceYScales[trace] = yAxisList.get(getTraceYIndex(dataPainter,  trace)).getScale();
            }
            Scale xScale = xAxisList.get(dataPainter.getXIndex()).getScale();

            NearestTracePoint nearestTracePoint = dataPainter.nearest(x, y, xScale, traceYScales);
            if (nearestTracePoint != null) {
                if (nearestTracePoint.getDistanceSqw() == 0) {
                    closestTracePoint = nearestTracePoint;
                    break;
                } else {
                    if (closestTracePoint == null || closestTracePoint.getDistanceSqw() > nearestTracePoint.getDistanceSqw()) {
                        closestTracePoint = nearestTracePoint;
                    }
                }
            }
        }

        if (closestTracePoint != null) {
            hoverPoint = closestTracePoint.getTracePoint();
            updateTooltipAndCrosshair();
            return true;
        }

        return false;
    }

    private void updateTooltipAndCrosshair() {
        if (hoverPoint == null) {
            return;
        }
        DataPainter hoverDataPainter = hoverPoint.getDataPainter();
        int hoverTrace = hoverPoint.getTrace();
        int hoverPointIndex = hoverPoint.getPointIndex();
        int tooltipYPosition = 0;
        Scale xScale = xAxisList.get(hoverDataPainter.getXIndex()).getScale();
        NamedValue xValue = hoverDataPainter.xValue(hoverPointIndex, xScale);

        int xPosition;
        int traceStart;
        int traceEnd;
        if (config.isMultiTraceTooltip()) { // all trace traces
            traceStart = 0;
            traceEnd = hoverDataPainter.traceCount() - 1;
        } else { // only hover trace
            traceStart = hoverTrace;
            traceEnd = hoverTrace;
        }
        Scale yScaleStart = yAxisList.get(getTraceYIndex(hoverDataPainter, traceStart)).getScale();
        Scale yScaleEnd = yAxisList.get(getTraceYIndex(hoverDataPainter, traceEnd)).getScale();

        BRectangle hoverAreaStart = hoverDataPainter.tracePointHoverArea(hoverPointIndex, traceStart, xScale, yScaleStart);
        BRectangle hoverAreaEnd = hoverDataPainter.tracePointHoverArea(hoverPointIndex, traceEnd, xScale, yScaleEnd);

        xPosition = (hoverAreaEnd.x + hoverAreaEnd.width + hoverAreaStart.x) / 2;
        crosshair = new Crosshair(config.getCrossHairConfig(), xPosition);
        tooltip = new Tooltip(config.getTooltipConfig(), xPosition, tooltipYPosition);
        tooltip.setHeader(null, null, xValue.getValue());


        for (int trace = traceStart; trace <= traceEnd; trace++) {
            Scale yScale = yAxisList.get(getTraceYIndex(hoverDataPainter, trace)).getScale();
            NamedValue[] traceValues = hoverDataPainter.traceValues(hoverPointIndex, trace, xScale, yScale);
            if (traceValues.length == 2) {
                tooltip.addLine(hoverDataPainter.getTraceColor(trace), hoverDataPainter.getTraceName(trace), traceValues[1].getValue());
            } else {
                tooltip.addLine(hoverDataPainter.getTraceColor(trace), hoverDataPainter.getTraceName(trace), "");
                for (NamedValue traceValue : traceValues) {
                    tooltip.addLine(null, traceValue.getValueName(), traceValue.getValue());
                }
            }
            BRectangle traceArea = hoverDataPainter.tracePointHoverArea(hoverPointIndex, trace, xScale, yScale);
            crosshair.addY(traceArea.y);
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

        // draw X axes grids separately for every stack

        for (int stack = 0; stack < stackCount; stack++) {
            AxisWrapper yAxis = yAxisList.get(2 * stack);
            BRectangle stackArea = new BRectangle(graphArea.x, (int) yAxis.getEnd(), graphArea.width, (int) yAxis.length());

            int bottomAxisIndex = getXIndex(XAxisPosition.BOTTOM);
            int topAxisIndex = getXIndex(XAxisPosition.TOP);;
            AxisWrapper bottomAxis = xAxisList.get(bottomAxisIndex);
            AxisWrapper topAxis = xAxisList.get(topAxisIndex);

            if(!bottomAxis.isUsed() && !topAxis.isUsed()) {
                // do nothing
            } else if(!bottomAxis.isUsed()) {
                topAxis.drawGrid(canvas, stackArea);
            } else if(!topAxis.isUsed()) {
                bottomAxis.drawGrid(canvas, stackArea);
            } else { // both axis used
                AxisWrapper xAxisWithGrid = xAxisList.get(chooseXAxisWithGrid(stack));
                if(xAxisWithGrid.isUsed()) {
                    xAxisWithGrid.drawGrid(canvas, stackArea);
                }
            }
        }

        // draw Y axes grids
        for (int i = 0; i < stackCount; i++) {
            AxisWrapper leftAxis = yAxisList.get(getYIndex(i, YAxisPosition.LEFT));
            AxisWrapper rightAxis = yAxisList.get(getYIndex(i, YAxisPosition.RIGHT));

            if(!rightAxis.isUsed() && !leftAxis.isUsed()) {
               // do nothing
            } else if (!leftAxis.isUsed()) {
                rightAxis.drawGrid(canvas, graphArea);
            } else if (!rightAxis.isUsed() ) {
                leftAxis.drawGrid(canvas, graphArea);
            } else { // both axis is used
                if (config.getPrimaryYPosition() == YAxisPosition.LEFT) {
                    leftAxis.drawGrid(canvas, graphArea);
                } else {
                    rightAxis.drawGrid(canvas, graphArea);
                }
            }
        }

        // draw X axes
        for (AxisWrapper axis : xAxisList) {
            if(axis.isUsed()) {
                axis.drawAxis(canvas, graphArea);
            }
        }

        // draw Y axes
        for (AxisWrapper axis : yAxisList) {
            if(axis.isUsed()) {
                axis.drawAxis(canvas, graphArea);
            }
        }
        canvas.save();
        canvas.setClip(graphArea.x, graphArea.y, graphArea.width, graphArea.height);

        for (DataPainter dataPainter : dataPainters) {
            for (int trace = 0; trace < dataPainter.traceCount(); trace++) {
                dataPainter.drawTrace(canvas, trace, xAxisList.get(dataPainter.getXIndex()).getScale(), yAxisList.get(getTraceYIndex(dataPainter,  trace)).getScale());
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
        for (DataPainter dataPainter : dataPainters) {
            dataPainter.appendData();
        }
        isDirty = true;
    }

    public String[] getTraceNames() {
        List<String> names = new ArrayList<>();
        for (DataPainter trace : dataPainters) {
            for (int i = 0; i < trace.traceCount(); i++) {
                names.add(trace.getTraceName(i));
            }
        }
        String[] namesArr = new String[names.size()];
        return names.toArray(namesArr);
    }

    public int dataPainterTraceCount(int traceNumber) {
        return dataPainters.get(traceNumber).traceCount();
    }

    private int dataPainterTraceToGeneralTraceNumber(DataPainterTrace dataPainterTrace) throws IllegalArgumentException {
        int traceCount = 0;
        for (DataPainter dataPainter : dataPainters) {
            if(dataPainter == dataPainterTrace.getDataPainter()) {
                traceCount += dataPainterTrace.getTrace();
                return traceCount;
            }
            traceCount += dataPainter.traceCount();
        }
        String errMsg = "Invalid DataPainterTrace. Corresponding DataPainter does not exist";
        throw new IllegalArgumentException(errMsg);
    }

    private DataPainterTrace generalTraceNumberToDataPainterTrace(int trace) throws IllegalArgumentException{
        int traceCount = 0;
        for (DataPainter dataPainter : dataPainters) {
            if(trace < traceCount + dataPainter.traceCount()) {
                return new DataPainterTrace(dataPainter, trace - traceCount);
            }
            traceCount += dataPainter.traceCount();
        }
        String errMsg = "Invalid trace number. No DataPainter corresponds it: "+trace;
        throw new IllegalArgumentException(errMsg);


    }

    public int getTraceNumberByName(String name) {
        for (int i = 0; i < dataPainters.size(); i++) {
            DataPainter dataPainter = dataPainters.get(i);
            for (int trace = 0; trace < dataPainter.traceCount(); trace++) {
                if (dataPainter.getTraceName(trace).equals(name)) {
                    return dataPainterTraceToGeneralTraceNumber(new DataPainterTrace(dataPainter, trace));
                }
            }
        }
        return -1;
    }

    public int getSelectedTraceNumber() {
        DataPainterTrace selectedTrace = getSelectedTrace();
        if (selectedTrace != null) {
            return dataPainterTraceToGeneralTraceNumber(selectedTrace);
        }
        return -1;
    }

    /**
     * return COPY of chart legendConfig. To change chart legendConfig use setConfig
     */
    public ChartConfig getConfig() {
        return new ChartConfig(config);
    }

    public void setConfig(ChartConfig chartConfig) {
        setConfig(chartConfig, true);
    }

    /**
     * if isTraceColorChangeEnabled is true all traces colors will be
     * changed according with the legendConfig traceColors.
     * Otherwise trace colors will stay as they are.
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
            int trace = 0;
            for (DataPainter dataPainter : dataPainters) {
                for (int i = 0; i < dataPainter.traceCount(); i++) {
                    dataPainter.setTraceColor(i, colors[(trace + i) % colors.length]);
                    trace++;
                }
            }
        }
        isDirty = true;
    }


    public void setTitle(String title) {
        this.title.setTitle(title);
        isDirty = true;
    }

    public void setTraceColor(int trace, BColor color) {
        DataPainterTrace dataPainterTrace = generalTraceNumberToDataPainterTrace(trace);
        dataPainterTrace.getDataPainter().setTraceColor(dataPainterTrace.getTrace(), color);
    }

    public void setTraceName(int trace, String name) {
        DataPainterTrace dataPainterTrace = generalTraceNumberToDataPainterTrace(trace);
        dataPainterTrace.getDataPainter().setTraceName(dataPainterTrace.getTrace(), name);
        legend.setTraceName(dataPainterTrace, name);
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
     * @throws IllegalStateException if stack axis are used by some trace traces and
     *                               therefor can not be deleted
     */
    public void removeStack(int stack) throws IllegalStateException {
        // check that no trace use that stack
        int leftYIndex = stack * 2;
        int rightYIndex = stack * 2 + 1;

        for (DataPainter dataPainter : dataPainters) {
            for (int trace = 0; trace < dataPainter.traceCount(); trace++) {
                int traceYIndex = getTraceYIndex(dataPainter,  trace);
                if(traceYIndex == leftYIndex || traceYIndex == rightYIndex) {
                    String errMsg = "Stack: " + stack + "can not be removed. It is used by trace";
                    throw new IllegalStateException(errMsg);
                }
            }

            if(dataPainter.getYStartIndex() > leftYIndex) {
                dataPainter.setYStartIndex(dataPainter.getYStartIndex() - 2);
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
    public void addTraces(ChartData data, Trace tracePainter) {
        addTraces(data, tracePainter, true);
    }

    /**
     * add trace to the last stack
     */
    public void addTraces(ChartData data, Trace tracePainter, boolean isSplit) {
        int stack = Math.max(0, yAxisList.size() / 2 - 1);
        addTraces(data, tracePainter, isSplit, stack);
    }

    /**
     * add trace to the stack with the given number
     */
    public void addTraces(ChartData data, Trace tracePainter, boolean isSplit, int stack) {
        addTraces(data, tracePainter, isSplit, stack, config.getPrimaryXPosition(), config.getPrimaryYPosition());
    }

    public void addTraces(ChartData data, Trace tracePainter, boolean isSplit, XAxisPosition xPosition, YAxisPosition yPosition) {
        int stack = Math.max(0, yAxisList.size() / 2 - 1);
        addTraces(data, tracePainter, isSplit, stack, xPosition, yPosition);
    }

    /**
     * add trace to the stack with the given number
     */
    public void addTraces(ChartData data, Trace tracePainter, boolean isSplit, int stack, XAxisPosition xPosition, YAxisPosition yPosition) throws IllegalArgumentException {
        DataPainter dataPainter = new DataPainter(data, tracePainter, isSplit, dataProcessingConfig, getXIndex(xPosition), getYIndex(stack, yPosition));
        if (dataPainter.traceCount() < 1) {
            String errMsg = "Number of trace traces: " + dataPainter.traceCount() + ". Please specify valid trace data";
            throw new IllegalArgumentException(errMsg);
        }

        if (yAxisList.size() == 0) {
            addStack(); // add stack if there is no stack
        }
        checkStackNumber(stack);

        int xIndex = getXIndex(xPosition);
        int yIndex = getYIndex(stack, yPosition);

        AxisWrapper xAxis = xAxisList.get(xIndex);
        StringSequence dataLabels = dataPainter.getLabelsIfXColumnIsString();

        if (dataLabels != null && xAxis.getScale() instanceof CategoryScale) {
            CategoryScale scale = (CategoryScale) xAxis.getScale();
            StringSequence scaleLabels = scale.getLabels();
            if (scaleLabels == null) {
                scale.setLabels(dataLabels);
            }
        }

        xAxis.setUsed(true);
        if (!isSplit) {
            AxisWrapper yAxis = yAxisList.get(yIndex);
            yAxis.setUsed(true);
        } else {
            int stackCount = yAxisList.size() / 2;
            int availableStacks = stackCount - stack;
            if (dataPainter.traceCount() > availableStacks) {
                for (int i = 0; i < dataPainter.traceCount() - availableStacks; i++) {
                    addStack();
                }
            }
            for (int i = 0; i < dataPainter.traceCount(); i++) {
                AxisWrapper yAxis = yAxisList.get(yIndex + i * 2);
                yAxis.setUsed(true);
            }
        }

        BColor[] colors = config.getTraceColors();
        int totalTraces = 0;
        for (DataPainter trace1 : dataPainters) {
            totalTraces += trace1.traceCount();
        }
        for (int i = 0; i < dataPainter.traceCount(); i++) {
            int trace = totalTraces + i;
            if (dataPainter.getTraceColor(i) == null) {
                dataPainter.setTraceColor(i, colors[trace % colors.length]);
            }
            if (StringUtils.isNullOrBlank(dataPainter.getTraceName(i))) {
                dataPainter.setTraceName(i, "Trace" + trace);
            }

        }

        dataPainters.add(dataPainter);

        if (isLegendEnabled()) {
            for (int i = 0; i < dataPainter.traceCount(); i++) {
                final int traceNumber = i;
                legend.add(new DataPainterTrace(dataPainter, traceNumber));
            }
        }
        isDirty = true;
    }

    public void removeTrace(int trace) {
        DataPainterTrace dataPainterTrace = generalTraceNumberToDataPainterTrace(trace);
        DataPainter dataPainter = dataPainterTrace.getDataPainter();
        
        if (isLegendEnabled()) {
            legend.remove(dataPainterTrace);
        }
        dataPainter.hideTrace(dataPainterTrace.getTrace());
        // try to remove empty stack 
        int traceStartStack = dataPainter.getYStartIndex() / 2;
        try {
            removeStack(traceStartStack + dataPainter.traceCount());
        } catch (IllegalStateException ex) {
            // do nothing;
        }
        isDirty = true;
    }

    public void setArea(BRectangle area) {
        fullArea = area;
        title.setArea(area);
        isDirty = true;
    }

    public int traceCount() {
        return dataPainters.size();
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
     * return COPY of X axis legendConfig. To change axis legendConfig use setXConfig
     */
    public AxisConfig getXConfig(XAxisPosition xPosition) {
        return xAxisList.get(getXIndex(xPosition)).getConfig();
    }

    /**
     * return COPY of Y axis legendConfig. To change axis legendConfig use setYConfig
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
        double zoomedMin = zoomedScale.getMin();
        double zoomedMax = zoomedScale.getMax();
        setYMinMax(stack, yPosition, zoomedMin, zoomedMax);
    }

    public void zoomX(XAxisPosition xPosition, double zoomFactor) {
        Scale zoomedScale = xAxisList.get(getXIndex(xPosition)).zoom(zoomFactor);
        double zoomedMin = zoomedScale.getMin();
        double zoomedMax = zoomedScale.getMax();
        setXMinMax(xPosition, zoomedMin, zoomedMax);
    }

    public void translateY(int stack, YAxisPosition yPosition, int translation) {
        Scale translatedScale = yAxisList.get(getYIndex(stack, yPosition)).translate(translation);
        double translatedMin = translatedScale.getMin();
        double translatedMax = translatedScale.getMax();
        setYMinMax(stack, yPosition, translatedMin, translatedMax);
    }

    public void translateX(XAxisPosition xPosition, int translation) {
        Scale translatedScale = xAxisList.get(getXIndex(xPosition)).translate(translation);
        double translatedMin = translatedScale.getMin();
        double translatedMax = translatedScale.getMax();
        setXMinMax(xPosition, translatedMin, translatedMax);
    }


    public boolean selectTrace(int x, int y) {
        if (isLegendEnabled() && legend.selectItem(x, y)) {
            return true;
        }
        return false;
    }


    class Legend {
        private ButtonGroup buttonGroup;
        // only LinkedHashMap will iterate in the order in which the entries were put into the map
        private Map<SwitchButton, DataPainterTrace> buttonsToDataPainterTraces = new LinkedHashMap<>();

        private BRectangle legendArea;
        private int height;

        private LegendConfig legendConfig;
        private boolean isLegendDirty = true;

        public Legend(LegendConfig legendConfig) {
            this.legendConfig = legendConfig;
            this.buttonGroup = new ButtonGroup();
        }

        public DataPainterTrace getSelectedTrace() {
           return buttonsToDataPainterTraces.get(buttonGroup.getSelection());
        }

        public boolean isEnabled() {
            return legendConfig.isEnabled();
        }

        public int getHeight(BCanvas canvas) {
            if(!legendConfig.isAttachedToStacks()) {
                if(isLegendDirty) {
                    arrangeButtons(canvas);
                }
                return height;
            }
            return 0;
        }

        public boolean isAttachedToStacks() {
            return legendConfig.isAttachedToStacks();
        }

        public boolean isTop() {
            if(legendConfig.getVerticalAlign() == VerticalAlign.TOP) {
                return true;
            }
            return false;
        }

        public boolean isBottom() {
            if(legendConfig.getVerticalAlign() == VerticalAlign.BOTTOM) {
                return true;
            }
            return false;
        }

        public boolean selectItem(int x, int y) {
            for (SwitchButton button : buttonsToDataPainterTraces.keySet()){
                if(button.contains(x, y)) {
                    button.switchState();
                    return true;
                }
            }
            return false;
        }

        public  void setArea(BRectangle legendArea) {
            this.legendArea = legendArea;
            isLegendDirty = true;
        }

        public void setTraceName(DataPainterTrace dataPainterTrace, String name) {
            for (SwitchButton button : buttonsToDataPainterTraces.keySet()){
                if(buttonsToDataPainterTraces.get(button).equals(dataPainterTrace)) {
                    button.setLabel(name);
                    isLegendDirty = true;
                    return;
                }
            }
        }


        public void setConfig(LegendConfig legendConfig) {
            this.legendConfig = legendConfig;
            for (SwitchButton button : buttonsToDataPainterTraces.keySet()){
                button.setBackgroundColor(this.legendConfig.getBackgroundColor());
                button.setTextStyle(this.legendConfig.getTextStyle());
                button.setMargin(this.legendConfig.getPadding());
            }
            isLegendDirty = true;
        }

        public void add(DataPainterTrace dataPainterTrace) {
            // add trace legend button
            SwitchButton traceButton = new SwitchButton(dataPainterTrace.getDataPainter().getTraceName(dataPainterTrace.getTrace()));
            buttonsToDataPainterTraces.put(traceButton, dataPainterTrace);
            buttonGroup.add(traceButton);
            traceButton.setBackgroundColor(legendConfig.getBackgroundColor());
            traceButton.setTextStyle(legendConfig.getTextStyle());
            traceButton.setMargin(legendConfig.getPadding());
            isLegendDirty = true;
        }

        public void remove(DataPainterTrace dataPainterTrace) {
            for (SwitchButton button : buttonsToDataPainterTraces.keySet()){
                DataPainterTrace buttonDataPainterTrace = buttonsToDataPainterTraces.get(button);
                if(buttonDataPainterTrace.equals(dataPainterTrace)) {
                    buttonGroup.remove(button);
                    buttonsToDataPainterTraces.remove(button);
                    break;
                }
            }
            isLegendDirty = true;
        }

        private BRectangle getTraceTraceArea(DataPainterTrace dataPainterTrace) {
            if(!legendConfig.isAttachedToStacks()) {
                return legendArea;
            }

            Scale yScale = yAxisList.get(getTraceYIndex(dataPainterTrace.getDataPainter(), dataPainterTrace.getTrace())).getScale();
            return new BRectangle(legendArea.x, (int)yScale.getEnd(), legendArea.width, (int)yScale.getLength());
        }

        private void arrangeButtons(BCanvas canvas) {
            // only LinkedHashMap will iterate in the order in which the entries were put into the map
            Map<BRectangle, List<SwitchButton>> areaToButtons = new LinkedHashMap<>();

            for (SwitchButton button : buttonsToDataPainterTraces.keySet()) {
                DataPainterTrace dataPainterTrace = buttonsToDataPainterTraces.get(button);
                BRectangle traceArea = getTraceTraceArea(dataPainterTrace);
                List<SwitchButton> areaButtons = areaToButtons.get(traceArea);
                if(areaButtons == null) {
                    areaButtons = new ArrayList<>();
                    areaToButtons.put(traceArea, areaButtons);
                }
                areaButtons.add(button);
            }

            List<SwitchButton> lineButtons = new ArrayList<SwitchButton>();
            Insets margin = legendConfig.getMargin();
            for (BRectangle area : areaToButtons.keySet()) {
                List<SwitchButton> areaButtons = areaToButtons.get(area);
                height = 0;
                int width = 0;
                int x = area.x;
                int y = area.y;
                for (SwitchButton button : areaButtons) {
                    BRectangle btnArea = button.getBounds(canvas);
                    if(height == 0) {
                        height = btnArea.height;
                        lineButtons.clear();
                    }
                    if(lineButtons.size() > 0 && x + legendConfig.getInterItemSpace() + btnArea.width >= area.x + area.width - margin.left() - margin.right()) {
                        width += (lineButtons.size() - 1) * legendConfig.getInterItemSpace();
                        if(legendConfig.getHorizontalAlign() == HorizontalAlign.LEFT) {
                            moveButtons(lineButtons, margin.left(),0);
                        }
                        if(legendConfig.getHorizontalAlign() == HorizontalAlign.RIGHT) {
                            moveButtons(lineButtons, area.width - width - margin.right(),0);
                        }
                        if(legendConfig.getHorizontalAlign() == HorizontalAlign.CENTER) {
                            moveButtons(lineButtons, (area.width - width) / 2,0);
                        }

                        x = area.x;
                        y += btnArea.height + legendConfig.getInterLineSpace();
                        button.setLocation(x, y, canvas);

                        x += btnArea.width + legendConfig.getInterItemSpace();
                        height += btnArea.height + legendConfig.getInterLineSpace();
                        width = btnArea.width;
                        lineButtons.clear();
                        lineButtons.add(button);
                    } else {
                        button.setLocation(x, y, canvas);
                        x += legendConfig.getInterItemSpace() + btnArea.width;
                        width += btnArea.width;
                        lineButtons.add(button);
                    }
                }

                width += (lineButtons.size() - 1) * legendConfig.getInterItemSpace();
                if(legendConfig.getHorizontalAlign() == HorizontalAlign.LEFT) {
                    moveButtons(lineButtons, margin.left(),0);
                }
                if(legendConfig.getHorizontalAlign() == HorizontalAlign.RIGHT) {
                    moveButtons(lineButtons, area.width - width - margin.right(),0);
                }
                if(legendConfig.getHorizontalAlign() == HorizontalAlign.CENTER) {
                    moveButtons(lineButtons, (area.width - width) / 2,0);
                }

                if(legendConfig.getVerticalAlign() == VerticalAlign.TOP) {
                    moveButtons(lineButtons, 0, margin.top());
                }
                if(legendConfig.getVerticalAlign() == VerticalAlign.BOTTOM) {
                    moveButtons(lineButtons, 0, area.height - height - margin.bottom());
                }
                if(legendConfig.getVerticalAlign() == VerticalAlign.MIDDLE) {
                    moveButtons(lineButtons, 0, (area.height - height)/2);
                }
            }
            height += margin.top() + margin.bottom();
            isLegendDirty = false;
        }

        private void moveButtons(List<SwitchButton> buttons, int dx, int dy) {
            if(dx != 0 || dy != 0) {
                for (SwitchButton button : buttons) {
                    button.moveLocation(dx, dy);
                }
            }
        }

        public void draw(BCanvas canvas) {
            if (buttonsToDataPainterTraces.size() == 0) {
                return;
            }
            if(isLegendDirty) {
                arrangeButtons(canvas);
            }
            canvas.setTextStyle(legendConfig.getTextStyle());
            for (SwitchButton button : buttonsToDataPainterTraces.keySet()) {
                DataPainterTrace dataPainterTrace = buttonsToDataPainterTraces.get(button);
                button.setColor(dataPainterTrace.getDataPainter().getTraceColor(dataPainterTrace.getTrace()));
                button.draw(canvas);
            }
        }
    }
}
