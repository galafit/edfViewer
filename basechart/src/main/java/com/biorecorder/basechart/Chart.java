package com.biorecorder.basechart;

import com.biorecorder.basechart.axis.*;
import com.biorecorder.basechart.button.StateListener;
import com.biorecorder.basechart.button.ButtonGroup;
import com.biorecorder.basechart.graphics.*;
import com.biorecorder.basechart.scales.LinearScale;
import com.biorecorder.basechart.scales.Scale;
import com.biorecorder.basechart.themes.DarkTheme;
import com.biorecorder.basechart.traces.Trace;

import java.util.*;
import java.util.List;

/**
 * Created by hdablin on 24.03.17.
 */
public class Chart {
    private String title ;
    private int defaultWeight = 4;

    private HorizontalAlign legendHAlign = HorizontalAlign.RIGHT;
    private VerticalAlign legendVAlign = VerticalAlign.TOP;

    private boolean isLegendVisible = true;

    private boolean isMarginFixed = false;
    private Insets spacing = new Insets(0, 0, 10, 10);

    /*
 * 2 X-axis: 0(even) - BOTTOM and 1(odd) - TOP
 * 2 Y-axis for every section(stack): even - LEFT and odd - RIGHT;
 * All LEFT and RIGHT Y-axis are stacked.
 * If there is no trace associated with some axis... this axis is invisible.
 **/
    private List<AxisWrapper> xAxisList = new ArrayList<>(2);
    private List<AxisWrapper> yAxisList = new ArrayList<>();

    private boolean isLeftAxisPrimary = true;
    private boolean isBottomAxisPrimary = true;

    private ArrayList<Integer> stackWeights = new ArrayList<Integer>();

    private ChartConfig chartConfig = new ChartConfig();

    private List<Trace> traces = new ArrayList<Trace>();
    private List<Legend> legends = new ArrayList<Legend>();
    private Title titleText;
    private BRectangle fullArea;
    private BRectangle graphArea;
    private Insets margin;
    private DataManager dataManager;
    private boolean isYAxisRoundingEnabled = false;
 
    private Crosshair crosshair;
    private Tooltip tooltip;

    private int selectedTraceIndex = -1;
    private int hoverPointIndex = -1;
    private int hoverTraceIndex = -1;

    private ButtonGroup buttonGroup = new ButtonGroup();

    public Chart() {
        this((new DarkTheme()).getChartConfig());
    }

    public Chart(ChartConfig chartConfig) {
        this(chartConfig, new DataProcessingConfig());
    }


    public Chart(ChartConfig chartConfig1, DataProcessingConfig dataProcessingConfig) {
        this.chartConfig = new ChartConfig(chartConfig1);
        dataManager = new DataManager(dataProcessingConfig);
        AxisWrapper bottomAxis = new AxisWrapper(new AxisBottom(new LinearScale(), chartConfig.getBottomAxisConfig()));
        AxisWrapper topAxis = new AxisWrapper(new AxisTop(new LinearScale(), chartConfig.getTopAxisConfig()));
        bottomAxis.setRoundingEnabled(false);
        topAxis.setRoundingEnabled(false);
        topAxis.setRoundingAccuracyPct(-1);
        bottomAxis.setRoundingAccuracyPct(-1);
        if (isBottomAxisPrimary) {
            bottomAxis.setGridVisible(true);
        } else {
            topAxis.setGridVisible(true);
        }
        xAxisList.add(bottomAxis);
        xAxisList.add(topAxis);

        // tooltips
        tooltip = new Tooltip(chartConfig.getTooltipConfig());
        crosshair = new Crosshair(chartConfig.getCrossHairConfig());
    }

    double getBestExtent(int xIndex) {
        double maxExtent = 0;
        for (int i = 0; i < traces.size(); i++) {
            if (traces.get(i).getXAxisIndex() == xIndex) {
                maxExtent = Math.max(maxExtent, dataManager.getBestExtent(i, fullArea.width));
            }
        }
        return maxExtent;
    }


    // for all x axis
    BRange getAllTracesFullMinMax() {
        BRange minMax = null;
        for (int i = 0; i < traces.size(); i++) {
            minMax = BRange.join(minMax, dataManager.getTraceFullXMinMax(i));
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
        graphArea = null;
        margin = null;
    }
    
    private boolean isAreasDirty() {
        if(margin == null || graphArea == null) {
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
    
    void setMargin(Insets margin) {
        this.margin = margin;
        setYStartEnd(fullArea.y + margin.top(), fullArea.height - margin.top() - margin.bottom());
        setXStartEnd(fullArea.x + margin.left(), fullArea.width - margin.left() - margin.right());
        graphArea = new BRectangle(fullArea.x + margin.left(), fullArea.y + margin.top(),
                fullArea.width - margin.left() - margin.right(), fullArea.height - margin.top() - margin.bottom());

        titleText = null;
    }

    Scale getXAxisScale(int xIndex) {
        return xAxisList.get(xIndex).getScale();
    }


    void calculateMarginsAndAreas(BCanvas canvas) {
        if (titleText == null) {
            titleText = new Title(title, chartConfig.getTitleConfig(), fullArea, canvas);
        }

        int left = 0;
        int right = 0;
        int top = spacing.top();
        int bottom = spacing.bottom();


        top += titleText.getBounds().height + xAxisList.get(1).getWidth(canvas);
        bottom += xAxisList.get(0).getWidth(canvas);

        // if margins and graph area were not calculated before or
        // width of some xAxis was changed (never should happen if label rotation angle is 0)
        if (margin == null || margin.top() != top || margin.bottom() != bottom) {
            setYStartEnd(fullArea.y + top, fullArea.height - top - bottom);
        }

        for (int i = 0; i < yAxisList.size() / 2; i++) {
            left = Math.max(left, yAxisList.get(i * 2).getWidth(canvas));
        }
        left += spacing.left();
        for (int i = 0; i < yAxisList.size() / 2; i++) {
            right = Math.max(right, yAxisList.get(i * 2 + 1).getWidth(canvas));
        }
        right += spacing.right();

        // if margins and graph area were not calculated before or
        // width of some yAxis was changed
        if (margin == null || margin.left() != left || margin.right() != right) {
            // adjust XAxis ranges
            setXStartEnd(fullArea.x + left, fullArea.width - left - right);
            int topNew = spacing.top() + titleText.getBounds().height + xAxisList.get(1).getWidth(canvas);
            int bottomNew = spacing.bottom() + xAxisList.get(0).getWidth(canvas);
            if (topNew != top || bottomNew != bottom) {
                // adjust YAxis ranges
                setYStartEnd(fullArea.y + top, fullArea.height - top - bottom);
                top = topNew;
                bottom = bottomNew;
            }
        }

        Insets resultantMargin = new Insets(top, right, bottom, left);
        if (margin == null || !margin.equals(resultantMargin)) {
            margin = resultantMargin;
            graphArea = new BRectangle(fullArea.x + left, fullArea.y + top,
                    fullArea.width - left - right, fullArea.height - top - bottom);

            for (int stackIndex = 0; stackIndex < legends.size(); stackIndex++) {
                int legendAreaYStart = yAxisList.get(2 * stackIndex).getEnd() + chartConfig.getTopAxisConfig().getAxisLineStroke().getWidth();
                int legendAreaYEnd = yAxisList.get(2 * stackIndex).getStart() - chartConfig.getBottomAxisConfig().getAxisLineStroke().getWidth();
                int legendAreaXStart = graphArea.x + chartConfig.getLeftAxisConfig().getAxisLineStroke().getWidth();
                int legendAreaXEnd = graphArea.x + graphArea.width - chartConfig.getRightAxisConfig().getAxisLineStroke().getWidth();

                BRectangle legendArea = new BRectangle(legendAreaXStart, legendAreaYStart, legendAreaXEnd - legendAreaXStart, legendAreaYEnd - legendAreaYStart);
                legends.get(stackIndex).setArea(legendArea);
            }
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
            int axisHeight = areaHeight * yAxisWeight / weightSum;
            int end = areaY + areaHeight * weightSumTillYAxis / weightSum;
            int start = end + axisHeight;

            yAxisList.get(stack * 2).setStartEnd(start, end);
            yAxisList.get(stack * 2 + 1).setStartEnd(start, end);

            weightSumTillYAxis += stackWeights.get(stack);
        }
    }


    boolean isXAxisUsed(int xIndex) {
        for (Trace trace : traces) {
            if (trace.getXAxisIndex() == xIndex) {
                return true;
            }
        }
        return false;
    }

    private void removeTracesData(int xAxisIndex) {
        for (Trace trace : traces) {
            if(trace.getXAxisIndex() == xAxisIndex) {
                trace.removeData();
            }
        }
    }

    public void draw(BCanvas canvas) {
        if(titleText == null) {
            if (titleText == null) {
                titleText = new Title(title, chartConfig.getTitleConfig(), fullArea, canvas);
            }
        }

        if (isAreasDirty()) {
            calculateMarginsAndAreas(canvas);
        }
        for (int i = 0; i < traces.size(); i++) {
            Trace trace = traces.get(i);
            if(!trace.isDataSet()) {
                trace.setData(dataManager.getTraceData(i, xAxisList.get(trace.getXAxisIndex()).getScale()));
            }
        }

        canvas.setColor(chartConfig.getMarginColor());
        canvas.fillRect(fullArea.x, fullArea.y, fullArea.width, fullArea.height);

        canvas.setColor(chartConfig.getBackgroundColor());
        canvas.fillRect(graphArea.x, graphArea.y, graphArea.width, graphArea.height);

        canvas.enableAntiAliasAndHinting();

        int topPosition = graphArea.y;
        int bottomPosition = graphArea.y + graphArea.height;
        int leftPosition = graphArea.x;
        int rightPosition = graphArea.x + graphArea.width;

        /*
         * Attention!!!
         * Drawing  axis and grids should be done before drawing traces
         * because this methods invokes axis rounding
         */
        AxisWrapper bottomAxis = xAxisList.get(0);
        AxisWrapper topAxis = xAxisList.get(1);
        if(bottomAxis.isVisible && bottomAxis.isGridVisible) {
           bottomAxis.drawGrid(canvas, bottomPosition, graphArea.height);
        }
        if(topAxis.isVisible && topAxis.isGridVisible) {
            topAxis.drawGrid(canvas, topPosition, graphArea.height);
        }

        AxisWrapper leftAxis;
        AxisWrapper rightAxis;
        for (int i = 0; i < yAxisList.size() / 2; i++) {
            leftAxis = yAxisList.get(i * 2);
            rightAxis = yAxisList.get(i * 2 + 1);
            if(leftAxis.isVisible() && leftAxis.isGridVisible) {
                leftAxis.drawGrid(canvas, leftPosition, graphArea.width);
            }
            if(rightAxis.isVisible() && rightAxis.isGridVisible) {
                rightAxis.drawGrid(canvas, rightPosition, graphArea.width);
            }
        }

        if(bottomAxis.isVisible) {
            bottomAxis.drawAxis(canvas, bottomPosition);
        }
        if(topAxis.isVisible) {
            topAxis.drawAxis(canvas, topPosition);
        }

        for (int i = 0; i < yAxisList.size() / 2; i++) {
            leftAxis = yAxisList.get(i * 2);
            rightAxis = yAxisList.get(i * 2 + 1);
            if(leftAxis.isVisible()) {
                leftAxis.drawAxis(canvas, leftPosition);
            }
            if(rightAxis.isVisible()) {
                rightAxis.drawAxis(canvas, rightPosition);
            }
        }

        canvas.save();
        canvas.setClip(graphArea.x, graphArea.y, graphArea.width, graphArea.height);

        for (Trace trace : traces) {
            trace.draw(canvas, xAxisList.get(trace.getXAxisIndex()).getScale(), yAxisList.get(trace.getYAxisIndex()).getScale());
        }
        canvas.restore();

        titleText.draw(canvas);

        if (isLegendVisible) {
            for (Legend legend : legends) {
                legend.draw(canvas);
            }
        }

        if (hoverTraceIndex >= 0 && hoverPointIndex >= 0) {
            crosshair.draw(canvas, graphArea);
            tooltip.draw(canvas, fullArea);
        }
    }




    /**
     * =======================Base methods to interact==========================
     **/

    public void setDefaultWeight(int defaultWeight) {
        this.defaultWeight = defaultWeight;
    }

    public void setSpacing(int top, int right, int bottom, int left) {
        this.spacing = new Insets(top, right, bottom, left);
        setAreasDirty();
        titleText = null;
    }

    public void setFixedMargin(Insets margin) {
        if(margin != null) {
            isMarginFixed = true;
            setMargin(margin);
        } else {
            isMarginFixed = false;
            setAreasDirty();
            titleText = null;
        }
    }

    /**
     * if true all x axis will start and end on tick
     *
     */
    public void setXRoundingEnabled(boolean isRoundingEnabled) {
        for (AxisWrapper axis : xAxisList) {
            axis.setRoundingEnabled(isRoundingEnabled);
        }
        setAreasDirty();
    }

    /**
     * if true all y axis will start and end on tick
     *
     */
    public void setYRoundingEnabled(boolean isRoundingEnabled) {
        isYAxisRoundingEnabled = isRoundingEnabled;
        for (AxisWrapper axis : yAxisList) {
            axis.setRoundingEnabled(isRoundingEnabled);
        }
        setAreasDirty();
    }

    public void setXLabelsInside(boolean isInside) {
        for (AxisWrapper axis : xAxisList) {
            axis.setTickLabelInside(isInside);
        }
        setAreasDirty();
    }

    public void setYLabelsInside(boolean isInside) {
        for (AxisWrapper axis : yAxisList) {
            axis.setTickLabelInside(isInside);
        }
        setAreasDirty();
    }

    public ChartConfig getConfig() {
        return chartConfig;
    }

    public void setConfig(ChartConfig chartConfig1) {
        this.chartConfig = new ChartConfig(chartConfig1);
        // axis
        for (int i = 0; i < xAxisList.size(); i++) {
            if (isXAxisBottom(i)) {
                xAxisList.get(i).setConfig(chartConfig.getBottomAxisConfig());
            } else {
                xAxisList.get(i).setConfig(chartConfig.getTopAxisConfig());
            }
        }

        for (int i = 0; i < yAxisList.size(); i++) {
            if (isYAxisLeft(i)) {
                yAxisList.get(i).setConfig(chartConfig.getLeftAxisConfig());
            } else {
                yAxisList.get(i).setConfig(chartConfig.getRightAxisConfig());
            }
        }
        // tooltips
        tooltip = new Tooltip(chartConfig.getTooltipConfig());
        crosshair = new Crosshair(chartConfig.getCrossHairConfig());

        //legends
        for (Legend legend : legends) {
            legend.setConfig(chartConfig.getLegendConfig());
        }

        int colorsCount = chartConfig.getTraceColors().length;
        for (int i = 0; i < traces.size(); i++) {
            BColor traceColor = chartConfig.getTraceColors()[i % colorsCount];
            traces.get(i).setMainColor(traceColor);
            int stackNumber = traces.get(i).getYAxisIndex() / 2;
            legends.get(stackNumber).getButton(i).setColor(traceColor);
        }

        // title
        titleText = null;
        setAreasDirty();
    }


    public void addStack(int weight) {
        AxisWrapper leftAxis = new AxisWrapper(new AxisLeft(new LinearScale(), chartConfig.getLeftAxisConfig()));
        AxisWrapper rightAxis = new AxisWrapper(new AxisRight(new LinearScale(), chartConfig.getRightAxisConfig()));
        leftAxis.setRoundingEnabled(isYAxisRoundingEnabled);
        rightAxis.setRoundingEnabled(isYAxisRoundingEnabled);
        if (isLeftAxisPrimary) {
            leftAxis.setGridVisible(true);
        } else {
            rightAxis.setGridVisible(true);
        }
        yAxisList.add(leftAxis);
        yAxisList.add(rightAxis);
        stackWeights.add(weight);

        legends.add(new Legend(chartConfig.getLegendConfig(), legendHAlign, legendVAlign, buttonGroup));
        setAreasDirty();
    }

    public void addStack() {
        addStack(defaultWeight);
    }


    /**
     * add trace to the stack with the given number
     *
     * @param stackNumber
     * @param trace
     * @param traceData
     * @param isXAxisOpposite
     * @param isYAxisOpposite
     */
    public void addTrace(int stackNumber, Trace trace, ChartData traceData, boolean isXAxisOpposite, boolean isYAxisOpposite) {
        boolean isBottomXAxis = true;
        boolean isLeftYAxis = true;
        if (isXAxisOpposite && isBottomAxisPrimary) {
            isBottomXAxis = false;
        }
        if (!isXAxisOpposite && !isBottomAxisPrimary) {
            isBottomXAxis = false;
        }
        if (isYAxisOpposite && isLeftAxisPrimary) {
            isLeftYAxis = false;
        }
        if (!isYAxisOpposite && !isLeftAxisPrimary) {
            isLeftYAxis = false;
        }
        int xIndex = isBottomXAxis ? 0 : 1;
        int yIndex = isLeftYAxis ? stackNumber * 2 : stackNumber * 2 + 1;
        trace.setAxes(xIndex, yIndex);

        yAxisList.get(yIndex).setVisible(true);
        xAxisList.get(xIndex).setVisible(true);

        if (trace.getName() == null) {
            trace.setName("Trace " + traces.size());
        }
        if (trace.getMainColor() == null) {
            BColor[] traceColors = chartConfig.getTraceColors();
            trace.setMainColor(traceColors[traces.size() % traceColors.length]);
        }

        dataManager.addTrace(traceData, trace.getMarkSize());
        traces.add(trace);

        final int traceIndex = traces.size() - 1;
        StateListener traceSelectionListener = new StateListener() {
            @Override
            public void stateChanged(boolean isSelected) {
                if (isSelected) {
                    selectedTraceIndex = traceIndex;
                }
                if (!isSelected && selectedTraceIndex == traceIndex) {
                    selectedTraceIndex = -1;
                }
            }
        };
        legends.get(stackNumber).add(trace, traceSelectionListener);
    }


    public void setArea(BRectangle area) {
        fullArea = area;
        for (Trace trace : traces) {
            trace.removeData();
        }
        setXStartEnd(area.x, area.width);
        setYStartEnd(area.y, area.height);
        setAreasDirty();
        titleText = null;
    }

    public int traceCount() {
        return traces.size();
    }

    public boolean selectTrace(int x, int y) {
        for (Legend legend : legends) {
            if (legend.selectItem(x, y)) {
                return true;
            }
        }
        return false;
    }

    public boolean selectTrace(int traceIndex) {
        if (selectedTraceIndex != traceIndex) {
            selectedTraceIndex = traceIndex;
            return true;
        }
        return false;
    }

    public int getSelectedTraceIndex() {
        return selectedTraceIndex;
    }


    public int xAxisCount() {
        return xAxisList.size();
    }

    public int yAxisCount() {
        return yAxisList.size();
    }


    public void setXMinMax(int xIndex, double min, double max) {
        if(xAxisList.get(xIndex).setMinMax(min, max)) {
            removeTracesData(xIndex);
            if(! xAxisList.get(xIndex).isTickLabelInside() || !isMarginFixed) {
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
        if(! yAxisList.get(yAxisIndex).isTickLabelInside() || !isMarginFixed) {
            setAreasDirty();
        }
    }


    public RangeInt getYStartEnd(int yAxisIndex) {
        return new RangeInt(yAxisList.get(yAxisIndex).getEnd(), yAxisList.get(yAxisIndex).getStart());
    }

    public int getXIndex(int x, int y) {
        if (fullArea.y + fullArea.height / 2 <= y) {
            for (int i = 0; i < traces.size(); i++) {
                if (getTraceXIndex(i) == 0) {
                    return 0; // bottom Axis
                }
            }
            return 1; // top Axis
        }
        if (fullArea.y <= y && y <= fullArea.y + fullArea.height / 2) {
            for (int i = 0; i < traces.size(); i++) {
                if (getTraceXIndex(i) == 1) {
                    return 1; // top Axis
                }
            }
            return 0; // bottom Axis
        }
        return -1;
    }

    /**
     * Find and return Y axis used by the traces belonging to the stack containing point (x, y)
     */
    public int getYIndex(int x, int y) {
        for (int stackIndex = 0; stackIndex < yAxisList.size() / 2; stackIndex++) {
            if (yAxisList.get(2 * stackIndex).getEnd() <= y && yAxisList.get(2 * stackIndex).getStart() >= y) {
                int stackLeftYAxisIndex = 2 * stackIndex;
                int stackRightYAxisIndex = 2 * stackIndex + 1;
                if (fullArea.x <= x && x <= fullArea.x + fullArea.width / 2) { // left half
                    for (int i = 0; i < traces.size(); i++) {
                        if (getTraceYIndex(i) == stackLeftYAxisIndex) { // if leftAxis is used by some trace
                            return stackLeftYAxisIndex;
                        }
                    }
                    return stackRightYAxisIndex;
                }
                if (fullArea.x + fullArea.width / 2 <= x && x <= fullArea.x + fullArea.width) { // right half
                    for (int i = 0; i < traces.size(); i++) {
                        if (getTraceYIndex(i) == stackRightYAxisIndex) { // if rightAxis is used by some trace
                            return stackRightYAxisIndex;
                        }
                    }
                    return stackLeftYAxisIndex;
                }
            }
        }
        return -1;
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
        BRange tracesXMinMax = null;
        for (int i = 0; i < traces.size(); i++) {
            if (traces.get(i).getXAxisIndex() == xIndex) {
                tracesXMinMax = BRange.join(tracesXMinMax, dataManager.getTraceFullXMinMax(i));
            }
        }
        if (tracesXMinMax != null) {
            setXMinMax(xIndex, tracesXMinMax.getMin(), tracesXMinMax.getMax());
        }
    }

    public void autoScaleY(int yIndex) {
        for (int i = 0; i < traces.size(); i++) {
            Trace trace = traces.get(i);
            if(trace.getYAxisIndex() == yIndex && !trace.isDataSet()) {
                trace.setData(dataManager.getTraceData(i, xAxisList.get(trace.getXAxisIndex()).getScale()));
            }
        }
        BRange tracesYMinMax = null;
        for (int i = 0; i < traces.size(); i++) {
            if (getTraceYIndex(i) == yIndex) {
                tracesYMinMax = BRange.join(tracesYMinMax, traces.get(i).getYExtremes());
            }
        }
        if (tracesYMinMax != null) {
            setYMinMax(yIndex, tracesYMinMax.getMin(), tracesYMinMax.getMax());
        }
    }

    public int getTraceYIndex(int traceIndex) {
        return traces.get(traceIndex).getYAxisIndex();
    }

    public int getTraceXIndex(int traceIndex) {
        return traces.get(traceIndex).getXAxisIndex();
    }


    public boolean hoverOff() {
        if (hoverPointIndex >= 0) {
            hoverPointIndex = -1;
            return true;
        }
        return false;
    }


    public boolean hoverOn(int x, int y, int traceIndex) {
        if (!graphArea.contains(x, y)) {
            if(hoverOff()) {
                return true;
            }
            return false;
        }
        if (traceIndex >= 0) {
            hoverTraceIndex = traceIndex;
        } else {
            for (int stackIndex = 0; stackIndex < yAxisList.size() / 2; stackIndex++) {
                if (yAxisList.get(2 * stackIndex).getEnd() <= y && yAxisList.get(2 * stackIndex).getStart() >= y) {
                    for (int i = 0; i < traces.size(); i++) {
                        if (getTraceYIndex(i) == 2 * stackIndex || getTraceYIndex(i) == 2 * stackIndex + 1) {
                            hoverTraceIndex = i;
                            break;
                        }
                    }
                }
            }
        }

        if (hoverTraceIndex >= 0) {
            Trace trace = traces.get(hoverTraceIndex);
            Scale xScale = xAxisList.get(trace.getXAxisIndex()).getScale();
            Scale yScale = yAxisList.get(trace.getYAxisIndex()).getScale();

            int nearestIndex = (int) trace.nearest(x, y, xScale, yScale);
            if (hoverPointIndex != nearestIndex) {
                hoverPointIndex = nearestIndex;
                if (hoverPointIndex >= 0) {
                    TooltipInfo tooltipInfo = new TooltipInfo();
                    tooltipInfo.addItems(traces.get(hoverTraceIndex).getInfo(hoverPointIndex, xScale, yScale));
                    BPoint dataPosition = traces.get(hoverTraceIndex).getDataPosition(hoverPointIndex, xScale, yScale);
                    tooltip.setTooltipInfo(tooltipInfo);
                    tooltip.setXY(dataPosition.getX(), yAxisList.get(getTraceYIndex(hoverTraceIndex)).getEnd());
                    crosshair.setXY(dataPosition.getX(), dataPosition.getY());
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Implement axis rounding when method:
     * drawAxis or drawGrid or getWidth is invoked !!!
     */
    class AxisWrapper {
        private Axis axis;
        private boolean isVisible = false;
        private boolean isGridVisible = false;
        private boolean isRoundingEnabled = false;
        // need this field to implement smooth zooming and translate when minMaxRounding enabled
        private BRange rowMinMax; // without rounding
        private boolean roundingDirty = true;


        public AxisWrapper(Axis axis) {
            this.axis = axis;
            rowMinMax = new BRange(axis.getMin(), axis.getMax());
        }

        private void setRoundingDirty() {
            roundingDirty = true;
            axis.setMinMax(rowMinMax);
        }

        public void setRoundingAccuracyPct(int roundingAccuracyPct) {
            axis.setRoundingAccuracyPct(roundingAccuracyPct);
            setRoundingDirty();
        }

        private boolean isDirty() {
            if (isRoundingEnabled && roundingDirty) {
                return true;
            }
            return false;
        }

        public void setRoundingEnabled(boolean roundingEnabled) {
            isRoundingEnabled = roundingEnabled;
            setRoundingDirty();
        }

        public void setTickLabelInside(boolean tickLabelInside) {
            axis.setTickLabelInside(tickLabelInside);
        }

        public boolean isTickLabelInside() {
            return axis.isTickLabelInside();
        }
        

        public void setTitle(String title) {
            axis.setTitle(title);
        }

        public void setTickInterval(double tickInterval) {
            axis.setTickInterval(tickInterval);
            setRoundingDirty();
        }

        public void setMinorTickIntervalCount(int minorTickIntervalCount) {
            axis.setMinorTickIntervalCount(minorTickIntervalCount);
        }

        public void setTickFormatInfo(TickFormatInfo tickFormatInfo) {
            axis.setTickFormatInfo(tickFormatInfo);
            setRoundingDirty();
        }

        public void setScale(Scale scale) {
            axis.setScale(scale);
        }

        public Scale getScale() {
            return axis.getScale();
        }


        public void setConfig(AxisConfig config) {
            axis.setConfig(config);
            setRoundingDirty();
        }


        public Scale zoom(double zoomFactor) {
            // to have smooth zooming we do it on row domain values instead of rounded ones !!!
            axis.setMinMax(rowMinMax);
            return axis.zoom(zoomFactor);
        }


        public Scale translate(int translation) {
            // to have smooth translating we do it on row domain values instead of rounded ones !!!
            axis.setMinMax(rowMinMax);
            Scale scale = axis.translate(translation);
            return scale;
        }

        /**
         * return true if axis min or max actually will be changed
         */
        public boolean setMinMax(double min, double max) {
            if(rowMinMax.getMin() != min || rowMinMax.getMax() != max) {
                rowMinMax = new BRange(min, max);
                setRoundingDirty();
                return true;
            }
            return false;
        }

        /**
         * return true if axis start or end actually changed
         */
        public boolean setStartEnd(int start, int end) {
            if((int)axis.getStart() != start || (int) axis.getEnd() != end) {
                setRoundingDirty();
                axis.setStartEnd(start, end);
                return true;
            }
            return false;
        }

        public double getMin() {
            return axis.getMin();
        }

        public double getMax() {
            return axis.getMax();
        }

        public int getStart() {
            return (int) axis.getStart();
        }

        public int getEnd() {
            return (int) axis.getEnd();
        }

        public double scale(double value) {
            return axis.scale(value);
        }

        public double invert(float value) {
            return axis.invert(value);
        }


        public boolean isVisible() {
            return isVisible;
        }

        public boolean isGridVisible() {
            return isGridVisible;
        }

        /**
         * this method DO AXIS ROUNDING
         */
        public int getWidth(BCanvas canvas) {
            if(isVisible) {
                if (isDirty()) {
                    axis.roundMinMax(canvas);
                    roundingDirty = false;
                }
                return axis.getWidth(canvas);
            }
            return 0;
        }

        /**
         * this method DO AXIS ROUNDING
         */
        public void drawGrid(BCanvas canvas, int axisOriginPoint, int length) {
            if (isDirty()) {
                axis.roundMinMax(canvas);
                roundingDirty = false;
            }
            axis.drawGrid(canvas, axisOriginPoint, length);
        }

        /**
         * this method DO AXIS ROUNDING
         */
        public void drawAxis(BCanvas canvas, int axisOriginPoint) {
            if (isDirty()) {
                axis.roundMinMax(canvas);
                roundingDirty = false;
            }
            axis.drawAxis(canvas, axisOriginPoint);
        }

        public void setVisible(boolean visible) {
            isVisible = visible;
        }

        public void setGridVisible(boolean gridVisible) {
            isGridVisible = gridVisible;
        }
    }
}
